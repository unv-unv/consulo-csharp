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

package org.mustbe.consulo.csharp.lang.psi.impl.light.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.csharp.lang.psi.CSharpIndexMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 07.12.2014
 */
public class CSharpLightIndexMethodDeclarationBuilder extends CSharpLightLikeMethodDeclarationBuilder<CSharpLightIndexMethodDeclarationBuilder> implements CSharpIndexMethodDeclaration
{
	public CSharpLightIndexMethodDeclarationBuilder(Project project)
	{
		super(project);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitIndexMethodDeclaration(this);
	}

	@Nullable
	@Override
	public DotNetType getTypeForImplement()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		return DotNetTypeRef.ERROR_TYPE;
	}

	@Override
	public String getName()
	{
		return "[]";
	}

	@NotNull
	@Override
	public DotNetXXXAccessor[] getAccessors()
	{
		return DotNetXXXAccessor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return DotNetNamedElement.EMPTY_ARRAY;
	}

	@RequiredReadAction
	@Override
	public PsiElement getLeftBrace()
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public PsiElement getRightBrace()
	{
		return null;
	}
}
