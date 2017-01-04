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

package consulo.csharp.lang.psi.impl.stub.elementTypes;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import consulo.csharp.lang.psi.CSharpAttributeList;
import consulo.csharp.lang.psi.impl.source.CSharpStubAttributeListImpl;
import consulo.csharp.lang.psi.impl.stub.CSharpAttributeListStub;
import consulo.csharp.lang.psi.impl.stub.index.CSharpIndexKeys;
import consulo.dotnet.psi.DotNetAttributeTargetType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 17.10.14
 */
public class CSharpAttributeListStubElementType extends CSharpAbstractStubElementType<CSharpAttributeListStub, CSharpAttributeList>
{
	public CSharpAttributeListStubElementType()
	{
		super("ATTRIBUTE_LIST");
	}

	@NotNull
	@Override
	public PsiElement createElement(@NotNull ASTNode astNode)
	{
		return new CSharpStubAttributeListImpl(astNode);
	}

	@Override
	public CSharpAttributeList createPsi(@NotNull CSharpAttributeListStub stub)
	{
		return new CSharpStubAttributeListImpl(stub, this);
	}

	@Override
	public boolean shouldCreateStub(ASTNode node)
	{
		PsiElement psi = node.getPsi();
		return ((CSharpAttributeList) psi).getTargetType() != null;
	}

	@Override
	public CSharpAttributeListStub createStub(@NotNull CSharpAttributeList attributeList, StubElement stubElement)
	{
		DotNetAttributeTargetType targetType = attributeList.getTargetType();
		assert targetType != null;
		int targetIndex = targetType.ordinal();
		return new CSharpAttributeListStub(stubElement, this, targetIndex);
	}

	@Override
	public void serialize(@NotNull CSharpAttributeListStub stub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeVarInt(stub.getTargetIndex());
	}

	@NotNull
	@Override
	public CSharpAttributeListStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		int targetIndex = stubInputStream.readVarInt();
		return new CSharpAttributeListStub(stubElement, this, targetIndex);
	}

	@Override
	public void indexStub(@NotNull CSharpAttributeListStub stub, @NotNull IndexSink indexSink)
	{
		indexSink.occurrence(CSharpIndexKeys.ATTRIBUTE_LIST_INDEX, stub.getTarget());
	}
}
