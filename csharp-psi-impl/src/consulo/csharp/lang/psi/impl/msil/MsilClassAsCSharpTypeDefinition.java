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

package consulo.csharp.lang.psi.impl.msil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.CSharpGenericConstraint;
import consulo.csharp.lang.psi.CSharpGenericConstraintList;
import consulo.csharp.lang.psi.CSharpTypeDeclaration;
import consulo.csharp.lang.psi.impl.light.CSharpLightGenericConstraintList;
import consulo.csharp.lang.psi.impl.source.CSharpTypeDeclarationImplUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetGenericParameterList;
import consulo.dotnet.psi.DotNetInheritUtil;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetModifierList;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.psi.DotNetVariable;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.lombok.annotations.Lazy;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.lang.psi.MsilEventEntry;
import consulo.msil.lang.psi.MsilFieldEntry;
import consulo.msil.lang.psi.MsilMethodEntry;
import consulo.msil.lang.psi.MsilPropertyEntry;
import consulo.msil.lang.psi.MsilTokens;
import consulo.msil.lang.psi.MsilXXXAcessor;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilClassAsCSharpTypeDefinition extends MsilElementWrapper<MsilClassEntry> implements CSharpTypeDeclaration
{
	private NotNullLazyValue<DotNetNamedElement[]> myMembersValue = new NotNullLazyValue<DotNetNamedElement[]>()
	{
		@NotNull
		@Override
		@RequiredDispatchThread
		protected DotNetNamedElement[] compute()
		{
			MsilClassAsCSharpTypeDefinition parentThis = MsilClassAsCSharpTypeDefinition.this;

			DotNetNamedElement[] temp = myOriginal.getMembers();
			List<DotNetNamedElement> copy = new ArrayList<DotNetNamedElement>(temp.length);
			Collections.addAll(copy, temp);

			List<DotNetNamedElement> list = new ArrayList<DotNetNamedElement>(temp.length);

			boolean isEnum = isEnum();
			List<String> bannedFieldNames = new ArrayList<String>();
			for(DotNetNamedElement element : temp)
			{
				if(element instanceof MsilFieldEntry)
				{
					String name = element.getName();
					if(name == null)
					{
						continue;
					}

					if(StringUtil.containsAnyChar(name, "<>") || Comparing.equal(name, "value__") && isEnum)
					{
						bannedFieldNames.add(name);
					}
				}
				else if(element instanceof MsilEventEntry)
				{
					bannedFieldNames.add(element.getName());
				}
			}

			for(DotNetNamedElement element : temp)
			{
				if(element instanceof MsilPropertyEntry)
				{
					DotNetXXXAccessor[] accessors = ((MsilPropertyEntry) element).getAccessors();

					List<Pair<DotNetXXXAccessor, MsilMethodEntry>> pairs = new ArrayList<Pair<DotNetXXXAccessor, MsilMethodEntry>>(2);

					for(DotNetXXXAccessor accessor : accessors)
					{
						if(accessor instanceof MsilXXXAcessor)
						{
							MsilMethodEntry methodEntry = ((MsilXXXAcessor) accessor).resolveToMethod();
							if(methodEntry != null)
							{
								pairs.add(Pair.create(accessor, methodEntry));
								copy.remove(methodEntry);
							}
						}
					}

					if(!pairs.isEmpty())
					{
						Pair<DotNetXXXAccessor, MsilMethodEntry> value = pairs.get(0);

						if(value.getFirst().getAccessorKind() == DotNetXXXAccessor.Kind.GET && value.getSecond().getParameters().length == 1 || value.getFirst().getAccessorKind() ==
								DotNetXXXAccessor.Kind.SET && value.getSecond().getParameters().length == 2)
						{
							list.add(new MsilPropertyAsCSharpIndexMethodDeclaration(parentThis, (MsilPropertyEntry) element, pairs));
							continue;
						}
					}

					list.add(new MsilPropertyAsCSharpPropertyDeclaration(parentThis, (MsilPropertyEntry) element, pairs));
				}
				else if(element instanceof MsilEventEntry)
				{
					DotNetXXXAccessor[] accessors = ((MsilEventEntry) element).getAccessors();

					List<Pair<DotNetXXXAccessor, MsilMethodEntry>> pairs = new ArrayList<Pair<DotNetXXXAccessor, MsilMethodEntry>>(2);

					for(DotNetXXXAccessor accessor : accessors)
					{
						if(accessor instanceof MsilXXXAcessor)
						{
							MsilMethodEntry methodEntry = ((MsilXXXAcessor) accessor).resolveToMethod();
							if(methodEntry != null)
							{
								pairs.add(Pair.create(accessor, methodEntry));
								copy.remove(methodEntry);
							}
						}
					}
					list.add(new MsilEventAsCSharpEventDeclaration(parentThis, (MsilEventEntry) element, pairs));
				}
				else if(element instanceof MsilFieldEntry)
				{
					String name = element.getName();
					if(bannedFieldNames.contains(name))
					{
						continue;
					}

					if(isEnum)
					{
						list.add(new MsilFieldAsCSharpEnumConstantDeclaration(parentThis, (DotNetVariable) element));
					}
					else
					{
						list.add(new MsilFieldAsCSharpFieldDeclaration(parentThis, (DotNetVariable) element));
					}
				}
				else if(element instanceof MsilClassEntry)
				{
					list.add((DotNetNamedElement) MsilToCSharpUtil.wrap(element, parentThis, myGenericParameterContext.gemmate()));
				}
			}

			for(DotNetNamedElement member : copy)
			{
				if(member instanceof MsilMethodEntry)
				{
					String nameFromBytecode = ((MsilMethodEntry) member).getNameFromBytecode();
					if(Comparing.equal(nameFromBytecode, MsilHelper.STATIC_CONSTRUCTOR_NAME) || StringUtil.startsWith(nameFromBytecode, "<"))
					{
						continue;
					}
					if(MsilHelper.CONSTRUCTOR_NAME.equals(nameFromBytecode))
					{
						list.add(new MsilMethodAsCSharpConstructorDeclaration(parentThis, MsilClassAsCSharpTypeDefinition.this, (MsilMethodEntry) member, false));
					}
					else if(Comparing.equal(nameFromBytecode, "op_Implicit") || Comparing.equal(nameFromBytecode, "op_Explicit"))
					{
						list.add(new MsilMethodAsCSharpConversionMethodDeclaration(parentThis, (MsilMethodEntry) member));
					}
					else
					{
						boolean isDeConstructor = Comparing.equal(nameFromBytecode, "Finalize") && ((MsilMethodEntry) member).hasModifier(MsilTokens.PROTECTED_KEYWORD);
						if(isDeConstructor)
						{
							list.add(new MsilMethodAsCSharpConstructorDeclaration(parentThis, MsilClassAsCSharpTypeDefinition.this, (MsilMethodEntry) member, true));
						}
						else
						{
							list.add(new MsilMethodAsCSharpMethodDeclaration(parentThis, null, new GenericParameterContext (null), (MsilMethodEntry) member));
						}
					}
				}
			}
			return list.isEmpty() ? DotNetNamedElement.EMPTY_ARRAY : list.toArray(new DotNetNamedElement[list.size()]);
		}
	};

	private final GenericParameterContext myGenericParameterContext;
	private final MsilModifierListToCSharpModifierList myModifierList;
	private final DotNetGenericParameterList myGenericParameterList;
	private final NullableLazyValue<CSharpLightGenericConstraintList> myGenericConstraintListValue = new NullableLazyValue<CSharpLightGenericConstraintList>()
	{
		@Nullable
		@Override
		@RequiredReadAction
		protected CSharpLightGenericConstraintList compute()
		{
			return MsilAsCSharpBuildUtil.buildConstraintList(getGenericParameterList());
		}
	};

	private Boolean myIsStruct;
	private Boolean myIsEnum;
	private Boolean myIsInterface;

	@RequiredReadAction
	public MsilClassAsCSharpTypeDefinition(@Nullable PsiElement parent, MsilClassEntry classEntry, @NotNull GenericParameterContext genericParameterContext)
	{
		super(parent, classEntry);
		myGenericParameterContext = genericParameterContext;
		myModifierList = new MsilModifierListToCSharpModifierList(this, classEntry.getModifierList());
		DotNetGenericParameterList genericParameterList = classEntry.getGenericParameterList();
		myGenericParameterList = MsilGenericParameterListAsCSharpGenericParameterList.build(this, genericParameterList, genericParameterContext);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitTypeDeclaration(this);
	}

	@Override
	public PsiFile getContainingFile()
	{
		return myOriginal.getContainingFile();
	}

	@RequiredReadAction
	@Override
	public String getVmQName()
	{
		return myOriginal.getVmQName();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getVmName()
	{
		return myOriginal.getVmName();
	}

	@Override
	public boolean isEquivalentTo(PsiElement another)
	{
		return CSharpTypeDeclarationImplUtil.isEquivalentTo(this, another);
	}

	@RequiredReadAction
	@Override
	public PsiElement getLeftBrace()
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public PsiElement getRightBrace()
	{
		return null;
	}

	@Nullable
	@Override
	public CSharpGenericConstraintList getGenericConstraintList()
	{
		return myGenericConstraintListValue.getValue();
	}

	@NotNull
	@Override
	public CSharpGenericConstraint[] getGenericConstraints()
	{
		CSharpGenericConstraintList genericConstraintList = getGenericConstraintList();
		return genericConstraintList == null ? CSharpGenericConstraint.EMPTY_ARRAY : genericConstraintList.getGenericConstraints();
	}

	@Override
	public boolean isInterface()
	{
		if(myIsInterface != null)
		{
			return myIsInterface;
		}
		return myIsInterface = myOriginal.isInterface();
	}

	@Override
	public boolean isStruct()
	{
		if(myIsStruct != null)
		{
			return myIsStruct;
		}
		return myIsStruct = myOriginal.isStruct();
	}

	@Override
	public boolean isEnum()
	{
		if(myIsEnum != null)
		{
			return myIsEnum;
		}
		return myIsEnum = myOriginal.isEnum();
	}

	@Override
	public boolean isNested()
	{
		return myOriginal.isNested();
	}

	@Nullable
	@Override
	public DotNetTypeList getExtendList()
	{
		return null;
	}

	@NotNull
	@Override
	@Lazy
	public DotNetTypeRef[] getExtendTypeRefs()
	{
		String vmQName = getVmQName();
		// hack
		if(DotNetTypes.System.Object.equals(vmQName))
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}
		DotNetTypeRef[] extendTypeRefs = myOriginal.getExtendTypeRefs();
		if(extendTypeRefs.length == 0)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}
		DotNetTypeRef[] typeRefs = new DotNetTypeRef[extendTypeRefs.length];
		for(int i = 0; i < typeRefs.length; i++)
		{
			typeRefs[i] = MsilToCSharpUtil.extractToCSharp(extendTypeRefs[i], myOriginal);
		}
		return typeRefs;
	}

	@RequiredReadAction
	@Override
	public boolean isInheritor(@NotNull String other, boolean deep)
	{
		return DotNetInheritUtil.isInheritor(this, other, deep);
	}

	@Override
	@Lazy
	public DotNetTypeRef getTypeRefForEnumConstants()
	{
		return MsilToCSharpUtil.extractToCSharp(myOriginal.getTypeRefForEnumConstants(), myOriginal);
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return myGenericParameterList;
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		return myGenericParameterList == null ? DotNetGenericParameter.EMPTY_ARRAY : myGenericParameterList.getParameters();
	}

	@Override
	public int getGenericParametersCount()
	{
		return myGenericParameterList == null ? 0 : myGenericParameterList.getGenericParametersCount();
	}

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return myMembersValue.getValue();
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		return myModifierList.hasModifier(modifier);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return myModifierList;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return myOriginal.getPresentableParentQName();
	}

	@Override
	public String getName()
	{
		return MsilHelper.cutGenericMarker(myOriginal.getName());
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableQName()
	{
		return MsilHelper.cutGenericMarker(myOriginal.getPresentableQName());
	}

	@Override
	public String toString()
	{
		return myOriginal.toString();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return myOriginal.getNameIdentifier();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Nullable
	@Override
	protected Class<? extends PsiElement> getNavigationElementClass()
	{
		return CSharpTypeDeclaration.class;
	}
}
