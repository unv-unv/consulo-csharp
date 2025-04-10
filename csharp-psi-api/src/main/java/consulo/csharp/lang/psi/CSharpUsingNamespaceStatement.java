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
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.util.collection.ArrayFactory;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 18.10.14
 */
public interface CSharpUsingNamespaceStatement extends CSharpUsingListChild
{
	CSharpUsingNamespaceStatement[] EMPTY_ARRAY = new CSharpUsingNamespaceStatement[0];

	ArrayFactory<CSharpUsingNamespaceStatement> ARRAY_FACTORY = ArrayFactory.of(CSharpUsingNamespaceStatement[]::new);

	@RequiredReadAction
	default boolean isGlobal()
	{
		return false;
	}

	@Nullable
	@RequiredReadAction
	String getReferenceText();

	@Nullable
	@RequiredReadAction
	DotNetNamespaceAsElement resolve();

	@Nullable
	DotNetReferenceExpression getNamespaceReference();
}
