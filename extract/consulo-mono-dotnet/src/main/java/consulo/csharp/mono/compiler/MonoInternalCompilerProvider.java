/*
 * Copyright 2013-2016 must-be.org
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

package consulo.csharp.mono.compiler;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.util.SystemInfo;
import consulo.content.bundle.Sdk;
import consulo.csharp.base.compiler.BaseInternalCompilerProvider;
import consulo.csharp.compiler.MSBaseDotNetCompilerOptionsBuilder;
import consulo.csharp.module.extension.CSharpModuleExtension;
import consulo.dotnet.compiler.DotNetCompileFailedException;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.mono.dotnet.sdk.MonoSdkType;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 01.01.2016
 */
@ExtensionImpl(id = "mono-internal", order = "first")
public class MonoInternalCompilerProvider extends BaseInternalCompilerProvider
{
	@Override
	public String getExtensionId()
	{
		return "mono-dotnet";
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return MonoSdkType.getInstance().getIcon();
	}

	@Override
	public void setupCompiler(@Nonnull DotNetModuleExtension<?> netExtension,
			@Nonnull CSharpModuleExtension<?> csharpExtension,
			@Nonnull MSBaseDotNetCompilerOptionsBuilder builder,
			@Nullable VirtualFile compilerSdkHome) throws DotNetCompileFailedException
	{
		Sdk sdk = netExtension.getSdk();
		if(sdk == null)
		{
			throw new DotNetCompileFailedException("Mono SDK is not resolved");
		}

		if(SystemInfo.isWindows)
		{
			builder.setExecutableFromSdk(sdk, "/../../../bin/mcs.bat");
		}
		else if(SystemInfo.isMac)
		{
			builder.setExecutableFromSdk(sdk, "/../../../bin/mcs");
		}
		else if(SystemInfo.isFreeBSD)
		{
			builder.setExecutable(MonoSdkType.ourDefaultFreeBSDCompilerPath);
		}
		else if(SystemInfo.isLinux)
		{
			builder.setExecutable(MonoSdkType.ourDefaultLinuxCompilerPath);
		}
	}
}
