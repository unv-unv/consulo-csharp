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

package consulo.csharp.lang.impl.psi.source;

import consulo.application.ApplicationProperties;
import consulo.csharp.lang.impl.psi.CSharpElementVisitor;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public abstract class CSharpElementImpl extends AdvancedCompositePsiElement
{
	public CSharpElementImpl(@Nonnull IElementType elementType)
	{
		super(elementType);

		if(ApplicationProperties.isInSandbox())
		{
			String name = getClass().getName();
			if(name.contains("Expression") && !name.contains("Statement") && !(this instanceof CSharpExpressionImpl))
			{
				throw new IllegalArgumentException();
			}
		}
	}

	@Nonnull
	@Override
	public GlobalSearchScope getResolveScope()
	{
		return super.getResolveScope();
	}

	@Override
	public ItemPresentation getPresentation()
	{
		return ItemPresentationProvider.getItemPresentation(this);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof CSharpElementVisitor)
		{
			accept((CSharpElementVisitor) visitor);
		}
		else
		{
			super.accept(visitor);
		}
	}

	public abstract void accept(@Nonnull CSharpElementVisitor visitor);
}
