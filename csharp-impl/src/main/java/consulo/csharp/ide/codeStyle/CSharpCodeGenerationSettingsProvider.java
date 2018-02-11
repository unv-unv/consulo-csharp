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

package consulo.csharp.ide.codeStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.csharp.lang.CSharpLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

/**
 * @author VISTALL
 * @since 28.07.2015
 */
public class CSharpCodeGenerationSettingsProvider extends CodeStyleSettingsProvider
{
	@Override
	public boolean hasSettingsPage()
	{
		return false;
	}

	@Override
	public String getConfigurableDisplayName()
	{
		return ApplicationBundle.message("title.code.generation");
	}

	@Nullable
	@Override
	public Language getLanguage()
	{
		return CSharpLanguage.INSTANCE;
	}

	@Nullable
	@Override
	public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings)
	{
		return new CSharpCodeGenerationSettings(settings);
	}

	@Nonnull
	@Override
	public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings)
	{
		return new CSharpCodeGenerationSettingsConfigurable(settings);
	}
}
