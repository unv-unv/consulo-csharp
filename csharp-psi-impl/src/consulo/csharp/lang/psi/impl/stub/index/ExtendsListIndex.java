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

package consulo.csharp.lang.psi.impl.stub.index;

import consulo.lombok.annotations.Lazy;
import org.jetbrains.annotations.NotNull;
import consulo.dotnet.psi.DotNetTypeList;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class ExtendsListIndex extends StringStubIndexExtension<DotNetTypeList>
{
	@NotNull
	@Lazy
	public static ExtendsListIndex getInstance()
	{
		return StubIndexExtension.EP_NAME.findExtension(ExtendsListIndex.class);
	}

	@NotNull
	@Override
	public StubIndexKey<String, DotNetTypeList> getKey()
	{
		return CSharpIndexKeys.EXTENDS_LIST_INDEX;
	}
}
