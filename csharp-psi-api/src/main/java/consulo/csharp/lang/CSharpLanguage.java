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

package consulo.csharp.lang;

import consulo.language.Language;
import consulo.language.version.LanguageVersion;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.11.13
 */
public class CSharpLanguage extends Language
{
	public static final CSharpLanguage INSTANCE = new CSharpLanguage();

	public CSharpLanguage()
	{
		super("C#");
	}

	@Override
	public boolean isCaseSensitive()
	{
		return true;
	}

	@Nonnull
	@Override
	protected LanguageVersion[] findVersions()
	{
		return CSharpLanguageVersionHelper.getInstance().getVersions();
	}
}
