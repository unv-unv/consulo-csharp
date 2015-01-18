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

package org.mustbe.consulo.csharp.lang.psi;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetLocalVariable;
import org.mustbe.consulo.dotnet.psi.DotNetType;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public interface CSharpLocalVariable extends DotNetLocalVariable
{
	/**
	 *
	 * @return variable type, but dont inherit it from prev child if we use declaration like 'int b, c'
	 */
	@Nullable
	DotNetType getSelfType();
}
