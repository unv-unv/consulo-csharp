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

package consulo.csharp.lang.psi.impl.source.resolve.methodResolving;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import consulo.csharp.lang.psi.impl.source.resolve.WeightUtil;
import consulo.csharp.lang.psi.impl.source.resolve.methodResolving.arguments.NCallArgument;
import com.intellij.openapi.util.UserDataHolderBase;

/**
 * @author VISTALL
 * @since 02.11.14
 */
public class MethodCalcResult extends UserDataHolderBase
{
	public static final MethodCalcResult VALID = new MethodCalcResult(true, WeightUtil.MAX_WEIGHT, Collections.<NCallArgument>emptyList());

	private final boolean myValid;
	private final int myWeight;
	private final List<NCallArgument> myArguments;

	public MethodCalcResult(boolean valid, int weight, List<NCallArgument> arguments)
	{
		myValid = valid;
		myWeight = weight;
		myArguments = arguments;
	}

	@Nonnull
	public MethodCalcResult dupNoResult(int weight)
	{
		return new MethodCalcResult(false, getWeight() + weight, getArguments());
	}

	@Nonnull
	public MethodCalcResult dupWithResult(int weight)
	{
		return new MethodCalcResult(myValid, getWeight() + weight, getArguments());
	}

	public boolean isValidResult()
	{
		return myValid;
	}

	public int getWeight()
	{
		return myWeight;
	}

	public List<NCallArgument> getArguments()
	{
		return myArguments;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		MethodCalcResult that = (MethodCalcResult) o;

		if(myValid != that.myValid)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return (myValid ? 1 : 0);
	}
}
