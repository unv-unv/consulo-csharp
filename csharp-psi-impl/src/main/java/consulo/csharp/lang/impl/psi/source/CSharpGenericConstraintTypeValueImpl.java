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

import consulo.csharp.lang.impl.psi.CSharpElementVisitor;
import consulo.csharp.lang.impl.psi.CSharpStubElementSets;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.EmptyStub;
import consulo.csharp.lang.psi.CSharpGenericConstraintTypeValue;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.psi.stub.IStubElementType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11.03.14
 */
public class CSharpGenericConstraintTypeValueImpl extends CSharpStubElementImpl<EmptyStub<CSharpGenericConstraintTypeValue>> implements CSharpGenericConstraintTypeValue
{
	public CSharpGenericConstraintTypeValueImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public CSharpGenericConstraintTypeValueImpl(@Nonnull EmptyStub<CSharpGenericConstraintTypeValue> stub,
			@Nonnull IStubElementType<? extends EmptyStub<CSharpGenericConstraintTypeValue>, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitGenericConstraintTypeValue(this);
	}

	@Nullable
	@Override
	public DotNetType getType()
	{
		return getStubOrPsiChildByIndex(CSharpStubElementSets.TYPE_SET, 0);
	}

	@Nonnull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		DotNetType type = getType();
		if(type == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		return getType().toTypeRef();
	}
}