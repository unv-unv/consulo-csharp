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

package consulo.csharp.lang.psi.impl.resolve;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import consulo.csharp.lang.CSharpLanguage;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpOperatorNameHelper;
import consulo.csharp.lang.psi.resolve.CSharpElementGroup;

/**
 * @author VISTALL
 * @since 29.09.14
 */
public class CSharpElementGroupImpl<T extends PsiElement> extends LightElement implements CSharpElementGroup<T>
{
	private final Object myKey;
	private final Collection<T> myElements;

	public CSharpElementGroupImpl(@Nonnull Project project, @Nonnull Object key, Collection<T> elements)
	{
		super(PsiManager.getInstance(project), CSharpLanguage.INSTANCE);
		myKey = key;
		myElements = elements;
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		for(T element : myElements)
		{
			ProgressManager.checkCanceled();

			if(!processor.execute(element, state))
			{
				return false;
			}
		}
		return true;
	}

	@Nonnull
	@Override
	public String getName()
	{
		if(myKey instanceof IElementType)
		{
			return CSharpOperatorNameHelper.getOperatorName((IElementType) myKey);
		}
		return myKey.toString();
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String name) throws IncorrectOperationException
	{
		for(T element : myElements)
		{
			if(element instanceof PsiNamedElement)
			{
				((PsiNamedElement) element).setName(name);
			}
		}
		return this;
	}

	@Nonnull
	@Override
	public Collection<T> getElements()
	{
		return myElements;
	}

	@Override
	public boolean process(@Nonnull Processor<? super T> processor)
	{
		for(T element : myElements)
		{
			ProgressManager.checkCanceled();

			if(!processor.process(element))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canNavigate()
	{
		return true;
	}

	@Override
	public void navigate(boolean requestFocus)
	{
		for(PsiElement element : getElements())
		{
			if(element instanceof Navigatable)
			{
				((Navigatable) element).navigate(requestFocus);
				break;
			}
		}
	}

	@Override
	@Nonnull
	public Object getKey()
	{
		return myKey;
	}

	@Override
	public String toString()
	{
		return "CSharpElementGroup: " + getName();
	}
}
