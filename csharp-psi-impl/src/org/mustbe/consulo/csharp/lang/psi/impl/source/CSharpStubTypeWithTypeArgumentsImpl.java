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
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.cache.CSharpResolveCache;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpEmptyStub;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.psi.DotNetTypeWithTypeArguments;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 13.12.13.
 */
public class CSharpStubTypeWithTypeArgumentsImpl extends CSharpStubElementImpl<CSharpEmptyStub<DotNetTypeWithTypeArguments>> implements
		DotNetTypeWithTypeArguments
{
	public CSharpStubTypeWithTypeArgumentsImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpStubTypeWithTypeArgumentsImpl(@NotNull CSharpEmptyStub<DotNetTypeWithTypeArguments> stub,
			@NotNull IStubElementType<? extends CSharpEmptyStub<DotNetTypeWithTypeArguments>, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		return CSharpResolveCache.getInstance(getProject()).resolveTypeRef(this, CSharpTypeWithTypeArgumentsImpl.OurResolver.INSTANCE, true);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitTypeWrapperWithTypeArguments(this);
	}

	@NotNull
	@Override
	public DotNetType getInnerType()
	{
		return getRequiredStubOrPsiChildByIndex(CSharpStubElements.TYPE_SET, 0);
	}

	@Nullable
	@Override
	public DotNetTypeList getArgumentsList()
	{
		return getStubOrPsiChild(CSharpStubElements.TYPE_ARGUMENTS);
	}

	@NotNull
	@Override
	public DotNetType[] getArguments()
	{
		DotNetTypeList argumentsList = getArgumentsList();
		if(argumentsList == null)
		{
			return DotNetType.EMPTY_ARRAY;
		}
		return argumentsList.getTypes();
	}
}
