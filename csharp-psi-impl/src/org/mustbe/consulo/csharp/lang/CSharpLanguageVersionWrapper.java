/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.csharp.lang;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.lexer.CSharpLexer;
import org.mustbe.consulo.csharp.lang.parser.CSharpParser;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.TokenSet;
import consulo.lang.LanguageVersion;
import consulo.lang.LanguageVersionWithParsing;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpLanguageVersionWrapper implements LanguageVersion<CSharpLanguage>, LanguageVersionWithParsing<CSharpLanguage>
{
	private final CSharpLanguageVersion myLanguageVersion;

	public CSharpLanguageVersionWrapper(CSharpLanguageVersion languageVersion)
	{
		myLanguageVersion = languageVersion;
	}

	@NotNull
	@Override
	public PsiParser createParser()
	{
		return new CSharpParser();
	}

	@NotNull
	@Override
	public Lexer createLexer()
	{
		return new CSharpLexer();
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens()
	{
		return CSharpTokenSets.COMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements()
	{
		return CSharpTokenSets.LITERALS;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens()
	{
		return CSharpTokenSets.WHITESPACES;
	}

	@NotNull
	@Override
	public String getName()
	{
		return getLanguageVersion().name();
	}

	@Override
	public CSharpLanguage getLanguage()
	{
		return CSharpLanguage.INSTANCE;
	}

	public CSharpLanguageVersion getLanguageVersion()
	{
		return myLanguageVersion;
	}
}
