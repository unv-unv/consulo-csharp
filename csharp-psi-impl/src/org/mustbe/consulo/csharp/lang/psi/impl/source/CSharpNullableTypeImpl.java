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
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpEmptyStub;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 17.04.14
 */
public class CSharpNullableTypeImpl extends CSharpStubElementImpl<CSharpEmptyStub<CSharpNullableTypeImpl>> implements DotNetType
{
	public CSharpNullableTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpNullableTypeImpl(@NotNull CSharpEmptyStub<CSharpNullableTypeImpl> stub,
			@NotNull IStubElementType<? extends CSharpEmptyStub<CSharpNullableTypeImpl>, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitNullableType(this);
	}

	@Override
	@NotNull
	public DotNetTypeRef toTypeRef()
	{
		DotNetType innerType = getInnerType();
		if(innerType == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		return new DotNetTypeRef.Delegate(innerType.toTypeRef())
		{
			@Override
			public boolean isNullable()
			{
				return true;
			}
		};
	}

	@Nullable
	public DotNetType getInnerType()
	{
		return getStubOrPsiChildByIndex(CSharpStubElements.TYPE_SET, 0);
	}

	@NotNull
	public PsiElement getQuestElement()
	{
		return findNotNullChildByType(CSharpTokens.QUEST);
	}
}
