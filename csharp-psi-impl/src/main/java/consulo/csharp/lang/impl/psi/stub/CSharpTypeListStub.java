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

package consulo.csharp.lang.impl.psi.stub;

import consulo.language.psi.stub.StubElement;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class CSharpTypeListStub extends StubBase<DotNetTypeList>
{
	private final String[] myReferences;

	public CSharpTypeListStub(StubElement parent, IStubElementType elementType, String[] references)
	{
		super(parent, elementType);
		myReferences = references;
	}

	@Nonnull
	public String[] geShortReferences()
	{
		return myReferences;
	}
}
