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

package org.mustbe.consulo.csharp.lang.psi;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.codeStyle.CSharpCodeGenerationSettings;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpArrayTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpEmptyGenericWrapperTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNullTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpRefTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpStaticTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeRefFromGenericParameter;
import com.intellij.psi.PsiElement;
import com.intellij.util.BitUtil;
import com.intellij.util.PairFunction;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpDynamicTypeRef;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetGenericParameterListOwner;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.resolve.DotNetGenericExtractor;
import consulo.dotnet.resolve.DotNetPointerTypeRef;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeResolveResult;
import consulo.internal.dotnet.msil.decompiler.textBuilder.util.StubBlockUtil;
import consulo.msil.lang.psi.impl.type.MsilTypeResolveResult;

/**
 * @author VISTALL
 * @since 03.09.14
 */
public class CSharpTypeRefPresentationUtil
{
	public static Map<String, String> ourTypesAsKeywords = new HashMap<String, String>()
	{
		{
			put(DotNetTypes.System.Object, "object");
			put(DotNetTypes.System.String, "string");
			put(DotNetTypes.System.SByte, "sbyte");
			put(DotNetTypes.System.Byte, "byte");
			put(DotNetTypes.System.Int16, "short");
			put(DotNetTypes.System.UInt16, "ushort");
			put(DotNetTypes.System.Int32, "int");
			put(DotNetTypes.System.UInt32, "uint");
			put(DotNetTypes.System.Int64, "long");
			put(DotNetTypes.System.UInt64, "ulong");
			put(DotNetTypes.System.Single, "float");
			put(DotNetTypes.System.Double, "double");
			put(DotNetTypes.System.Char, "char");
			put(DotNetTypes.System.Void, "void");
			put(DotNetTypes.System.Boolean, "bool");
			put(DotNetTypes.System.Decimal, "decimal");
		}
	};

	public static final int QUALIFIED_NAME = 1 << 0;
	public static final int TYPE_KEYWORD = 1 << 1;
	public static final int NO_GENERIC_ARGUMENTS = 1 << 2;
	public static final int NO_REF = 1 << 3;
	public static final int NULL = 1 << 4;

	public static final int QUALIFIED_NAME_WITH_KEYWORD = QUALIFIED_NAME | TYPE_KEYWORD;

	@NotNull
	@RequiredReadAction
	public static String buildShortText(@NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope)
	{
		StringBuilder builder = new StringBuilder();
		appendTypeRef(scope, builder, typeRef, TYPE_KEYWORD);
		return builder.toString();
	}

	@NotNull
	@RequiredReadAction
	public static String buildText(@NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope)
	{
		StringBuilder builder = new StringBuilder();
		appendTypeRef(scope, builder, typeRef, QUALIFIED_NAME);
		return builder.toString();
	}

	@NotNull
	@RequiredReadAction
	public static String buildText(@NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope, int flags)
	{
		StringBuilder builder = new StringBuilder();
		appendTypeRef(scope, builder, typeRef, flags);
		return builder.toString();
	}

	@NotNull
	@RequiredReadAction
	public static String buildTextWithKeyword(@NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope)
	{
		StringBuilder builder = new StringBuilder();
		appendTypeRef(scope, builder, typeRef, QUALIFIED_NAME | TYPE_KEYWORD);
		return builder.toString();
	}

	@NotNull
	@RequiredReadAction
	public static String buildTextWithKeywordAndNull(@NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope)
	{
		StringBuilder builder = new StringBuilder();
		appendTypeRef(scope, builder, typeRef, QUALIFIED_NAME | TYPE_KEYWORD | NULL);
		return builder.toString();
	}

	@RequiredReadAction
	public static void appendTypeRef(@NotNull final PsiElement scope, @NotNull StringBuilder builder, @NotNull DotNetTypeRef typeRef, final int flags)
	{
		if(typeRef == DotNetTypeRef.AUTO_TYPE)
		{
			builder.append("var");
			return;
		}
		else if(typeRef instanceof CSharpNullTypeRef && BitUtil.isSet(flags, NULL))
		{
			builder.append("null");
			return;
		}

		if(typeRef instanceof CSharpStaticTypeRef || typeRef instanceof CSharpDynamicTypeRef)
		{
			builder.append(typeRef.toString());
		}
		else if(typeRef instanceof CSharpArrayTypeRef)
		{
			appendTypeRef(scope, builder, ((CSharpArrayTypeRef) typeRef).getInnerTypeRef(), flags);
			builder.append("[");
			for(int i = 0; i < ((CSharpArrayTypeRef) typeRef).getDimensions(); i++)
			{
				builder.append(",");
			}
			builder.append("]");
		}
		else if(typeRef instanceof CSharpRefTypeRef)
		{
			if(!BitUtil.isSet(flags, NO_REF))
			{
				builder.append(((CSharpRefTypeRef) typeRef).getType().name());
				builder.append(" ");
			}
			appendTypeRef(scope, builder, ((CSharpRefTypeRef) typeRef).getInnerTypeRef(), flags);
		}
		else if(typeRef instanceof CSharpEmptyGenericWrapperTypeRef)
		{
			appendTypeRef(scope, builder, ((CSharpEmptyGenericWrapperTypeRef) typeRef).getInnerTypeRef(), flags | NO_GENERIC_ARGUMENTS);
			builder.append("<>");
		}
		else if(typeRef instanceof DotNetPointerTypeRef)
		{
			appendTypeRef(scope, builder, ((DotNetPointerTypeRef) typeRef).getInnerTypeRef(), flags);
			builder.append("*");
		}
		else
		{
			DotNetTypeResolveResult typeResolveResult = typeRef.resolve();

			PsiElement element = typeResolveResult.getElement();
			boolean isNullable = CSharpTypeUtil.isNullableElement(element);
			boolean isExpectedNullable = typeResolveResult instanceof MsilTypeResolveResult || typeResolveResult.isNullable();

			if(element instanceof DotNetQualifiedElement)
			{
				String qName = ((DotNetQualifiedElement) element).getPresentableQName();
				String name = ((DotNetQualifiedElement) element).getName();

				String typeAsKeyword = CSharpCodeGenerationSettings.getInstance(scope.getProject()).USE_LANGUAGE_DATA_TYPES ? ourTypesAsKeywords.get(qName) : null;

				if(BitUtil.isSet(flags, QUALIFIED_NAME))
				{
					if(BitUtil.isSet(flags, TYPE_KEYWORD) && typeAsKeyword != null)
					{
						builder.append(typeAsKeyword);
					}
					else
					{
						builder.append(qName);
					}
				}
				else
				{
					if(BitUtil.isSet(flags, TYPE_KEYWORD) && typeAsKeyword != null)
					{
						builder.append(typeAsKeyword);
					}
					else
					{
						builder.append(name);
					}
				}
			}
			else
			{
				// fallback
				builder.append(typeRef.toString());
			}

			if(!BitUtil.isSet(flags, NO_GENERIC_ARGUMENTS))
			{
				if(element instanceof DotNetGenericParameterListOwner)
				{
					DotNetGenericParameter[] genericParameters = ((DotNetGenericParameterListOwner) element).getGenericParameters();
					if(genericParameters.length > 0)
					{
						final DotNetGenericExtractor genericExtractor = typeResolveResult.getGenericExtractor();
						builder.append("<");
						StubBlockUtil.join(builder, genericParameters, new PairFunction<StringBuilder, DotNetGenericParameter, Void>()
						{
							@Nullable
							@Override
							@RequiredReadAction
							public Void fun(StringBuilder t, DotNetGenericParameter v)
							{
								DotNetTypeRef extractedTypeRef = genericExtractor.extract(v);
								if(extractedTypeRef == null)
								{
									extractedTypeRef = new CSharpTypeRefFromGenericParameter(v);
								}
								appendTypeRef(scope, t, extractedTypeRef, flags);
								return null;
							}
						}, ", ");
						builder.append(">");
					}
				}
			}
			if(isExpectedNullable && !isNullable)
			{
				builder.append("?");
			}
		}
	}
}
