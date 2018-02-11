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
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 30.11.14
 */
public class CSharpLinqLetClauseImpl extends CSharpElementImpl
{
	public CSharpLinqLetClauseImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Nullable
	public CSharpLinqVariableImpl getVariable()
	{
		return findChildByClass(CSharpLinqVariableImpl.class);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent,
			@Nonnull PsiElement place)
	{
		CSharpLinqVariableImpl variable = getVariable();
		if(variable != null)
		{
			if(!processor.execute(variable, state))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitLinqLetClause(this);
	}
}
