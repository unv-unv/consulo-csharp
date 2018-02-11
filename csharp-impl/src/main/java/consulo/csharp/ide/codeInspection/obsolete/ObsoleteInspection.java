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

package consulo.csharp.ide.codeInspection.obsolete;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.codeInspection.CSharpInspectionBundle;
import consulo.csharp.ide.projectView.CSharpElementTreeNode;
import consulo.csharp.lang.evaluator.ConstantExpressionEvaluator;
import consulo.csharp.lang.psi.CSharpAttribute;
import consulo.csharp.lang.psi.CSharpCallArgumentList;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpFieldOrPropertySet;
import consulo.csharp.lang.psi.CSharpNamedFieldOrPropertySet;
import consulo.csharp.lang.psi.CSharpReferenceExpression;
import consulo.csharp.lang.psi.CSharpTokenSets;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.psi.DotNetAttributeUtil;
import consulo.dotnet.psi.DotNetExpression;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.lang.stubbing.MsilCustomAttributeArgumentList;
import consulo.msil.lang.stubbing.MsilCustomAttributeStubber;
import consulo.msil.lang.stubbing.values.MsiCustomAttributeValue;

/**
 * @author VISTALL
 * @since 28.08.14
 */
public class ObsoleteInspection extends LocalInspectionTool
{
	@Nonnull
	@Override
	public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly)
	{
		return new CSharpElementVisitor()
		{
			@Override
			@RequiredReadAction
			public void visitReferenceExpression(CSharpReferenceExpression expression)
			{
				PsiElement resolve = expression.resolve();
				if(resolve == null)
				{
					return;
				}

				PsiElement referenceElement = expression.getReferenceElement();
				if(referenceElement == null)
				{
					return;
				}

				IElementType elementType = referenceElement.getNode().getElementType();
				if(CSharpTokenSets.KEYWORDS.contains(elementType))
				{
					return;
				}

				process(holder, referenceElement, resolve);
			}
		};
	}

	@RequiredReadAction
	private static void process(@Nonnull ProblemsHolder holder, @Nullable PsiElement range, @Nonnull PsiElement target)
	{
		if(range == null)
		{
			return;
		}

		// #hasAttribute() is cache result, #findAttribute() not
		if(DotNetAttributeUtil.hasAttribute(target, DotNetTypes.System.ObsoleteAttribute))
		{
			DotNetAttribute attribute = DotNetAttributeUtil.findAttribute(target, DotNetTypes.System.ObsoleteAttribute);
			if(attribute == null)
			{
				return;
			}
			String message = getMessage(attribute);
			if(message == null)
			{
				message = CSharpInspectionBundle.message("target.is.obsolete", CSharpElementTreeNode.getPresentableText((PsiNamedElement) target));
			}

			holder.registerProblem(range, message, ProblemHighlightType.LIKE_DEPRECATED);
		}
	}

	@RequiredReadAction
	public static String getMessage(DotNetAttribute attribute)
	{
		if(attribute instanceof CSharpAttribute)
		{
			CSharpCallArgumentList parameterList = ((CSharpAttribute) attribute).getParameterList();
			if(parameterList == null)
			{
				return null;
			}

			DotNetExpression[] expressions = parameterList.getExpressions();
			if(expressions.length == 0)
			{
				for(CSharpFieldOrPropertySet namedCallArgument : parameterList.getSets())
				{
					if(namedCallArgument instanceof CSharpNamedFieldOrPropertySet)
					{
						CSharpReferenceExpression argumentNameReference = (CSharpReferenceExpression) namedCallArgument.getNameElement();
						if("Message".equals(argumentNameReference.getReferenceName()))
						{
							return new ConstantExpressionEvaluator(namedCallArgument.getValueExpression()).getValueAs(String.class);
						}
					}
				}
			}
			else
			{
				DotNetExpression expression = expressions[0];
				return new ConstantExpressionEvaluator(expression).getValueAs(String.class);
			}
		}
		else if(attribute instanceof MsilCustomAttribute)
		{
			MsilCustomAttributeArgumentList attributeArgumentList = MsilCustomAttributeStubber.build((MsilCustomAttribute) attribute);

			List<MsiCustomAttributeValue> constructorArguments = attributeArgumentList.getConstructorArguments();
			if(!constructorArguments.isEmpty())
			{
				MsiCustomAttributeValue msiCustomAttributeValue = constructorArguments.get(0);

				Object value = msiCustomAttributeValue.getValue();
				if(value instanceof String)
				{
					return (String) value;
				}
			}
		}
		return null;
	}
}
