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

package consulo.csharp.ide.debugger.expressionEvaluator;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.ide.debugger.CSharpEvaluateContext;
import consulo.dotnet.psi.DotNetVariable;
import consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import consulo.dotnet.debugger.proxy.DotNetAbsentInformationException;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetInvalidStackFrameException;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 27.03.2016
 */
public abstract class LocalVariableOrParameterEvaluator<T extends DotNetVariable> extends Evaluator
{
	protected final String myName;

	@RequiredReadAction
	public LocalVariableOrParameterEvaluator(@NotNull T variable)
	{
		myName = variable.getName();
	}

	@Override
	public void evaluate(@NotNull CSharpEvaluateContext context) throws DotNetInvalidObjectException, DotNetInvalidStackFrameException, DotNetAbsentInformationException
	{
		DotNetStackFrameProxy frame = context.getFrame();
		DotNetSourceLocation sourceLocation = frame.getSourceLocation();
		if(sourceLocation == null)
		{
			throw new IllegalArgumentException("no source position");
		}
		DotNetMethodProxy method = sourceLocation.getMethod();

		DotNetValueProxy thisObject = frame.getThisObject();
		DotNetValueProxy yieldOrAsyncThis = ThisObjectEvaluator.tryToFindObjectInsideYieldOrAsyncThis(frame, thisObject);
		if(yieldOrAsyncThis instanceof DotNetObjectValueProxy)
		{
			DotNetObjectValueProxy thisObjectAsObjectMirror = (DotNetObjectValueProxy) thisObject;
			DotNetFieldProxy localVariableField = null;
			DotNetTypeProxy type = thisObjectAsObjectMirror.getType();
			assert type != null;
			for(final DotNetFieldProxy field : type.getFields())
			{
				String name = DotNetDebuggerCompilerGenerateUtil.extractNotGeneratedName(field.getName());
				if(name == null)
				{
					continue;
				}
				if(myName.equals(name))
				{
					localVariableField = field;
					break;
				}
			}

			if(localVariableField != null)
			{
				DotNetValueProxy value = localVariableField.getValue(frame, thisObjectAsObjectMirror);
				if(value != null)
				{
					context.pull(value, localVariableField);
					return;
				}
			}
		}
		else if(tryEvaluateFromStackFrame(context, frame, method))
		{
			return;
		}
		throw new IllegalArgumentException("no variable with name '" + myName + "'");
	}

	protected abstract boolean tryEvaluateFromStackFrame(@NotNull CSharpEvaluateContext context, DotNetStackFrameProxy frame, DotNetMethodProxy method);
}
