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
import consulo.csharp.lang.impl.psi.source.CSharpArrayInitializerImpl;
import consulo.csharp.lang.impl.psi.source.CSharpNewExpressionImpl;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 24.01.15
 */
public class CS0826 extends CompilerCheck<CSharpNewExpressionImpl>
{
	@RequiredReadAction
	@Nullable
	@Override
	public HighlightInfoFactory checkImpl(@Nonnull CSharpLanguageVersion languageVersion, @Nonnull CSharpHighlightContext highlightContext, @Nonnull CSharpNewExpressionImpl element)
	{
		DotNetType newType = element.getNewType();
		if(newType != null)
		{
			return null;
		}
		CSharpArrayInitializerImpl arrayInitializationExpression = element.getArrayInitializer();
		if(arrayInitializationExpression == null)
		{
			return null;
		}

		if(element.toTypeRef(true) == DotNetTypeRef.ERROR_TYPE)
		{
			return newBuilder(element);
		}
		return null;
	}
}
