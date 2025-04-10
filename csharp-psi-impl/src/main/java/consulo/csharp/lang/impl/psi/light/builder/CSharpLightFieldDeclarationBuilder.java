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

package consulo.csharp.lang.impl.psi.light.builder;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import consulo.csharp.lang.impl.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpFieldDeclaration;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;

/**
 * @author VISTALL
 * @since 08.05.14
 */
public class CSharpLightFieldDeclarationBuilder extends CSharpLightVariableBuilder<CSharpLightFieldDeclarationBuilder> implements
		CSharpFieldDeclaration
{
	private PsiElement myNameIdentifier;

	public CSharpLightFieldDeclarationBuilder(PsiElement element)
	{
		super(element);
	}

	public CSharpLightFieldDeclarationBuilder(Project manager)
	{
		super(manager);
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		PsiElement parent = getParent();
		if(parent instanceof DotNetQualifiedElement)
		{
			return ((DotNetQualifiedElement) parent).getPresentableQName();
		}
		return "";
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		String parentQName = getPresentableParentQName();
		if(StringUtil.isEmpty(parentQName))
		{
			return getName();
		}
		return parentQName + "." + getName();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return myNameIdentifier;
	}

	@Override
	public int getTextOffset()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? super.getTextOffset() : nameIdentifier.getTextOffset();
	}

	@Override
	public String getName()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		if(nameIdentifier != null)
		{
			return nameIdentifier.getText();
		}
		return super.getName();
	}

	public void withNameIdentifier(@Nonnull PsiElement element)
	{
		myNameIdentifier = element;
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitFieldDeclaration(this);
	}
}
