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

package consulo.csharp.lang.psi;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiPolyVariantReference;
import consulo.util.lang.CharFilter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 17.05.14
 */
public interface CSharpReferenceExpression extends DotNetReferenceExpression, PsiPolyVariantReference,
		CSharpQualifiedNonReference
{
	public static enum ResolveToKind
	{
		GENERIC_PARAMETER_FROM_PARENT, // return generic parameter from parent
		QUALIFIED_NAMESPACE,  // namespace by fully qualified like 'System.Reflection' system is not searching from context
		SOFT_QUALIFIED_NAMESPACE, // same as QUALIFIED_NAMESPACE but - soft ref
		METHOD,
		ATTRIBUTE,  // return type declaration but ref can find without Attribute sufix
		NATIVE_TYPE_WRAPPER, // return type declaration of native type
		ARRAY_METHOD,
		TYPE_LIKE, // return generic parameter or delegated method or type declaration
		CONSTRUCTOR,
		ANY_MEMBER,
		FIELD_OR_PROPERTY,
		PARAMETER,
		THIS, // return type declaration of parent
		BASE,  // return type declaration super class of parent
		ROOT_NAMESPACE,  // root namespace - global keyword
		LABEL,
		BASE_CONSTRUCTOR,
		THIS_CONSTRUCTOR,
		PARAMETER_FROM_PARENT,
		NAMEOF,
		EXPRESSION_OR_TYPE_LIKE,
		TUPLE_PROPERTY; // tuple property (name: exp)

		@Nonnull
		public static final ResolveToKind[] VALUES = values();
	}

	public static enum AccessType
	{
		NONE,
		DOT,
		ARROW,
		COLONCOLON,
		NULLABLE_CALL,
		NESTED_TYPE;

		@Nonnull
		public static final AccessType[] VALUES = values();
	}

	String PLACEHOLDER = "_";

	CharFilter DEFAULT_REF_FILTER = ch -> !Character.isWhitespace(ch) && ch != '@';

	@RequiredReadAction
	default boolean isPlaceholderReference()
	{
		if(getQualifier() != null)
		{
			return false;
		}

		return PLACEHOLDER.equals(getReferenceName()) && resolve() == null;
	}

	@Nullable
	@Override
	@RequiredReadAction
	DotNetExpression getQualifier();

	@Nullable
	@RequiredReadAction
	PsiElement getReferenceElement();

	@Nonnull
	@RequiredReadAction
	ResolveToKind kind();

	@Nullable
	@RequiredReadAction
	DotNetTypeList getTypeArgumentList();

	@Nonnull
	@RequiredReadAction
	DotNetTypeRef[] getTypeArgumentListRefs();

	@RequiredReadAction
	boolean isGlobalElement();

	@Nullable
	@RequiredReadAction
	PsiElement getMemberAccessElement();

	@Nonnull
	@RequiredReadAction
	AccessType getMemberAccessType();
}
