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

package consulo.csharp.impl.ide.highlight.check.impl;

import jakarta.annotation.Nonnull;

import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.impl.ide.highlight.CSharpHighlightContext;
import consulo.csharp.impl.ide.highlight.check.CompilerCheck;
import consulo.csharp.lang.impl.psi.source.CSharpAwaitExpressionImpl;
import consulo.csharp.lang.impl.psi.source.CSharpCatchStatementImpl;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.language.psi.util.PsiTreeUtil;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 02.01.15
 */
public class CS1985 extends CompilerCheck<CSharpAwaitExpressionImpl>
{
	@RequiredReadAction
	@Nullable
	@Override
	public HighlightInfoFactory checkImpl(@Nonnull CSharpLanguageVersion languageVersion, @Nonnull CSharpHighlightContext highlightContext, @Nonnull CSharpAwaitExpressionImpl element)
	{
		if(languageVersion.isAtLeast(CSharpLanguageVersion._6_0))
		{
			return null;
		}
		CSharpCatchStatementImpl catchStatement = PsiTreeUtil.getParentOfType(element, CSharpCatchStatementImpl.class);
		if(catchStatement != null)
		{
			return newBuilder(element.getAwaitKeywordElement(), "await");
		}
		return null;
	}
}
