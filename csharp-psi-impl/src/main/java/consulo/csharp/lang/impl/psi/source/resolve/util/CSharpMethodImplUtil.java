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

package consulo.csharp.lang.impl.psi.source.resolve.util;

import consulo.csharp.lang.psi.CSharpMethodDeclaration;
import consulo.csharp.lang.psi.CSharpModifier;
import consulo.dotnet.psi.DotNetParameter;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 26.03.14
 */
public class CSharpMethodImplUtil
{
	public static boolean isExtensionMethod(@Nonnull PsiElement element)
	{
		if(element instanceof CSharpMethodDeclaration)
		{
			DotNetParameter[] parameters = ((CSharpMethodDeclaration) element).getParameters();
			return parameters.length > 0 && parameters[0].hasModifier(CSharpModifier.THIS);
		}
		return false;
	}

	public static boolean isExtensionWrapper(@Nullable PsiElement element)
	{
		return element instanceof CSharpMethodDeclaration && element.getUserData(CSharpResolveUtil.EXTENSION_METHOD_WRAPPER) != null;
	}
}
