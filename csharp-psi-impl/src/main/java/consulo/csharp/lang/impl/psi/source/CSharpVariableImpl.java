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

import consulo.language.psi.PsiModificationTracker;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetVariable;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 06.01.14.
 */
public abstract class CSharpVariableImpl extends CSharpMemberImpl implements DotNetVariable
{
	private static final CSharpTypeRefCacher<CSharpVariableImpl> ourCacheSystem = new CSharpTypeRefCacher<CSharpVariableImpl>(true)
	{
		@RequiredReadAction
		@Nonnull
		@Override
		protected DotNetTypeRef toTypeRefImpl(CSharpVariableImpl element, boolean resolveFromParentOrInitializer)
		{
			return element.toTypeRefImpl(resolveFromParentOrInitializer);
		}
	};

	protected final ThreadLocal<Boolean> myTypeRefProcessing = ThreadLocal.withInitial(() -> Boolean.FALSE);

	public CSharpVariableImpl(@Nonnull IElementType elementType)
	{
		super(elementType);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetExpression getInitializer()
	{
		return findChildByClass(DotNetExpression.class);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromInitializer)
	{
		return ourCacheSystem.toTypeRef(this, resolveFromInitializer, getCacheKeys());
	}

	@Nonnull
	protected Object[] getCacheKeys()
	{
		return new Object[]{PsiModificationTracker.MODIFICATION_COUNT};
	}

	@RequiredReadAction
	@Nonnull
	public DotNetTypeRef toTypeRefImpl(boolean resolveFromInitializer)
	{
		DotNetType type = getType();
		if(type == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}

		DotNetTypeRef runtimeType = type.toTypeRef();
		if(resolveFromInitializer && runtimeType == DotNetTypeRef.AUTO_TYPE)
		{
			final DotNetExpression initializer = getInitializer();
			if(initializer == null)
			{
				return DotNetTypeRef.ERROR_TYPE;
			}

			if(myTypeRefProcessing.get())
			{
				return DotNetTypeRef.AUTO_TYPE;
			}

			try
			{
				myTypeRefProcessing.set(Boolean.TRUE);
				return initializer.toTypeRef(true);
			}
			finally
			{
				myTypeRefProcessing.set(Boolean.FALSE);
			}
		}
		else
		{
			return runtimeType;
		}
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getConstantKeywordElement()
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public boolean isConstant()
	{
		return false;
	}
}
