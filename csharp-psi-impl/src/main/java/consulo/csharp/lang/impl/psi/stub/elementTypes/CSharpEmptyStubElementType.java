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

package consulo.csharp.lang.impl.psi.stub.elementTypes;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.EmptyStub;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 19.10.14
 */
public abstract class CSharpEmptyStubElementType<T extends PsiElement> extends CSharpAbstractStubElementType<EmptyStub<T>, T>
{
	public CSharpEmptyStubElementType(@Nonnull @NonNls String debugName)
	{
		super(debugName);
	}

	@RequiredReadAction
	@Override
	public EmptyStub<T> createStub(@Nonnull T type, StubElement stubElement)
	{
		return new EmptyStub<T>(stubElement, this);
	}

	@Override
	public void serialize(@Nonnull EmptyStub cSharpEmptyStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@Nonnull
	@Override
	public EmptyStub<T> deserialize(@Nonnull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		return new EmptyStub<T>(stubElement, this);
	}
}
