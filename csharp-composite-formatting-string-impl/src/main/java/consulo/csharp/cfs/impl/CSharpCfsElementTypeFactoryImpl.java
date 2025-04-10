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

package consulo.csharp.cfs.impl;

import consulo.annotation.component.ServiceImpl;
import consulo.csharp.cfs.CSharpCfsElementTypeFactory;
import consulo.csharp.lang.CSharpLanguage;
import consulo.dotnet.cfs.lang.CfsLanguage;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.ILazyParseableElementType;
import consulo.language.impl.psi.LazyParseablePsiElement;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiBuilderFactory;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.version.LanguageVersion;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;

/**
 * @author VISTALL
 * @since 26-Dec-17
 */
@Singleton
@ServiceImpl
public class CSharpCfsElementTypeFactoryImpl implements CSharpCfsElementTypeFactory
{
	@Nonnull
	@Override
	public IElementType getInterpolationStringElementType()
	{
		return new ILazyParseableElementType("INTERPOLATION_STRING_LITERAL", CSharpLanguage.INSTANCE)
		{
			@Override
			protected ASTNode doParseContents(@Nonnull final ASTNode chameleon, @Nonnull final PsiElement psi)
			{
				final Project project = psi.getProject();
				final Language languageForParser = getLanguageForParser(psi);
				final LanguageVersion languageVersion = CSharpCfsLanguageVersion.getInstance();
				final PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, languageVersion, chameleon.getChars());
				final PsiParser parser = ParserDefinition.forLanguage(languageForParser).createParser(languageVersion);
				return parser.parse(this, builder, languageVersion).getFirstChildNode();
			}

			@Override
			protected Language getLanguageForParser(PsiElement psi)
			{
				return CfsLanguage.INSTANCE;
			}

			@Nullable
			@Override
			public ASTNode createNode(CharSequence text)
			{
				return new LazyParseablePsiElement(this, text);
			}
		};
	}
}
