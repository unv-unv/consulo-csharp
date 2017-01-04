/*
 * Copyright 2013-2017 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.csharp.ide.actions.generate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.actions.generate.memberChoose.CSharpVariableChooseObject;
import consulo.csharp.ide.actions.generate.memberChoose.ConstructorChooseMember;
import consulo.csharp.lang.psi.CSharpFieldDeclaration;
import consulo.csharp.lang.psi.CSharpFileFactory;
import consulo.csharp.lang.psi.CSharpPropertyDeclaration;
import consulo.csharp.lang.psi.CSharpTypeDeclaration;
import consulo.csharp.lang.psi.impl.resolve.CSharpResolveContextUtil;
import consulo.csharp.lang.psi.impl.source.CSharpTypeDeclarationImplUtil;
import consulo.csharp.lang.psi.impl.source.resolve.AsPsiElementProcessor;
import consulo.csharp.lang.psi.impl.source.resolve.CSharpResolveOptions;
import consulo.csharp.lang.psi.impl.source.resolve.ExecuteTarget;
import consulo.csharp.lang.psi.impl.source.resolve.MemberResolveScopeProcessor;
import consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import consulo.csharp.lang.psi.resolve.CSharpElementGroup;
import consulo.csharp.lang.psi.resolve.CSharpResolveContext;
import consulo.csharp.lang.psi.resolve.StaticResolveSelectors;
import consulo.dotnet.psi.DotNetConstructorDeclaration;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.DotNetVariable;
import consulo.dotnet.resolve.DotNetGenericExtractor;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.ide.util.MemberChooser;
import com.intellij.ide.util.MemberChooserBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.ResolveState;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.Function;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 25.06.14
 */
public class GenerateConstructorHandler implements CodeInsightActionHandler
{
	@RequiredDispatchThread
	@Override
	public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file)
	{
		PsiDocumentManager.getInstance(project).commitAllDocuments();

		final CSharpTypeDeclaration typeDeclaration = CSharpGenerateAction.findTypeDeclaration(editor, file);
		if(typeDeclaration == null)
		{
			return;
		}

		Pair<DotNetTypeDeclaration, DotNetGenericExtractor> pair = CSharpTypeDeclarationImplUtil.resolveBaseType(typeDeclaration, typeDeclaration);
		if(pair == null)
		{
			return;
		}
		final DotNetTypeDeclaration baseType = pair.getFirst();

		if(!(baseType instanceof CSharpTypeDeclaration))
		{
			return;
		}

		AsPsiElementProcessor psiElementProcessor = new AsPsiElementProcessor();
		MemberResolveScopeProcessor memberResolveScopeProcessor = new MemberResolveScopeProcessor(CSharpResolveOptions.build().element(typeDeclaration), psiElementProcessor,
				new ExecuteTarget[]{ExecuteTarget.ELEMENT_GROUP});

		ResolveState resolveState = ResolveState.initial();
		resolveState = resolveState.put(CSharpResolveUtil.SELECTOR, StaticResolveSelectors.CONSTRUCTOR_GROUP);
		resolveState = resolveState.put(CSharpResolveUtil.EXTRACTOR, pair.getSecond());

		CSharpResolveUtil.walkChildren(memberResolveScopeProcessor, baseType, false, false, resolveState);

		List<ConstructorChooseMember> members = new ArrayList<ConstructorChooseMember>();
		for(PsiElement psiElement : psiElementProcessor.getElements())
		{
			if(psiElement instanceof CSharpElementGroup)
			{
				for(PsiElement element : ((CSharpElementGroup<?>) psiElement).getElements())
				{
					members.add(new ConstructorChooseMember((DotNetConstructorDeclaration) element));
				}
			}
		}

		final ConstructorChooseMember[] map = ContainerUtil.toArray(members, ConstructorChooseMember.ARRAY_FACTORY);

		if(map.length == 1)
		{
			generateConstructor(typeDeclaration, editor, file, map[0]);
		}
		else
		{
			final MemberChooserBuilder<ConstructorChooseMember> builder = new MemberChooserBuilder<ConstructorChooseMember>(project);
			builder.setTitle("Choose Constructor");
			builder.allowMultiSelection(true);

			final MemberChooser<ConstructorChooseMember> memberChooser = builder.createBuilder(map);

			if(!memberChooser.showAndGet())
			{
				return;
			}

			final List<ConstructorChooseMember> selectedElements = memberChooser.getSelectedElements();
			if(selectedElements == null)
			{
				return;
			}

			for(ConstructorChooseMember selectedElement : selectedElements)
			{
				generateConstructor(typeDeclaration, editor, file, selectedElement);
			}
		}
	}

	@RequiredReadAction
	private static void generateConstructor(@NotNull final CSharpTypeDeclaration typeDeclaration,
			@NotNull final Editor editor,
			@NotNull final PsiFile file,
			@NotNull ConstructorChooseMember chooseMember)
	{
		CSharpResolveContext context = CSharpResolveContextUtil.createContext(DotNetGenericExtractor.EMPTY, typeDeclaration.getResolveScope(), typeDeclaration);
		final List<DotNetVariable> fieldOrProperties = new ArrayList<DotNetVariable>();
		context.processElements(new Processor<PsiElement>()
		{
			@Override
			public boolean process(PsiElement psiElement)
			{
				if(psiElement instanceof CSharpElementGroup)
				{
					((CSharpElementGroup) psiElement).process(this);
				}

				if(psiElement instanceof CSharpFieldDeclaration || psiElement instanceof CSharpPropertyDeclaration)
				{
					fieldOrProperties.add((DotNetVariable) psiElement);
				}
				return true;
			}
		}, false);

		List<CSharpVariableChooseObject> additionalParameters = Collections.emptyList();
		if(!fieldOrProperties.isEmpty())
		{
			final MemberChooserBuilder<CSharpVariableChooseObject> builder = new MemberChooserBuilder<CSharpVariableChooseObject>(typeDeclaration.getProject());
			builder.setTitle("Choose Fields or Properties");
			builder.allowMultiSelection(true);
			builder.allowEmptySelection(true);

			List<CSharpVariableChooseObject> map = ContainerUtil.map(fieldOrProperties, new Function<DotNetVariable, CSharpVariableChooseObject>()
			{
				@Override
				public CSharpVariableChooseObject fun(DotNetVariable variable)
				{
					return new CSharpVariableChooseObject(variable);
				}
			});

			MemberChooser<CSharpVariableChooseObject> fieldChooser = builder.createBuilder(ContainerUtil.toArray(map, CSharpVariableChooseObject.ARRAY_FACTORY));

			if(!fieldChooser.showAndGet())
			{
				return;
			}

			additionalParameters = fieldChooser.getSelectedElements();
			assert additionalParameters != null;
		}

		String text = chooseMember.getText(additionalParameters);
		text = text.replace("$NAME$", typeDeclaration.getName());

		final DotNetLikeMethodDeclaration method = CSharpFileFactory.createMethod(typeDeclaration.getProject(), text);

		final int offset = editor.getCaretModel().getOffset();
		final PsiElement elementAt = file.findElementAt(offset);
		assert elementAt != null;

		new WriteCommandAction.Simple<Object>(file.getProject(), file)
		{
			@Override
			protected void run() throws Throwable
			{
				final PsiElement psiElement = typeDeclaration.addAfter(method, elementAt);
				typeDeclaration.addAfter(PsiParserFacade.SERVICE.getInstance(file.getProject()).createWhiteSpaceFromText("\n"), psiElement);

				PsiDocumentManager.getInstance(getProject()).commitDocument(editor.getDocument());

				CodeStyleManager.getInstance(getProject()).reformat(psiElement);
			}
		}.execute();
	}

	@Override
	public boolean startInWriteAction()
	{
		return false;
	}
}
