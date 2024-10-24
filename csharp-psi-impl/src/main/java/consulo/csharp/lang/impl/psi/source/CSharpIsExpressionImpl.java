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

package consulo.csharp.lang.impl.psi.source;

import consulo.language.ast.IElementType;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.lang.impl.psi.CSharpElementVisitor;
import consulo.csharp.lang.impl.psi.source.resolve.type.CSharpTypeRefByQName;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class CSharpIsExpressionImpl extends CSharpExpressionImpl implements DotNetExpression
{
	public CSharpIsExpressionImpl(@Nonnull IElementType elementType)
	{
		super(elementType);
	}

	@Nonnull
	@RequiredReadAction
	public DotNetTypeRef getIsTypeRef()
	{
		DotNetType type = findChildByClass(DotNetType.class);
		return type == null ? DotNetTypeRef.ERROR_TYPE : type.toTypeRef();
	}

	@Nonnull
	public DotNetExpression getExpression()
	{
		return findNotNullChildByClass(DotNetExpression.class);
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitIsExpression(this);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl(boolean resolveFromParent)
	{
		return new CSharpTypeRefByQName(this, DotNetTypes.System.Boolean);
	}

	@Nullable
	public CSharpIsVariableImpl getVariable()
	{
		return findChildByClass(CSharpIsVariableImpl.class);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		CSharpIsVariableImpl variable = getVariable();
		if(variable != null && !processor.execute(variable, state))
		{
			return false;
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}
}
