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

package consulo.csharp.ide.liveTemplates.macro;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.CSharpLookupElementBuilder;
import consulo.dotnet.psi.DotNetVariable;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Macro;
import com.intellij.codeInsight.template.PsiElementResult;
import com.intellij.codeInsight.template.Result;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @author ven
 *         <p/>
 *         from java plugin
 * @since 11.06.14
 *        <p/>
 *        base code by
 */
public abstract class VariableTypeMacroBase extends Macro
{
	@Nullable
	protected abstract PsiElement[] getVariables(Expression[] params, final ExpressionContext context);

	@Override
	@RequiredReadAction
	public LookupElement[] calculateLookupItems(@Nonnull Expression[] params, final ExpressionContext context)
	{
		final PsiElement[] vars = getVariables(params, context);
		if(vars == null || vars.length < 2)
		{
			return null;
		}
		return CSharpLookupElementBuilder.buildToLookupElements(vars);
	}

	@Override
	public Result calculateResult(@Nonnull Expression[] params, ExpressionContext context)
	{
		final PsiElement[] vars = getVariables(params, context);
		if(vars == null || vars.length == 0)
		{
			return null;
		}
		return new PsiElementResult(vars[0])
		{
			@Override
			public String toString()
			{
				PsiElement element = getElement();
				if(element instanceof DotNetVariable)
				{
					return ((DotNetVariable) element).getName();
				}
				return super.toString();
			}
		};
	}

	@Override
	@Nonnull
	public String getDefaultValue()
	{
		return "a";
	}
}
