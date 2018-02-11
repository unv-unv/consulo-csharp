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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.lang.psi.*;
import consulo.csharp.lang.psi.impl.light.builder.CSharpLightLocalVariableBuilder;
import consulo.csharp.lang.psi.impl.source.resolve.ExecuteTarget;
import consulo.csharp.lang.psi.impl.source.resolve.ExecuteTargetUtil;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeRefByQName;
import consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import consulo.csharp.lang.psi.impl.stub.CSharpXXXAccessorStub;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetModifierList;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetStatement;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpXXXAccessorImpl extends CSharpStubMemberImpl<CSharpXXXAccessorStub> implements DotNetXXXAccessor, CSharpSimpleLikeMethodAsElement
{
	public CSharpXXXAccessorImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public CSharpXXXAccessorImpl(@Nonnull CSharpXXXAccessorStub stub)
	{
		super(stub, CSharpStubElements.XXX_ACCESSOR);
	}


	@RequiredReadAction
	@Nonnull
	@Override
	public CSharpSimpleParameterInfo[] getParameterInfos()
	{
		return CSharpSimpleParameterInfo.EMPTY_ARRAY;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		if(getAccessorKind() == Kind.GET)
		{
			Pair<DotNetTypeRef, ? extends PsiElement> typeRefOfParent = getTypeRefOfParent();
			return typeRefOfParent.getFirst();
		}
		return new CSharpTypeRefByQName(this, DotNetTypes.System.Void);
	}

	@RequiredReadAction
	@Override
	@Nullable
	public DotNetModifierList getModifierList()
	{
		return findChildByClass(DotNetModifierList.class);
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		DotNetModifierList modifierList = getModifierList();
		return modifierList != null && modifierList.hasModifier(modifier);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public PsiElement getNameIdentifier()
	{
		return findNotNullChildByType(CSharpTokenSets.XXX_ACCESSOR_START);
	}

	@RequiredReadAction
	@Override
	public String getName()
	{
		Kind accessorKind = getAccessorKind();
		if(accessorKind == null)
		{
			return "null";
		}
		return accessorKind.name().toLowerCase(Locale.US);
	}

	@Nonnull
	private Pair<DotNetTypeRef, DotNetQualifiedElement> getTypeRefOfParent()
	{
		CSharpXXXAccessorOwner element = PsiTreeUtil.getParentOfType(this, CSharpXXXAccessorOwner.class);
		if(element == null)
		{
			return Pair.create(DotNetTypeRef.ERROR_TYPE, null);
		}

		DotNetTypeRef typeRef = DotNetTypeRef.ERROR_TYPE;
		if(element instanceof CSharpPropertyDeclaration)
		{
			typeRef = ((CSharpPropertyDeclaration) element).toTypeRef(false);
		}
		else if(element instanceof CSharpEventDeclaration)
		{
			typeRef = ((CSharpEventDeclaration) element).toTypeRef(false);
		}
		else if(element instanceof CSharpIndexMethodDeclaration)
		{
			typeRef = ((CSharpIndexMethodDeclaration) element).getReturnTypeRef();
		}
		return Pair.create(typeRef, (DotNetQualifiedElement) element);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor,
			@Nonnull ResolveState state,
			PsiElement lastParent,
			@Nonnull PsiElement place)
	{
		if(ExecuteTargetUtil.canProcess(processor, ExecuteTarget.LOCAL_VARIABLE_OR_PARAMETER))
		{
			PsiElement parent = getParent();
			if(!parent.processDeclarations(processor, state, lastParent, place))
			{
				return false;
			}

			Kind accessorKind = getAccessorKind();
			if(accessorKind == Kind.SET || accessorKind == Kind.ADD || accessorKind == Kind.REMOVE)
			{
				Pair<DotNetTypeRef, DotNetQualifiedElement> pair = getTypeRefOfParent();
				if(pair.getSecond() == null)
				{
					return true;
				}

				CSharpLightLocalVariableBuilder builder = new CSharpLightLocalVariableBuilder(pair.getSecond()).withName(VALUE).withParent(this)
						.withTypeRef(pair.getFirst());

				builder.putUserData(CSharpResolveUtil.ACCESSOR_VALUE_VARIABLE_OWNER, pair.getSecond());

				if(!processor.execute(builder, state))
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitXXXAccessor(this);
	}

	@Nullable
	@Override
	public PsiElement getCodeBlock()
	{
		return findChildByClass(DotNetStatement.class);
	}

	@Nullable
	@Override
	public PsiElement getAccessorElement()
	{
		return getNameIdentifier();
	}

	@Nullable
	@Override
	public Kind getAccessorKind()
	{
		CSharpXXXAccessorStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getAccessorType();
		}
		IElementType elementType = getNameIdentifier().getNode().getElementType();
		if(elementType == CSharpSoftTokens.GET_KEYWORD)
		{
			return Kind.GET;
		}
		else if(elementType == CSharpSoftTokens.SET_KEYWORD)
		{
			return Kind.SET;
		}
		else if(elementType == CSharpSoftTokens.ADD_KEYWORD)
		{
			return Kind.ADD;
		}
		else if(elementType == CSharpSoftTokens.REMOVE_KEYWORD)
		{
			return Kind.REMOVE;
		}
		return null;
	}
}
