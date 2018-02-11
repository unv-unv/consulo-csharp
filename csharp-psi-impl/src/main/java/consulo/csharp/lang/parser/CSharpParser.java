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

package consulo.csharp.lang.parser;

import javax.annotation.Nonnull;

import consulo.csharp.lang.parser.decl.DeclarationParsing;
import com.intellij.lang.ASTNode;
import consulo.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpParser extends SharedParsingHelpers implements PsiParser
{
	@Nonnull
	@Override
	public ASTNode parse(@Nonnull IElementType elementType, @Nonnull PsiBuilder builder, @Nonnull LanguageVersion languageVersion)
	{
		//builder.setDebugMode(true);
		CSharpBuilderWrapper builderWrapper = new CSharpBuilderWrapper(builder, languageVersion);

		PsiBuilder.Marker marker = builderWrapper.mark();

		DeclarationParsing.parseAll(builderWrapper, true, false);

		marker.done(elementType);
		return builder.getTreeBuilt();
	}
}
