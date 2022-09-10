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

package consulo.csharp.lang.psi;

import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.util.collection.ArrayFactory;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface CSharpTypeDeclaration extends DotNetTypeDeclaration, CSharpGenericConstraintOwner, CSharpBodyWithBraces, CSharpNamedElement
{
	public static final CSharpTypeDeclaration[] EMPTY_ARRAY = new CSharpTypeDeclaration[0];

	public static ArrayFactory<CSharpTypeDeclaration> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new CSharpTypeDeclaration[count];
}
