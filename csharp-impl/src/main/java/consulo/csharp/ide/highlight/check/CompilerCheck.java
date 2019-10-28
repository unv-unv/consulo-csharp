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

package consulo.csharp.ide.highlight.check;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import consulo.annotations.RequiredReadAction;
import consulo.application.ApplicationProperties;
import consulo.csharp.ide.CSharpElementPresentationUtil;
import consulo.csharp.ide.CSharpErrorBundle;
import consulo.csharp.ide.highlight.CSharpHighlightContext;
import consulo.csharp.lang.psi.CSharpLocalVariable;
import consulo.csharp.lang.psi.CSharpTypeRefPresentationUtil;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.ide.DotNetElementPresentationUtil;
import consulo.dotnet.psi.*;
import consulo.dotnet.resolve.DotNetTypeRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public abstract class CompilerCheck<T extends PsiElement>
{
	public static interface HighlightInfoFactory
	{
		@Nullable
		public abstract HighlightInfo create(boolean insideDoc);

		@Nonnull
		default List<IntentionAction> getQuickFixes()
		{
			return Collections.emptyList();
		}
	}

	public static class CompilerCheckBuilder implements HighlightInfoFactory
	{
		private String myText;
		private TextRange myTextRange;
		private HighlightInfoType myHighlightInfoType;
		private TextAttributesKey myTextAttributesKey;

		private List<IntentionAction> myQuickFixes = Collections.emptyList();

		public TextRange getTextRange()
		{
			return myTextRange;
		}

		public CompilerCheckBuilder setTextRange(TextRange textRange)
		{
			myTextRange = textRange;
			return this;
		}

		public String getText()
		{
			return myText;
		}

		public CompilerCheckBuilder setText(String text)
		{
			myText = text;
			return this;
		}

		public HighlightInfoType getHighlightInfoType()
		{
			return myHighlightInfoType;
		}

		public TextAttributesKey getTextAttributesKey()
		{
			return myTextAttributesKey;
		}

		public CompilerCheckBuilder setTextAttributesKey(TextAttributesKey textAttributesKey)
		{
			myTextAttributesKey = textAttributesKey;
			return this;
		}

		public CompilerCheckBuilder setHighlightInfoType(HighlightInfoType highlightInfoType)
		{
			myHighlightInfoType = highlightInfoType;
			return this;
		}

		public CompilerCheckBuilder addQuickFix(IntentionAction a)
		{
			if(myQuickFixes.isEmpty())
			{
				myQuickFixes = new ArrayList<>(3);
			}
			myQuickFixes.add(a);
			return this;
		}

		@Override
		@Nonnull
		public List<IntentionAction> getQuickFixes()
		{
			return myQuickFixes;
		}

		@Nullable
		@Override
		public HighlightInfo create(boolean insideDoc)
		{
			HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(insideDoc ? HighlightInfoType.WEAK_WARNING : getHighlightInfoType());
			builder = builder.descriptionAndTooltip(getText());
			builder = builder.range(getTextRange());

			TextAttributesKey textAttributesKey = getTextAttributesKey();
			if(textAttributesKey != null)
			{
				builder = builder.textAttributes(textAttributesKey);
			}
			return builder.create();
		}
	}

	@Nonnull
	@RequiredReadAction
	public List<? extends HighlightInfoFactory> check(@Nonnull CSharpLanguageVersion languageVersion, @Nonnull CSharpHighlightContext highlightContext, @Nonnull T element)
	{
		HighlightInfoFactory check = checkImpl(languageVersion, highlightContext, element);
		if(check == null)
		{
			return Collections.emptyList();
		}
		return Collections.singletonList(check);
	}

	@Nullable
	@RequiredReadAction
	public HighlightInfoFactory checkImpl(@Nonnull CSharpLanguageVersion languageVersion, @Nonnull CSharpHighlightContext highlightContext, @Nonnull T element)
	{
		return null;
	}

	@Nonnull
	@RequiredReadAction
	public CompilerCheckBuilder newBuilder(@Nonnull PsiElement range, String... args)
	{
		return newBuilderImpl(getClass(), range, args);
	}

	@Nonnull
	@RequiredReadAction
	public CompilerCheckBuilder newBuilder(@Nonnull TextRange range, String... args)
	{
		return newBuilderImpl(getClass(), range, args);
	}

	@Nonnull
	@RequiredReadAction
	public static CompilerCheckBuilder newBuilderImpl(@Nonnull Class<?> clazz, @Nonnull PsiElement range, String... args)
	{
		return newBuilderImpl(clazz, range.getTextRange(), args);
	}

	@Nonnull
	@RequiredReadAction
	public static CompilerCheckBuilder newBuilderImpl(@Nonnull Class<?> clazz, @Nonnull TextRange range, String... args)
	{
		CompilerCheckBuilder result = new CompilerCheckBuilder();
		result.setText(message(clazz, args));
		result.setTextRange(range);
		return result;
	}

	@Nonnull
	public static String message(@Nonnull Class<?> aClass, String... args)
	{
		String id = aClass.getSimpleName();
		String message = CSharpErrorBundle.message(id, args);
		if(ApplicationProperties.isInSandbox())
		{
			message = id + ": " + message;
		}
		return message;
	}

	@Nonnull
	@RequiredReadAction
	public static PsiElement getNameIdentifier(PsiElement element)
	{
		if(element instanceof PsiNameIdentifierOwner)
		{
			PsiElement nameIdentifier = ((PsiNameIdentifierOwner) element).getNameIdentifier();
			if(nameIdentifier != null)
			{
				return nameIdentifier;
			}
		}
		return element;
	}

	@RequiredReadAction
	@Nullable
	public static String formatElement(PsiElement e)
	{
		if(e instanceof DotNetParameter)
		{
			return ((DotNetParameter) e).getName();
		}
		else if(e instanceof DotNetGenericParameter)
		{
			return ((DotNetGenericParameter) e).getName();
		}
		else if(e instanceof CSharpLocalVariable)
		{
			return ((CSharpLocalVariable) e).getName();
		}
		else if(e instanceof DotNetXAccessor)
		{
			PsiElement parent = e.getParent();
			return formatElement(parent) + "." + ((DotNetXAccessor) e).getAccessorKind().name().toLowerCase(Locale.US);
		}

		String parentName = null;
		PsiElement parent = e.getParent();
		if(parent instanceof DotNetNamespaceDeclaration)
		{
			parentName = ((DotNetNamespaceDeclaration) parent).getPresentableQName();
		}
		else if(parent instanceof DotNetTypeDeclaration)
		{
			parentName = DotNetElementPresentationUtil.formatTypeWithGenericParameters((DotNetTypeDeclaration) parent);
		}

		String currentText = "Unknown element : " + e.getClass().getSimpleName();
		if(e instanceof DotNetLikeMethodDeclaration)
		{
			currentText = CSharpElementPresentationUtil.formatMethod((DotNetLikeMethodDeclaration) e, 0);
		}
		else if(e instanceof DotNetTypeDeclaration)
		{
			currentText = DotNetElementPresentationUtil.formatTypeWithGenericParameters((DotNetTypeDeclaration) e);
		}
		else if(e instanceof DotNetVariable && e instanceof DotNetQualifiedElement)
		{
			currentText = ((DotNetQualifiedElement) e).getName();
		}

		if(StringUtil.isEmpty(parentName))
		{
			return currentText;
		}
		else
		{
			return parentName + "." + currentText;
		}
	}

	@RequiredReadAction
	public static String formatTypeRef(@Nonnull DotNetTypeRef typeRef, @Nonnull PsiElement scope)
	{
		return CSharpTypeRefPresentationUtil.buildTextWithKeywordAndNull(typeRef, scope);
	}
}
