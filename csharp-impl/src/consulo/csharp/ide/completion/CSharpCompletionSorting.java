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

package consulo.csharp.ide.completion;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionSorter;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.completion.weigher.CSharpRecursiveGuardWeigher;
import consulo.csharp.lang.psi.CSharpCallArgumentListOwner;
import consulo.csharp.lang.psi.CSharpEnumConstantDeclaration;
import consulo.csharp.lang.psi.CSharpEventDeclaration;
import consulo.csharp.lang.psi.CSharpFieldDeclaration;
import consulo.csharp.lang.psi.CSharpLocalVariable;
import consulo.csharp.lang.psi.CSharpMethodDeclaration;
import consulo.csharp.lang.psi.CSharpPropertyDeclaration;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetVariable;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;

/**
 * @author VISTALL
 * @since 20.03.2016
 */
public class CSharpCompletionSorting
{
	public static class KindSorter extends LookupElementWeigher
	{
		private static Key<Type> ourForceType = Key.create("csharpOurForceType");

		public enum Type
		{
			top1,
			top2,
			delegate,
			localVariableOrParameter,
			parameterInCall,
			keyword,
			constants,
			member,
			overrideMember,
			preprocessorKeywords,
			any,
			hiddenKeywords,
			namespace,
		}

		protected KindSorter()
		{
			super("csharpKindSorter");
		}

		@Nullable
		@Override
		@RequiredReadAction
		public Type weigh(@NotNull LookupElement element)
		{
			Type type = element.getUserData(ourForceType);
			if(type != null)
			{
				return type;
			}

			IElementType keywordElementType = element.getUserData(CSharpCompletionUtil.KEYWORD_ELEMENT_TYPE);

			String lookupString = element.getLookupString();
			if(lookupString.startsWith("__") && keywordElementType != null)
			{
				return Type.hiddenKeywords;
			}
			else if(keywordElementType != null)
			{
				return Type.keyword;
			}
			else if(lookupString.startsWith("#"))
			{
				return Type.preprocessorKeywords;
			}

			PsiElement psiElement = element.getPsiElement();
			if(psiElement instanceof CSharpLocalVariable || psiElement instanceof DotNetParameter)
			{
				return Type.localVariableOrParameter;
			}

			if(psiElement instanceof CSharpFieldDeclaration && ((CSharpFieldDeclaration) psiElement).isConstant() || psiElement instanceof CSharpEnumConstantDeclaration)
			{
				return Type.constants;
			}

			if(psiElement instanceof CSharpPropertyDeclaration || psiElement instanceof CSharpEventDeclaration || psiElement instanceof CSharpFieldDeclaration || psiElement instanceof
					CSharpMethodDeclaration && !((CSharpMethodDeclaration) psiElement).isDelegate())
			{
				return Type.member;
			}

			if(psiElement instanceof DotNetNamespaceAsElement)
			{
				return Type.namespace;
			}
			return Type.any;
		}
	}

	public static void force(UserDataHolder holder, KindSorter.Type type)
	{
		holder.putUserData(KindSorter.ourForceType, type);
	}

	@Nullable
	public static KindSorter.Type getSort(UserDataHolder userDataHolder)
	{
		return userDataHolder.getUserData(KindSorter.ourForceType);
	}

	public static void copyForce(UserDataHolder from, UserDataHolder to)
	{
		KindSorter.Type data = from.getUserData(KindSorter.ourForceType);
		if(data != null)
		{
			to.putUserData(KindSorter.ourForceType, data);
		}
	}

	@Nullable
	@RequiredReadAction
	private static LookupElementWeigher recursiveSorter(CompletionParameters completionParameters, CompletionResultSet result)
	{
		PsiElement position = completionParameters.getPosition();

		PsiElement argumentListOwner = PsiTreeUtil.getContextOfType(position, CSharpCallArgumentListOwner.class, DotNetVariable.class);
		if(argumentListOwner != null)
		{
			return new CSharpRecursiveGuardWeigher(argumentListOwner);
		}
		return null;
	}

	@RequiredReadAction
	public static CompletionResultSet modifyResultSet(CompletionParameters completionParameters, CompletionResultSet result)
	{
		CompletionSorter sorter = CompletionSorter.defaultSorter(completionParameters, result.getPrefixMatcher());
		List<LookupElementWeigher> afterStats = new ArrayList<>();

		ContainerUtil.addIfNotNull(afterStats, recursiveSorter(completionParameters, result));
		afterStats.add(new KindSorter());

		sorter = sorter.weighAfter("stats", afterStats.toArray(new LookupElementWeigher[afterStats.size()]));
		result = result.withRelevanceSorter(sorter);
		return result;
	}
}
