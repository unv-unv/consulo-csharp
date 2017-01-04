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

package consulo.csharp.lang.psi.impl.source.resolve.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.lang.psi.CSharpCallArgumentListOwner;
import consulo.csharp.lang.psi.CSharpNewExpression;
import consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImplUtil;
import consulo.csharp.lang.psi.impl.source.resolve.CSharpResolveOptions;
import consulo.csharp.lang.psi.impl.source.resolve.StubScopeProcessor;
import consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.resolve.DotNetGenericExtractor;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 27.07.2015
 */
public class FieldOrPropertyKindProcessor implements KindProcessor
{
	@RequiredReadAction
	@Override
	public void process(@NotNull CSharpResolveOptions options,
			@NotNull DotNetGenericExtractor defaultExtractor,
			@Nullable PsiElement forceQualifierElement,
			@NotNull Processor<ResolveResult> processor)
	{
		PsiElement element = options.getElement();

		DotNetTypeRef resolvedTypeRef;
		CSharpCallArgumentListOwner callArgumentListOwner = PsiTreeUtil.getParentOfType(element, CSharpCallArgumentListOwner.class);

		if(callArgumentListOwner instanceof CSharpNewExpression)
		{
			resolvedTypeRef = ((CSharpNewExpression) callArgumentListOwner).toTypeRef(false);
		}
		else if(callArgumentListOwner instanceof DotNetAttribute)
		{
			resolvedTypeRef = ((DotNetAttribute) callArgumentListOwner).toTypeRef();
		}
		else
		{
			throw new IllegalArgumentException(callArgumentListOwner == null ? "null" : callArgumentListOwner.getClass().getName());
		}

		if(resolvedTypeRef == DotNetTypeRef.ERROR_TYPE)
		{
			return;
		}

		DotNetTypeResolveResult typeResolveResult = resolvedTypeRef.resolve();

		PsiElement typeElement = typeResolveResult.getElement();
		if(typeElement == null)
		{
			return;
		}

		DotNetGenericExtractor genericExtractor = typeResolveResult.getGenericExtractor();

		StubScopeProcessor scopeProcessor = CSharpReferenceExpressionImplUtil.createMemberProcessor(options, processor);

		ResolveState state = ResolveState.initial();
		state = state.put(CSharpResolveUtil.EXTRACTOR, genericExtractor);
		state = state.put(CSharpResolveUtil.SELECTOR, options.getSelector());
		CSharpResolveUtil.walkChildren(scopeProcessor, typeElement, false, true, state);
	}
}
