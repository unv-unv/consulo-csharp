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

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetMemberOwner;
import consulo.dotnet.psi.DotNetVirtualImplementOwner;
import consulo.navigation.NavigationItem;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 01.03.14
 */
public interface CSharpIndexMethodDeclaration extends DotNetLikeMethodDeclaration, DotNetVirtualImplementOwner, DotNetMemberOwner,
		CSharpSimpleLikeMethodAsElement, CSharpBodyWithBraces, CSharpXAccessorOwner, NavigationItem
{
	@RequiredReadAction
	default boolean isAutoGet()
	{
		return false;
	}

	@Nonnull
	@Override
	@RequiredReadAction
	CSharpCodeBodyProxy getCodeBlock();
}
