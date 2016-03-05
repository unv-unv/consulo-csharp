/*
 * Copyright 2013-2016 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.util.ExceptionUtil;

/**
 * @author VISTALL
 * @since 05.03.2016
 */
public class CSharpResolveResult extends PsiElementResolveResult
{
	public static final Key<PsiElement> FORCE_PROVIDER_ELEMENT = Key.create("csharp.provider.element");

	private PsiElement myProviderElement;
	private String myCreateTrace = ExceptionUtil.getThrowableText(new Exception());

	public CSharpResolveResult(@NotNull PsiElement element)
	{
		super(element);
	}

	public CSharpResolveResult(@NotNull PsiElement element, boolean validResult)
	{
		super(element, validResult);
	}

	@NotNull
	public CSharpResolveResult setProvider(@Nullable PsiElement element)
	{
		myProviderElement = element;
		return this;
	}

	@Nullable
	public PsiElement getProviderElement()
	{
		return myProviderElement;
	}
}
