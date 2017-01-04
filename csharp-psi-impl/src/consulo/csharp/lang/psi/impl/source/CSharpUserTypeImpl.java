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

package consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpReferenceExpression;
import consulo.csharp.lang.psi.CSharpUserType;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpUserTypeRef;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpUserTypeImpl extends CSharpTypeElementImpl implements CSharpUserType
{
	public CSharpUserTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}
	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitUserType(this);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		return new CSharpUserTypeRef(getReferenceExpression());
	}

	@NotNull
	@Override
	public DotNetPsiSearcher.TypeResoleKind getTypeResoleKind()
	{
		return DotNetPsiSearcher.TypeResoleKind.UNKNOWN;
	}

	@NotNull
	@Override
	public String getReferenceText()
	{
		return getReferenceExpression().getText();
	}

	@NotNull
	@Override
	public CSharpReferenceExpression getReferenceExpression()
	{
		return findNotNullChildByClass(CSharpReferenceExpression.class);
	}
}
