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

package consulo.csharp.lang.psi.impl.source;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpStatementAsStatementOwner;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetStatement;
import consulo.dotnet.psi.DotNetVariable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author VISTALL
 * @since 06.01.14.
 */
public class CSharpUsingStatementImpl extends CSharpElementImpl implements DotNetStatement, CSharpStatementAsStatementOwner
{
	public CSharpUsingStatementImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Nullable
	public DotNetVariable getVariable()
	{
		return findChildByClass(DotNetVariable.class);
	}

	@Nullable
	public DotNetExpression getExpression()
	{
		return findChildByClass(DotNetExpression.class);
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitUsingStatement(this);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent,
			@Nonnull PsiElement place)
	{
		if(lastParent == null || !PsiTreeUtil.isAncestor(this, lastParent, false))
		{
			return true;
		}

		DotNetVariable variable = getVariable();
		if(variable != null)
		{
			if(!processor.execute(variable, state))
			{
				return false;
			}
		}
		return true;
	}

	@Nullable
	@Override
	public DotNetStatement getChildStatement()
	{
		return findChildByClass(DotNetStatement.class);
	}
}
