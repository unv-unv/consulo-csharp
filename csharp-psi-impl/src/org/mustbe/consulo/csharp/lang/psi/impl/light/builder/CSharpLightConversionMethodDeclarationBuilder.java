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
import org.mustbe.consulo.csharp.lang.psi.CSharpConversionMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpStaticTypeRef;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 15.12.14
 */
public class CSharpLightConversionMethodDeclarationBuilder  extends
		CSharpLightLikeMethodDeclarationBuilder<CSharpLightConversionMethodDeclarationBuilder> implements CSharpConversionMethodDeclaration
{
	private final CSharpStaticTypeRef myTypeRef;

	public CSharpLightConversionMethodDeclarationBuilder(Project project, CSharpStaticTypeRef typeRef)
	{
		super(project);
		myTypeRef = typeRef;
	}

	@Override
	public boolean isImplicit()
	{
		return getConversionTypeRef() == CSharpStaticTypeRef.IMPLICIT;
	}

	@NotNull
	@Override
	public DotNetTypeRef getConversionTypeRef()
	{
		return myTypeRef;
	}

	@Nullable
	@Override
	public DotNetType getConversionType()
	{
		return null;
	}

	@Nullable
	@Override
	public PsiElement getOperatorElement()
	{
		return null;
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitConversionMethodDeclaration(this);
	}
}
