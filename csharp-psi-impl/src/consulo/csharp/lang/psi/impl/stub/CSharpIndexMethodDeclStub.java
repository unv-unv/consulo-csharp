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

package consulo.csharp.lang.psi.impl.stub;

import org.jetbrains.annotations.Nullable;
import com.intellij.psi.stubs.StubElement;
import consulo.csharp.lang.psi.CSharpIndexMethodDeclaration;
import consulo.csharp.lang.psi.CSharpStubElements;

/**
 * @author VISTALL
 * @since 01.03.14
 */
public class CSharpIndexMethodDeclStub extends MemberStub<CSharpIndexMethodDeclaration>
{
	public CSharpIndexMethodDeclStub(StubElement parent, @Nullable String qname)
	{
		super(parent, CSharpStubElements.INDEX_METHOD_DECLARATION, qname, 0);
	}
}
