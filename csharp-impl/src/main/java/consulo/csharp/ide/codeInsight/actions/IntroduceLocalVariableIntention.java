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

package consulo.csharp.ide.codeInsight.actions;

import javax.annotation.Nonnull;

import consulo.ui.RequiredUIAccess;
import consulo.csharp.ide.refactoring.introduceVariable.CSharpIntroduceLocalVariableHandler;
import consulo.csharp.lang.psi.impl.source.CSharpAssignmentExpressionImpl;
import consulo.csharp.lang.psi.impl.source.CSharpExpressionStatementImpl;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeRefUtil;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.BaseRefactoringIntentionAction;
import com.intellij.util.IncorrectOperationException;

/**
 * @author michael.shumenko
 * @since Aug 09, 2015
 */
public class IntroduceLocalVariableIntention extends BaseRefactoringIntentionAction
{
	@Override
	public void invoke(@Nonnull Project project,
			Editor editor,
			@Nonnull PsiElement element) throws IncorrectOperationException
	{
		new CSharpIntroduceLocalVariableHandler().invoke(project, editor, element.getContainingFile(), null);
	}

	@Override
	@RequiredUIAccess
	public boolean isAvailable(@Nonnull Project project, Editor editor, @Nonnull PsiElement psi)
	{
		CSharpExpressionStatementImpl exprStmt = PsiTreeUtil.getParentOfType(psi, CSharpExpressionStatementImpl.class);
		if(psi instanceof SyntheticElement || exprStmt == null)
		{
			return false;
		}

		DotNetExpression expression = exprStmt.getExpression();
		if(expression instanceof CSharpAssignmentExpressionImpl)
		{
			return false;
		}

		DotNetTypeRef ref = expression.toTypeRef(true);
		return !(ref == DotNetTypeRef.ERROR_TYPE || DotNetTypeRefUtil.isVmQNameEqual(ref, expression, DotNetTypes.System.Void));
	}

	@Nonnull
	@Override
	public String getFamilyName()
	{
		return getText();
	}

	@Nonnull
	@Override
	public String getText()
	{
		return CodeInsightBundle.message("intention.introduce.variable.text");
	}
}
