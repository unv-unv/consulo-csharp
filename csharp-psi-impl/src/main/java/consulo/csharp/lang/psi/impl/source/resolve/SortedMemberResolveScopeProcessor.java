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

package consulo.csharp.lang.psi.impl.source.resolve;

import java.util.Comparator;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.ResolveResult;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 27.07.2015
 */
public class SortedMemberResolveScopeProcessor extends MemberResolveScopeProcessor
{
	private final Processor<ResolveResult> myOriginalProcessor;
	private Comparator<ResolveResult> myComparator;

	public SortedMemberResolveScopeProcessor(@Nonnull CSharpResolveOptions options,
			@Nonnull Processor<ResolveResult> resultProcessor,
			@Nonnull Comparator<ResolveResult> comparator,
			ExecuteTarget[] targets)
	{
		super(options, CommonProcessors.<ResolveResult>alwaysTrue(), targets);
		myOriginalProcessor = resultProcessor;
		myComparator = comparator;
		initThisProcessor();
	}

	private void initThisProcessor()
	{
		myResultProcessor = new CommonProcessors.CollectProcessor<>(new LinkedHashSet<>());
	}

	public void consumeAll()
	{
		ResolveResult[] resolveResults = ((CommonProcessors.CollectProcessor<ResolveResult>) myResultProcessor).toArray(ResolveResult.ARRAY_FACTORY);

		ContainerUtil.sort(resolveResults, myComparator);

		for(ResolveResult result : resolveResults)
		{
			ProgressManager.checkCanceled();

			if(!myOriginalProcessor.process(result))
			{
				return;
			}
		}
	}
}
