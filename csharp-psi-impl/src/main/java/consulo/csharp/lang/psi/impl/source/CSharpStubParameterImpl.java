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
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.refactoring.CSharpRefactoringUtil;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpModifier;
import consulo.csharp.lang.psi.CSharpNamedElement;
import consulo.csharp.lang.psi.CSharpStubElements;
import consulo.csharp.lang.psi.impl.fragment.CSharpFragmentFactory;
import consulo.csharp.lang.psi.impl.fragment.CSharpFragmentFileImpl;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpRefTypeRef;
import consulo.csharp.lang.psi.impl.stub.CSharpVariableDeclStub;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetModifierList;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetParameterList;
import consulo.dotnet.psi.DotNetParameterListOwner;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpStubParameterImpl extends CSharpStubElementImpl<CSharpVariableDeclStub<DotNetParameter>> implements DotNetParameter, CSharpNamedElement
{
	private Ref<DotNetExpression> myInitializerExpression;

	public CSharpStubParameterImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public CSharpStubParameterImpl(@Nonnull CSharpVariableDeclStub<DotNetParameter> stub)
	{
		super(stub, CSharpStubElements.PARAMETER);
	}

	@Override
	protected void clearUserData()
	{
		super.clearUserData();
		myInitializerExpression = null;
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitParameter(this);
	}

	@RequiredReadAction
	@Override
	public boolean isConstant()
	{
		return false;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getConstantKeywordElement()
	{
		return null;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromInitializer)
	{
		DotNetType type = getType();
		DotNetTypeRef typeRef = type.toTypeRef();
		if(hasModifier(CSharpModifier.REF))
		{
			return new CSharpRefTypeRef(getProject(), CSharpRefTypeRef.Type.ref, typeRef);
		}
		else if(hasModifier(CSharpModifier.OUT))
		{
			return new CSharpRefTypeRef(getProject(), CSharpRefTypeRef.Type.out, typeRef);
		}
		return typeRef;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType getType()
	{
		return getRequiredStubOrPsiChildByIndex(CSharpStubElements.TYPE_SET, 0);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetExpression getInitializer()
	{
		CSharpVariableDeclStub<DotNetParameter> stub = getStub();
		if(stub != null)
		{
			if(myInitializerExpression != null)
			{
				return myInitializerExpression.get();
			}

			String initializerText = stub.getInitializerText();
			if(initializerText != null)
			{
				CSharpFragmentFileImpl expressionFragment = CSharpFragmentFactory.createExpressionFragment(getProject(), initializerText, this);
				DotNetExpression value = (DotNetExpression) expressionFragment.getChildren()[0];
				myInitializerExpression = Ref.create(value);
				return value;
			}

			return null;
		}

		myInitializerExpression = null;
		return findChildByClass(DotNetExpression.class);
	}

	@RequiredReadAction
	@Override
	@Nullable
	public DotNetModifierList getModifierList()
	{
		return getStubOrPsiChild(CSharpStubElements.MODIFIER_LIST);
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		if(modifier == CSharpModifier.OPTIONAL)
		{
			final CSharpVariableDeclStub<DotNetParameter> stub = getGreenStub();
			if(stub != null)
			{
				return stub.isOptional();
			}
			return getInitializer() != null;
		}

		DotNetModifierList modifierList = getModifierList();
		return modifierList != null && modifierList.hasModifier(modifier);
	}

	@Nullable
	@Override
	@RequiredReadAction
	public PsiElement getNameIdentifier()
	{
		return getStubOrPsiChild(CSharpStubElements.IDENTIFIER);
	}

	@RequiredReadAction
	@Override
	public int getTextOffset()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? super.getTextOffset() : nameIdentifier.getTextOffset();
	}

	@Override
	@RequiredReadAction
	public String getName()
	{
		return CSharpPsiUtilImpl.getNameWithoutAt(this);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getNameWithAt()
	{
		return CSharpPsiUtilImpl.getNameWithAt(this);
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		CSharpRefactoringUtil.replaceNameIdentifier(this, s);
		return this;
	}

	@Nullable
	@Override
	public DotNetParameterListOwner getOwner()
	{
		return getStubOrPsiParentOfType(DotNetLikeMethodDeclaration.class);
	}

	@Override
	public int getIndex()
	{
		DotNetParameterList parameterList = getStubOrPsiParentOfType(DotNetParameterList.class);
		assert parameterList != null;
		return ArrayUtil.indexOf(parameterList.getParameters(), this);
	}
}
