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

package consulo.csharp.impl.ide.findUsage.usageType;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.csharp.lang.impl.psi.source.CSharpAsExpressionImpl;
import consulo.csharp.lang.impl.psi.source.CSharpIsExpressionImpl;
import consulo.csharp.lang.impl.psi.source.CSharpTypeCastExpressionImpl;
import consulo.csharp.lang.impl.psi.source.CSharpTypeOfExpressionImpl;
import consulo.csharp.lang.psi.*;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.usage.UsageType;
import consulo.usage.UsageTypeProvider;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 01.11.14
 */
@ExtensionImpl
public class CSharpUsageTypeProvider implements UsageTypeProvider
{
	public static final UsageType AS_METHOD_REF = new UsageType("As method reference expression");
	public static final UsageType METHOD_CALL = new UsageType("Method call");
	public static final UsageType ATTRIBUTE = new UsageType("Attribute");
	public static final UsageType CLASS_IN_AS = new UsageType("Usage in 'as' expression");
	public static final UsageType CLASS_IN_IS = new UsageType("Usage in 'is' expression");
	public static final UsageType TYPE_OF_EXPRESSION = new UsageType("Usage in 'typeof' expression");

	@Nullable
	@Override
	@RequiredReadAction
	public UsageType getUsageType(PsiElement element)
	{
		if(element instanceof CSharpReferenceExpression)
		{
			PsiElement resolvedElement = ((CSharpReferenceExpression) element).resolve();
			if(resolvedElement == null)
			{
				return null;
			}
			CSharpReferenceExpression.ResolveToKind kind = ((CSharpReferenceExpression) element).kind();
			switch(kind)
			{
				case METHOD:
					return METHOD_CALL;
				case CONSTRUCTOR:
					if(element.getParent() instanceof DotNetAttribute)
					{
						return ATTRIBUTE;
					}
					return UsageType.CLASS_NEW_OPERATOR;
				case TYPE_LIKE:
					DotNetType type = PsiTreeUtil.getParentOfType(element, DotNetType.class);
					if(type == null)
					{
						return null;
					}
					PsiElement parent = type.getParent();
					if(parent instanceof CSharpLocalVariable)
					{
						return UsageType.CLASS_LOCAL_VAR_DECLARATION;
					}
					else if(parent instanceof CSharpFieldDeclaration)
					{
						return UsageType.CLASS_FIELD_DECLARATION;
					}
					else if(parent instanceof DotNetParameter)
					{
						return UsageType.CLASS_METHOD_PARAMETER_DECLARATION;
					}
					else if(parent instanceof CSharpSimpleLikeMethodAsElement)
					{
						return UsageType.CLASS_METHOD_RETURN_TYPE;
					}
					else if(parent instanceof CSharpTypeCastExpressionImpl)
					{
						return UsageType.CLASS_CAST_TO;
					}
					else if(parent instanceof CSharpAsExpressionImpl)
					{
						return CLASS_IN_AS;
					}
					else if(parent instanceof CSharpIsExpressionImpl)
					{
						return CLASS_IN_IS;
					}
					else if(parent instanceof CSharpTypeOfExpressionImpl)
					{
						return TYPE_OF_EXPRESSION;
					}
					break;
				case ANY_MEMBER:
					if(resolvedElement instanceof CSharpMethodDeclaration && !((CSharpMethodDeclaration) resolvedElement).isDelegate())
					{
						return AS_METHOD_REF;
					}
					break;
			}
		}
		return null;
	}
}
