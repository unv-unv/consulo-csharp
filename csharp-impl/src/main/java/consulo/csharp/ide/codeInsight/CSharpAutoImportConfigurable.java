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

package consulo.csharp.ide.codeInsight;

import com.intellij.openapi.options.Configurable;
import consulo.disposer.Disposable;
import consulo.options.SimpleConfigurableByProperties;
import consulo.platform.base.localize.ApplicationLocalize;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 01.01.14.
 */
public class CSharpAutoImportConfigurable extends SimpleConfigurableByProperties implements Configurable
{
	private final Provider<CSharpCodeInsightSettings> myCSharpCodeInsightSettings;

	@Inject
	public CSharpAutoImportConfigurable(Provider<CSharpCodeInsightSettings> codeInsightSettings)
	{
		myCSharpCodeInsightSettings = codeInsightSettings;
	}

	@RequiredUIAccess
	@Nonnull
	@Override
	protected Component createLayout(@Nonnull PropertyBuilder propertyBuilder, @Nonnull Disposable uiDisposable)
	{
		VerticalLayout verticalLayout = VerticalLayout.create();

		CSharpCodeInsightSettings settings = myCSharpCodeInsightSettings.get();

		CheckBox optimizeImportOnTheFlyBox = CheckBox.create(ApplicationLocalize.checkboxOptimizeImportsOnTheFly());
		verticalLayout.add(optimizeImportOnTheFlyBox);
		propertyBuilder.add(optimizeImportOnTheFlyBox, () -> settings.OPTIMIZE_IMPORTS_ON_THE_FLY, value -> settings.OPTIMIZE_IMPORTS_ON_THE_FLY = value);

		return verticalLayout;
	}
}
