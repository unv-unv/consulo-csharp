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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.EmptyStub;
import com.intellij.util.IncorrectOperationException;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpIdentifier;
import consulo.csharp.lang.psi.CSharpStubElements;
import consulo.csharp.lang.psi.CSharpTokens;
import consulo.csharp.lang.psi.CSharpTypeDefStatement;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public class CSharpTypeDefStatementImpl extends CSharpStubElementImpl<EmptyStub<CSharpTypeDefStatement>> implements CSharpTypeDefStatement
{
	public CSharpTypeDefStatementImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public CSharpTypeDefStatementImpl(@Nonnull EmptyStub<CSharpTypeDefStatement> stub)
	{
		super(stub, CSharpStubElements.TYPE_DEF_STATEMENT);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public PsiElement getUsingKeywordElement()
	{
		return findNotNullChildByType(CSharpTokens.USING_KEYWORD);
	}

	@Override
	@RequiredReadAction
	public String getName()
	{
		CSharpIdentifier nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? null : nameIdentifier.getValue();
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitTypeDefStatement(this);
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@RequiredReadAction
	@Override
	@Nullable
	public DotNetType getType()
	{
		return getStubOrPsiChildByIndex(CSharpStubElements.TYPE_SET, 0);
	}

	@RequiredReadAction
	@Override
	@Nonnull
	public DotNetTypeRef toTypeRef()
	{
		DotNetType type = getType();
		return type == null ? DotNetTypeRef.ERROR_TYPE : type.toTypeRef();
	}

	@Nullable
	@Override
	@RequiredReadAction
	public CSharpIdentifier getNameIdentifier()
	{
		return getStubOrPsiChild(CSharpStubElements.IDENTIFIER);
	}

	@Nullable
	@Override
	@RequiredReadAction
	public PsiElement getReferenceElement()
	{
		return getType();
	}

	@RequiredReadAction
	@Override
	public int getTextOffset()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? super.getTextOffset() : nameIdentifier.getTextOffset();
	}
}
