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

package consulo.csharp.module.extension;

import org.jetbrains.annotations.NotNull;
import consulo.csharp.CSharpBundle;
import consulo.util.pointers.Named;
import consulo.util.pointers.NamedPointer;

/**
 * @author VISTALL
 * @since 15.05.14
 */
public enum CSharpLanguageVersion implements Named, NamedPointer<CSharpLanguageVersion>
{
	_1_0,
	_2_0,
	_3_0,
	_4_0,
	_5_0,
	_6_0,
	_7_0;

	public static final CSharpLanguageVersion HIGHEST = _7_0;

	public boolean isAtLeast(@NotNull CSharpLanguageVersion languageVersion)
	{
		return ordinal() >= languageVersion.ordinal();
	}

	@NotNull
	public String getPresentableName()
	{
		String name = name();
		name = name.substring(1, name.length());
		name = name.replace("_", ".");
		return name;
	}

	@NotNull
	public String getDescription()
	{
		return CSharpBundle.message("csharp.version." + name());
	}

	@NotNull
	@Override
	public CSharpLanguageVersion get()
	{
		return this;
	}

	@NotNull
	@Override
	public String getName()
	{
		return name();
	}
}
