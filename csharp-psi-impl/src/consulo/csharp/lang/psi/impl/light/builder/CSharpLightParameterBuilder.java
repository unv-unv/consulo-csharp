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

package consulo.csharp.lang.psi.impl.light.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpModifier;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpRefTypeRef;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetParameterListOwner;
import consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 08.05.14
 */
public class CSharpLightParameterBuilder extends CSharpLightVariableBuilder<CSharpLightParameterBuilder> implements DotNetParameter
{
	private DotNetLikeMethodDeclaration myMethod;

	public CSharpLightParameterBuilder(Project project)
	{
		super(project);
	}

	public CSharpLightParameterBuilder(PsiElement element)
	{
		super(element);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromInitializer)
	{
		DotNetTypeRef typeRef = super.toTypeRef(resolveFromInitializer);
		if(hasModifier(CSharpModifier.REF))
		{
			return new CSharpRefTypeRef(CSharpRefTypeRef.Type.ref, typeRef);
		}
		else if(hasModifier(CSharpModifier.OUT))
		{
			return new CSharpRefTypeRef(CSharpRefTypeRef.Type.out, typeRef);
		}
		return typeRef;
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitParameter(this);
	}

	@Nullable
	@Override
	public DotNetParameterListOwner getOwner()
	{
		return myMethod;
	}

	@Override
	public int getIndex()
	{
		return 0;
	}

	public void setMethod(DotNetLikeMethodDeclaration method)
	{
		myMethod = method;
	}
}
