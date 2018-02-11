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
import com.intellij.openapi.project.Project;
import consulo.csharp.lang.psi.CSharpElementVisitor;
import consulo.csharp.lang.psi.impl.source.resolve.type.CSharpFastImplicitTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.resolve.DotNetArrayTypeRef;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import consulo.dotnet.resolve.DotNetTypeResolveResult;

/**
 * @author VISTALL
 * @since 30.12.14
 */
public class CSharpImplicitArrayInitializationExpressionImpl extends CSharpExpressionImpl implements DotNetExpression, CSharpArrayInitializerOwner
{
	private static class ImplicitArrayInitializationTypeRef extends DotNetTypeRefWithCachedResult implements CSharpFastImplicitTypeRef
	{
		protected ImplicitArrayInitializationTypeRef(Project project)
		{
			super(project);
		}

		@RequiredReadAction
		@Nonnull
		@Override
		public String toString()
		{
			return "{...}";
		}

		@RequiredReadAction
		@Nonnull
		@Override
		protected DotNetTypeResolveResult resolveResult()
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		@RequiredReadAction
		@Nullable
		@Override
		public DotNetTypeRef doMirror(@Nonnull DotNetTypeRef another, PsiElement scope)
		{
			if(another instanceof DotNetArrayTypeRef)
			{
				return another;
			}
			return null;
		}

		@Override
		public boolean isConversion()
		{
			return true;
		}
	}

	public CSharpImplicitArrayInitializationExpressionImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@Nonnull CSharpElementVisitor visitor)
	{
		visitor.visitImplicitArrayInitializationExpression(this);
	}

	@Nonnull
	public DotNetExpression[] getExpressions()
	{
		return findChildrenByClass(DotNetExpression.class);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl(boolean resolveFromParent)
	{
		return new ImplicitArrayInitializationTypeRef(getProject());
	}

	@Nullable
	@Override
	public CSharpArrayInitializerImpl getArrayInitializer()
	{
		return findChildByClass(CSharpArrayInitializerImpl.class);
	}
}
