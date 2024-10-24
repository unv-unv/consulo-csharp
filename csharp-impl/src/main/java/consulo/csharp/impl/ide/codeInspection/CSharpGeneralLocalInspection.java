/*
 * Copyright 2013-2022 consulo.io
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

package consulo.csharp.impl.ide.codeInspection;

import consulo.csharp.lang.CSharpLanguage;
import consulo.language.Language;
import consulo.language.editor.inspection.LocalInspectionTool;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11-Sep-22
 */
public abstract class CSharpGeneralLocalInspection extends LocalInspectionTool
{
	@Nullable
	@Override
	public final Language getLanguage()
	{
		return CSharpLanguage.INSTANCE;
	}

	@Nonnull
	@Override
	public final String getGroupDisplayName()
	{
		return "General";
	}
}
