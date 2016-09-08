/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 30.12.13.
 */
public class CSharpParenthesesExpressionImpl extends CSharpExpressionImpl implements DotNetExpression
{
	public CSharpParenthesesExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Nullable
	public DotNetExpression getInnerExpression()
	{
		return findChildByClass(DotNetExpression.class);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitParenthesesExpression(this);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRefImpl(boolean resolveFromParent)
	{
		DotNetExpression innerExpression = getInnerExpression();
		if(innerExpression == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		return innerExpression.toTypeRef(resolveFromParent);
	}
}
