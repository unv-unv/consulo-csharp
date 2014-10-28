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

package org.mustbe.consulo.csharp.lang.psi.impl.msil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.dotnet.lang.psi.impl.stub.MsilHelper;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilDelegateTypeRef extends DotNetTypeRef.Delegate
{
	private final Boolean myNullability;

	public MsilDelegateTypeRef(DotNetTypeRef typeRef, Boolean nullability)
	{
		super(typeRef);
		myNullability = nullability;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		String s = MsilHelper.cutGenericMarker(super.getPresentableText());
		if(myNullability == Boolean.TRUE)
		{
			s += "?";
		}
		return s;
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		return MsilHelper.prepareForUser(super.getQualifiedText());
	}

	@NotNull
	@Override
	public DotNetTypeResolveResult resolve(@NotNull final PsiElement scope)
	{
		return new DotNetTypeResolveResult()
		{
			private DotNetTypeResolveResult cachedResult = MsilDelegateTypeRef.this.getDelegate().resolve(scope);

			@Nullable
			@Override
			public PsiElement getElement()
			{
				PsiElement element = cachedResult.getElement();
				if(element == null)
				{
					return null;
				}
				return MsilToCSharpUtil.wrap(cachedResult.getElement());
			}

			@NotNull
			@Override
			public DotNetGenericExtractor getGenericExtractor()
			{
				return cachedResult.getGenericExtractor();
			}

			@Override
			public boolean isNullable()
			{
				return myNullability == Boolean.TRUE || CSharpTypeUtil.isElementIsNullable(getElement());
			}
		};
	}
}
