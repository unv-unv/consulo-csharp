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

package consulo.csharp.lang.psi.impl.source.injection;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.ILazyParseableElementType;
import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.lang.CSharpLanguage;
import consulo.csharp.lang.lexer.CSharpLexer;
import consulo.csharp.lang.parser.CSharpBuilderWrapper;
import consulo.csharp.lang.parser.ModifierSet;
import consulo.csharp.lang.parser.exp.ExpressionParsing;
import consulo.csharp.lang.psi.CSharpReferenceExpression;
import consulo.lang.LanguageVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 12.03.2015
 */
public class CSharpInjectExpressionElementType extends ILazyParseableElementType
{
	private final static PsiParser ourParser = (elementType, builder, languageVersion) ->
	{
		PsiBuilder.Marker mark = builder.mark();
		ExpressionParsing.parse(new CSharpBuilderWrapper(builder, languageVersion), ModifierSet.EMPTY);
		while(!builder.eof())
		{
			builder.error("Unexpected token");
			builder.advanceLexer();
		}
		mark.done(elementType);
		return builder.getTreeBuilt();
	};

	private final CSharpReferenceExpression.ResolveToKind myResolveToKind;

	public CSharpInjectExpressionElementType(@Nonnull String debugName, @Nullable Language language, @Nonnull CSharpReferenceExpression.ResolveToKind resolveToKind)
	{
		super(debugName, language);
		myResolveToKind = resolveToKind;
	}

	@Override
	@RequiredReadAction
	protected ASTNode doParseContents(@Nonnull final ASTNode chameleon, @Nonnull final PsiElement psi)
	{
		Project project = psi.getProject();
		Language language = getLanguageForParser(psi);
		CSharpLexer lexer = new CSharpLexer();
		PsiElement element = findElementOfLanguage(psi, CSharpLanguage.INSTANCE);
		LanguageVersion version = element == null ? language.getVersions()[0] : element.getLanguageVersion();
		PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, lexer, language, version, chameleon.getChars());
		return ourParser.parse(this, builder, version).getFirstChildNode();
	}

	@RequiredReadAction
	private static PsiElement findElementOfLanguage(PsiElement element, Language language)
	{
		if(element.getLanguage() == language)
		{
			return element;
		}

		PsiElement parent = element.getParent();
		if(parent != null)
		{
			return findElementOfLanguage(parent, language);
		}

		return null;
	}

	@Override
	protected Language getLanguageForParser(PsiElement psi)
	{
		return CSharpLanguage.INSTANCE;
	}

	@Nullable
	@Override
	public ASTNode createNode(CharSequence text)
	{
		return new CSharpForInjectionFragmentHolder(this, text, myResolveToKind);
	}
}