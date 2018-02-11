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

package consulo.csharp.ide.actions.generate.memberChoose;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiElement;
import com.intellij.util.PairConsumer;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.CSharpElementPresentationUtil;
import consulo.csharp.ide.completion.expected.ExpectedUsingInfo;
import consulo.csharp.lang.psi.CSharpAccessModifier;
import consulo.csharp.lang.psi.CSharpMethodDeclaration;

/**
 * @author VISTALL
 * @since 16.12.14
 */
public class MethodChooseMember extends ImplementMemberChooseObject<CSharpMethodDeclaration>
{
	public MethodChooseMember(CSharpMethodDeclaration declaration,
			PairConsumer<PsiElement, StringBuilder> additionalModifiersAppender,
			PairConsumer<PsiElement, StringBuilder> returnAppender,
			boolean canGenerateBlock)
	{
		super(declaration, additionalModifiersAppender, returnAppender, canGenerateBlock);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	@RequiredDispatchThread
	public String getPresentationText()
	{
		return CSharpElementPresentationUtil.formatMethod(myDeclaration, CSharpElementPresentationUtil.METHOD_WITH_RETURN_TYPE | CSharpElementPresentationUtil.METHOD_PARAMETER_NAME);
	}

	@Override
	@RequiredDispatchThread
	public String getText()
	{
		StringBuilder builder = new StringBuilder();
		CSharpAccessModifier modifier = CSharpAccessModifier.findModifier(myDeclaration);
		if(modifier != CSharpAccessModifier.NONE && myCanGenerateBlock)
		{
			builder.append(modifier.getPresentableText()).append(" ");
		}

		myAdditionalModifiersAppender.consume(myDeclaration, builder);

		int flags = CSharpElementPresentationUtil.METHOD_WITH_RETURN_TYPE | CSharpElementPresentationUtil.METHOD_PARAMETER_NAME | CSharpElementPresentationUtil.NON_QUALIFIED_TYPE;
		String text = CSharpElementPresentationUtil.formatMethod(myDeclaration, flags);
		builder.append(text);

		if(myCanGenerateBlock)
		{
			builder.append(" {\n");
			myReturnAppender.consume(myDeclaration, builder);
			builder.append("}");
		}
		else
		{
			builder.append(";");
		}
		return builder.toString();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public ExpectedUsingInfo getExpectedUsingInfo()
	{
		return ExpectedUsingInfo.calculateFrom(myDeclaration);
	}
}
