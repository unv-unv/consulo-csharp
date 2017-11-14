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

package consulo.csharp.ide.codeInsight.editorActions;

import java.util.Collections;
import java.util.List;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import consulo.csharp.lang.psi.CSharpTokens;

/**
 * @author VISTALL
 * @since 14-Nov-17
 */
public class CSharpExtendWordSelectionHandler extends ExtendWordSelectionHandlerBase
{
	@Override
	public boolean canSelect(PsiElement e)
	{
		return PsiUtilCore.getElementType(e) == CSharpTokens.IDENTIFIER;
	}

	@Override
	public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor)
	{
		return Collections.singletonList(e.getTextRange());
	}
}
