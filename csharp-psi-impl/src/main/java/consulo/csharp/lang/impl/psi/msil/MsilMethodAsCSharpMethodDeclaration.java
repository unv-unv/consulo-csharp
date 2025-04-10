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

package consulo.csharp.lang.impl.psi.msil;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.util.NullableLazyValue;
import consulo.csharp.lang.impl.psi.CSharpElementVisitor;
import consulo.csharp.lang.impl.psi.msil.typeParsing.SomeType;
import consulo.csharp.lang.impl.psi.msil.typeParsing.SomeTypeParser;
import consulo.csharp.lang.impl.psi.source.resolve.util.CSharpMethodImplUtil;
import consulo.csharp.lang.psi.*;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.lang.psi.MsilMethodEntry;
import consulo.util.lang.Pair;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilMethodAsCSharpMethodDeclaration extends MsilMethodAsCSharpLikeMethodDeclaration implements CSharpMethodDeclaration
{
	private static Map<String, Pair<String, IElementType>> ourOperatorNames = new HashMap<String, Pair<String, IElementType>>()
	{
		{
			put("op_Addition", Pair.create("+", CSharpTokens.PLUS));
			put("op_UnaryPlus", Pair.create("+", CSharpTokens.PLUS));
			put("op_Subtraction", Pair.create("-", CSharpTokens.MINUS));
			put("op_UnaryNegation", Pair.create("-", CSharpTokens.MINUS));
			put("op_Multiply", Pair.create("*", CSharpTokens.MUL));
			put("op_Division", Pair.create("/", CSharpTokens.DIV));
			put("op_Modulus", Pair.create("%", CSharpTokens.PERC));
			put("op_BitwiseAnd", Pair.create("&", CSharpTokens.AND));
			put("op_BitwiseOr", Pair.create("|", CSharpTokens.OR));
			put("op_ExclusiveOr", Pair.create("^", CSharpTokens.XOR));
			put("op_LeftShift", Pair.create("<<", CSharpTokens.LTLT));
			put("op_RightShift", Pair.create(">>", CSharpTokens.GTGT));
			put("op_Equality", Pair.create("==", CSharpTokens.EQEQ));
			put("op_Inequality", Pair.create("!=", CSharpTokens.NTEQ));
			put("op_LessThan", Pair.create("<", CSharpTokens.LT));
			put("op_LessThanOrEqual", Pair.create("<=", CSharpTokens.LTEQ));
			put("op_GreaterThan", Pair.create(">", CSharpTokens.GT));
			put("op_GreaterThanOrEqual", Pair.create(">=", CSharpTokens.GTEQ));
			put("op_OnesComplement", Pair.create("~", CSharpTokens.TILDE));
			put("op_LogicalNot", Pair.create("!", CSharpTokens.EXCL));
			put("op_Increment", Pair.create("++", CSharpTokens.PLUSPLUS));
			put("op_Decrement", Pair.create("--", CSharpTokens.MINUSMINUS));
		}
	};

	private final NullableLazyValue<String> myNameValue = new NullableLazyValue<String>()
	{
		@Nullable
		@Override
		protected String compute()
		{
			Pair<String, IElementType> pair = ourOperatorNames.get(myOriginal.getName());
			if(pair != null)
			{
				return pair.getFirst();
			}
			return myDelegate == null ? MsilMethodAsCSharpMethodDeclaration.super.getName() : MsilHelper.cutGenericMarker(myDelegate.getName());
		}
	};

	private final NullableLazyValue<DotNetType> myTypeForImplementValue;

	private final MsilClassEntry myDelegate;

	@RequiredReadAction
	public MsilMethodAsCSharpMethodDeclaration(PsiElement parent, @Nullable MsilClassEntry declaration, @Nonnull GenericParameterContext genericParameterContext, @Nonnull MsilMethodEntry methodEntry)
	{
		super(parent, CSharpModifier.EMPTY_ARRAY, methodEntry);
		myDelegate = declaration;

		setGenericParameterList(declaration != null ? declaration : methodEntry, genericParameterContext);

		myTypeForImplementValue = NullableLazyValue.of(() ->
		{
			String nameFromBytecode = myOriginal.getNameFromBytecode();
			String typeBeforeDot = StringUtil.getPackageName(nameFromBytecode);
			SomeType someType = SomeTypeParser.parseType(typeBeforeDot, nameFromBytecode);
			if(someType != null)
			{
				return new DummyType(getProject(), MsilMethodAsCSharpMethodDeclaration.this, someType);
			}
			return null;
		});
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitMethodDeclaration(this);
	}

	@RequiredReadAction
	@Override
	public String getName()
	{
		return myNameValue.getValue();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return myDelegate == null ? super.getPresentableParentQName() : myDelegate.getPresentableParentQName();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableQName()
	{
		return myDelegate == null ? MsilHelper.append(getPresentableParentQName(), getName()) : MsilHelper.cutGenericMarker(myDelegate.getPresentableQName());
	}

	@Nullable
	@Override
	public CSharpGenericConstraintList getGenericConstraintList()
	{
		return myGenericConstraintListValue.getValue();
	}

	@Nonnull
	@Override
	public CSharpGenericConstraint[] getGenericConstraints()
	{
		CSharpGenericConstraintList genericConstraintList = getGenericConstraintList();
		return genericConstraintList == null ? CSharpGenericConstraint.EMPTY_ARRAY : genericConstraintList.getGenericConstraints();
	}

	@Override
	public boolean isDelegate()
	{
		return myDelegate != null;
	}

	@RequiredReadAction
	@Override
	public boolean isOperator()
	{
		return ourOperatorNames.containsKey(myOriginal.getName());
	}

	@Override
	public boolean isExtension()
	{
		return CSharpMethodImplUtil.isExtensionMethod(this);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public IElementType getOperatorElementType()
	{
		Pair<String, IElementType> pair = ourOperatorNames.get(myOriginal.getName());
		if(pair == null)
		{
			return null;
		}
		return pair.getSecond();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetType getTypeForImplement()
	{
		return myTypeForImplementValue.getValue();
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public DotNetTypeRef getTypeRefForImplement()
	{
		DotNetType typeForImplement = getTypeForImplement();
		return typeForImplement != null ? typeForImplement.toTypeRef() : DotNetTypeRef.ERROR_TYPE;
	}

	@Nullable
	@Override
	protected Class<? extends PsiElement> getNavigationElementClass()
	{
		return CSharpMethodDeclaration.class;
	}

	@Nullable
	public MsilClassEntry getDelegate()
	{
		return myDelegate;
	}
}
