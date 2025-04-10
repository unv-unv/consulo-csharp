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

import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.impl.ide.highlight.CSharpHighlightContext;
import consulo.csharp.impl.ide.highlight.check.CompilerCheck;
import consulo.csharp.lang.impl.psi.CSharpElements;
import consulo.csharp.lang.psi.CSharpTypeDeclaration;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.util.collection.ArrayUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 21.01.2016
 */
public class CS1008 extends CompilerCheck<DotNetType>
{
	private static final String[] ourEnumSuperTypes = new String[] {
			DotNetTypes.System.SByte,
			DotNetTypes.System.Byte,
			DotNetTypes.System.Int16,
			DotNetTypes.System.UInt16,
			DotNetTypes.System.Int32,
			DotNetTypes.System.UInt32,
			DotNetTypes.System.Int64,
			DotNetTypes.System.UInt64,
	};

	@RequiredReadAction
	@Nullable
	@Override
	public HighlightInfoFactory checkImpl(@Nonnull CSharpLanguageVersion languageVersion, @Nonnull CSharpHighlightContext highlightContext, @Nonnull DotNetType element)
	{
		PsiElement parent = element.getParent();
		if(parent instanceof DotNetTypeList && PsiUtilCore.getElementType(parent) == CSharpElements.EXTENDS_LIST)
		{
			PsiElement superParent = parent.getParent();
			if(superParent instanceof CSharpTypeDeclaration && ((CSharpTypeDeclaration) superParent).isEnum())
			{
				PsiElement psiElement = element.toTypeRef().resolve().getElement();
				if(psiElement instanceof CSharpTypeDeclaration)
				{
					if(!ArrayUtil.contains(((CSharpTypeDeclaration) psiElement).getVmQName(), ourEnumSuperTypes))
					{
						return newBuilder(element);
					}
				}
			}
		}
		return null;
	}
}
