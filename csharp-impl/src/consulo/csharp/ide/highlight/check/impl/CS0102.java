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

package consulo.csharp.ide.highlight.check.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.highlight.CSharpHighlightContext;
import consulo.csharp.ide.highlight.check.CompilerCheck;
import consulo.csharp.lang.psi.CSharpElementCompareUtil;
import consulo.csharp.lang.psi.CSharpModifier;
import consulo.csharp.lang.psi.CSharpTypeDeclaration;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.psi.DotNetMemberOwner;
import consulo.dotnet.psi.DotNetModifierListOwner;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.ObjectUtil;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 10.06.14
 */
public class CS0102 extends CompilerCheck<CSharpTypeDeclaration>
{
	@RequiredReadAction
	@NotNull
	@Override
	public List<CompilerCheckBuilder> check(@NotNull CSharpLanguageVersion languageVersion, @NotNull CSharpHighlightContext highlightContext, @NotNull CSharpTypeDeclaration element)
	{
		return doCheck(this, element);
	}

	@NotNull
	public static <T extends DotNetMemberOwner & DotNetQualifiedElement> List<CompilerCheckBuilder> doCheck(@NotNull CompilerCheck<T> compilerCheck,
			@NotNull T element)
	{
		List<CompilerCheckBuilder> results = new SmartList<CompilerCheckBuilder>();

		final DotNetNamedElement[] members = element.getMembers();

		for(final DotNetNamedElement namedElement : members)
		{
			DotNetNamedElement findTarget = ContainerUtil.find(members, new Condition<DotNetNamedElement>()
			{
				@Override
				public boolean value(DotNetNamedElement element)
				{
					if(element == namedElement)
					{
						return false;
					}

					boolean equal = CSharpElementCompareUtil.isEqualWithVirtualImpl(element, namedElement, element);
					// we dont interest if it partial types
					if(equal && isPartial(element) && isPartial(namedElement))
					{
						return false;
					}
					return equal;
				}
			});

			if(findTarget != null)
			{
				PsiElement toHighlight = findTarget;
				if(findTarget instanceof PsiNameIdentifierOwner)
				{
					PsiElement nameIdentifier = ((PsiNameIdentifierOwner) findTarget).getNameIdentifier();
					toHighlight = ObjectUtil.notNull(nameIdentifier, findTarget);
				}

				results.add(compilerCheck.newBuilder(toHighlight, element.getPresentableQName(), findTarget.getName()));
			}
		}
		return results;
	}

	private static boolean isPartial(DotNetNamedElement element)
	{
		return element instanceof DotNetModifierListOwner && ((DotNetModifierListOwner) element).hasModifier(CSharpModifier.PARTIAL);
	}
}
