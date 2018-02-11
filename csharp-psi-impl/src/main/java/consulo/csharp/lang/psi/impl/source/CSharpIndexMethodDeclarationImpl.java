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
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpIndexMethodDeclaration;
import consulo.csharp.lang.psi.CSharpSimpleParameterInfo;
import consulo.csharp.lang.psi.CSharpStubElements;
import consulo.csharp.lang.psi.CSharpTokens;
import consulo.csharp.lang.psi.impl.stub.CSharpIndexMethodDeclStub;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetGenericParameterList;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetParameterList;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 01.03.14
 */
public class CSharpIndexMethodDeclarationImpl extends CSharpStubMemberImpl<CSharpIndexMethodDeclStub> implements CSharpIndexMethodDeclaration
{
	public CSharpIndexMethodDeclarationImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public CSharpIndexMethodDeclarationImpl(@Nonnull CSharpIndexMethodDeclStub stub)
	{
		super(stub, CSharpStubElements.INDEX_METHOD_DECLARATION);
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitIndexMethodDeclaration(this);
	}

	@Nonnull
	@Override
	public DotNetXXXAccessor[] getAccessors()
	{
		return getStubOrPsiChildren(CSharpStubElements.XXX_ACCESSOR, DotNetXXXAccessor.ARRAY_FACTORY);
	}

	@Nonnull
	@Override
	public DotNetType getReturnType()
	{
		return getRequiredStubOrPsiChildByIndex(CSharpStubElements.TYPE_SET, 0);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		DotNetType type = getReturnType();
		return type.toTypeRef();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public CSharpSimpleParameterInfo[] getParameterInfos()
	{
		return CSharpLikeMethodDeclarationImplUtil.getParametersInfos(this);
	}

	@Nonnull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return getAccessors();
	}

	@Nullable
	@Override
	public DotNetParameterList getParameterList()
	{
		return getStubOrPsiChild(CSharpStubElements.PARAMETER_LIST);
	}

	@Nonnull
	@Override
	public DotNetParameter[] getParameters()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetParameter.EMPTY_ARRAY : parameterList.getParameters();
	}

	@Nonnull
	@Override
	public DotNetTypeRef[] getParameterTypeRefs()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetTypeRef.EMPTY_ARRAY : parameterList.getParameterTypeRefs();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(CSharpTokens.THIS_KEYWORD);
	}

	@RequiredReadAction
	@Override
	public String getName()
	{
		String singleAttributeValue = null;/* CSharpAttributeUtil.findSingleAttributeValue(this, DotNetTypes.System.Runtime.CompilerServices
		.IndexerName, String.class);  */
		//TODO [VISTALL] fix stackoverflow and uncomment
		return StringUtil.notNullize(singleAttributeValue, "Item");
	}

	@Nullable
	@Override
	public PsiElement getCodeBlock()
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return null;
	}

	@Nonnull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		return DotNetGenericParameter.EMPTY_ARRAY;
	}

	@Override
	public int getGenericParametersCount()
	{
		return 0;
	}

	@Nullable
	@Override
	public DotNetType getTypeForImplement()
	{
		return getStubOrPsiChildByIndex(CSharpStubElements.TYPE_SET, 1);
	}

	@Nonnull
	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		DotNetType typeForImplement = getTypeForImplement();
		if(typeForImplement == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		else
		{
			return typeForImplement.toTypeRef();
		}
	}

	@RequiredReadAction
	@Override
	public PsiElement getLeftBrace()
	{
		return findChildByType(CSharpTokens.LBRACE);
	}

	@RequiredReadAction
	@Override
	public PsiElement getRightBrace()
	{
		return findChildByType(CSharpTokens.RBRACE);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor,
			@Nonnull ResolveState state,
			PsiElement lastParent,
			@Nonnull PsiElement place)
	{
		return CSharpLikeMethodDeclarationImplUtil.processDeclarations(this, processor, state, lastParent, place);
	}
}
