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
import jakarta.annotation.Nullable;

import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.impl.ide.highlight.CSharpHighlightContext;
import consulo.csharp.impl.ide.highlight.check.CompilerCheck;
import consulo.csharp.lang.impl.psi.source.CSharpLikeMethodDeclarationImplUtil;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.psi.DotNetVirtualImplementOwner;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.util.lang.Pair;

/**
 * @author VISTALL
 * @since 08.11.14
 */
public class CS0539 extends CompilerCheck<DotNetVirtualImplementOwner>
{
	@RequiredReadAction
	@Nullable
	@Override
	public CompilerCheckBuilder checkImpl(@Nonnull CSharpLanguageVersion languageVersion, @Nonnull CSharpHighlightContext highlightContext, @Nonnull DotNetVirtualImplementOwner element)
	{
		PsiElement nameIdentifier = ((PsiNameIdentifierOwner) element).getNameIdentifier();
		if(nameIdentifier == null)
		{
			return null;
		}
		final Pair<CSharpLikeMethodDeclarationImplUtil.ResolveVirtualImplementResult, PsiElement> resultPair = CSharpLikeMethodDeclarationImplUtil.resolveVirtualImplementation(element, element);
		switch(resultPair.getFirst())
		{
			case CANT_HAVE:
			case FOUND:
			default:
				return null;
			case NOT_FOUND:
				return newBuilder(nameIdentifier, formatElement(element));
		}
	}
}
