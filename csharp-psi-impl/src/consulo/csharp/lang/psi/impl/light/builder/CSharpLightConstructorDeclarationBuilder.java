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
import consulo.csharp.lang.psi.CSharpConstructorDeclaration;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeRefByQName;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 17.05.14
 */
public class CSharpLightConstructorDeclarationBuilder extends CSharpLightLikeMethodDeclarationBuilder<CSharpLightConstructorDeclarationBuilder>
		implements CSharpConstructorDeclaration
{
	private DotNetTypeRef myTypeRef;

	@RequiredReadAction
	public CSharpLightConstructorDeclarationBuilder(PsiElement element)
	{
		super(element.getProject());
		myTypeRef = new CSharpTypeRefByQName(element, DotNetTypes.System.Void);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		return myTypeRef;
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitConstructorDeclaration(this);
	}

	@Override
	public boolean isDeConstructor()
	{
		return false;
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return null;
	}
}
