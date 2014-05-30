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

package org.mustbe.consulo.csharp.lang.psi.impl.msil;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpArrayTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeTypeRef;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.lang.psi.DotNetInheritUtil;
import org.mustbe.consulo.dotnet.lang.psi.impl.source.resolve.type.DotNetGenericWrapperTypeRef;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.MsilEntry;
import org.mustbe.consulo.msil.lang.psi.MsilMethodEntry;
import org.mustbe.consulo.msil.lang.psi.MsilModifierElementType;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.type.MsilArrayTypRefImpl;
import org.mustbe.consulo.msil.lang.psi.impl.type.MsilNativeTypeRefImpl;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ConcurrentHashMap;
import com.intellij.util.containers.ContainerUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilToCSharpUtil
{
	@Nullable
	public static MsilModifierElementType toMsilModifier(DotNetModifier modifier)
	{
		if(modifier == DotNetModifier.STATIC)
		{
			return MsilTokens.STATIC_KEYWORD;
		}
		if(modifier instanceof CSharpModifier)
		{
			switch((CSharpModifier) modifier)
			{
				case PUBLIC:
					return MsilTokens.PUBLIC_KEYWORD;
				case PRIVATE:
					return MsilTokens.PRIVATE_KEYWORD;
				case PROTECTED:
					return MsilTokens.PROTECTED_KEYWORD;
				case STATIC:
					return MsilTokens.STATIC_KEYWORD;
				case SEALED:
					return MsilTokens.SEALED_KEYWORD;
				case READONLY:
					break;
				case UNSAFE:
					break;
				case PARAMS: //TODO [VISTALL] handle System.ParamArrayAttribute
					break;
				case THIS:  //TODO [VISTALL] handle System.Runtime.CompilerServices.ExtensionAttribute
					break;
				case ABSTRACT:
					return MsilTokens.ABSTRACT_KEYWORD;
				case PARTIAL:
					break;
			}
		}
		return null;
	}

	private static Map<MsilEntry, PsiElement> ourCache = new ConcurrentHashMap<MsilEntry, PsiElement>();

	@Nullable
	public static PsiElement wrap(PsiElement element)
	{
		if(element instanceof MsilClassEntry)
		{
			PsiElement cache = ourCache.get(element);
			if(cache != null)
			{
				return cache;
			}

			if(DotNetInheritUtil.isInheritor((MsilClassEntry) element, DotNetTypes.System_MulticastDelegate, true))
			{
				val msilMethodEntry = (MsilMethodEntry) ContainerUtil.find(((MsilClassEntry) element).getMembers(),
						new Condition<DotNetNamedElement>()
				{
					@Override
					public boolean value(DotNetNamedElement element)
					{
						return element instanceof MsilMethodEntry && Comparing.equal(element.getName(), "Invoke");
					}
				});

				assert msilMethodEntry != null : ((MsilClassEntry) element).getPresentableQName();

				cache = new MsilMethodAsCSharpMethodDefinition((MsilClassEntry) element, msilMethodEntry);
			}
			else
			{
				cache = new MsilClassAsCSharpTypeDefinition((MsilClassEntry) element);
			}
			ourCache.put((MsilClassEntry) element, cache);
			return cache;
		}
		return element;
	}

	public static DotNetTypeRef extractToCSharp(DotNetTypeRef typeRef, PsiElement scope)
	{
		if(typeRef == DotNetTypeRef.ERROR_TYPE)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}

		if(typeRef instanceof MsilNativeTypeRefImpl)
		{
			IElementType elementType = ((MsilNativeTypeRefImpl) typeRef).getElementType();
			if(elementType == MsilTokens.INT8_KEYWORD)
			{
				return CSharpNativeTypeRef.SBYTE;
			}
			else if(elementType == MsilTokens.UINT8_KEYWORD)
			{
				return CSharpNativeTypeRef.BYTE;
			}
			else if(elementType == MsilTokens.INT16_KEYWORD)
			{
				return CSharpNativeTypeRef.SHORT;
			}
			else if(elementType == MsilTokens.UINT16_KEYWORD)
			{
				return CSharpNativeTypeRef.USHORT;
			}
			else if(elementType == MsilTokens.INT32_KEYWORD)
			{
				return CSharpNativeTypeRef.INT;
			}
			else if(elementType == MsilTokens.UINT32_KEYWORD)
			{
				return CSharpNativeTypeRef.UINT;
			}
			else if(elementType == MsilTokens.INT64_KEYWORD)
			{
				return CSharpNativeTypeRef.LONG;
			}
			else if(elementType == MsilTokens.UINT64_KEYWORD)
			{
				return CSharpNativeTypeRef.ULONG;
			}
			else if(elementType == MsilTokens.STRING_KEYWORD)
			{
				return CSharpNativeTypeRef.STRING;
			}
			else if(elementType == MsilTokens.OBJECT_KEYWORD)
			{
				return CSharpNativeTypeRef.OBJECT;
			}
			else if(elementType == MsilTokens.CHAR_KEYWORD)
			{
				return CSharpNativeTypeRef.CHAR;
			}
			else if(elementType == MsilTokens.BOOL_KEYWORD)
			{
				return CSharpNativeTypeRef.BOOL;
			}
			else if(elementType == MsilTokens.FLOAT_KEYWORD)
			{
				return CSharpNativeTypeRef.FLOAT;
			}
			else if(elementType == MsilTokens.FLOAT64_KEYWORD)
			{
				return CSharpNativeTypeRef.DOUBLE;
			}
			else if(elementType == MsilTokens.VOID_KEYWORD)
			{
				return CSharpNativeTypeRef.VOID;
			}
		}
		else if(typeRef instanceof MsilArrayTypRefImpl)
		{
			return new CSharpArrayTypeRef(extractToCSharp(((MsilArrayTypRefImpl) typeRef).getInnerType(), scope), 0);
		}
		else if(typeRef instanceof DotNetGenericWrapperTypeRef)
		{
			val inner = extractToCSharp(((DotNetGenericWrapperTypeRef) typeRef).getInner(), scope);
			DotNetTypeRef[] arguments = ((DotNetGenericWrapperTypeRef) typeRef).getArguments();
			DotNetTypeRef[] newArguments = new DotNetTypeRef[arguments.length];
			for(int i = 0; i < newArguments.length; i++)
			{
				newArguments[i] = extractToCSharp(arguments[i], scope);
			}
			return new DotNetGenericWrapperTypeRef(inner, arguments);
		}
		return new MsilDelegateTypeRef(typeRef);
	}
}
