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

package consulo.csharp.impl.ide.codeInsight.problems;

import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.lang.CSharpFileType;
import consulo.csharp.module.extension.CSharpSimpleModuleExtension;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleFileIndex;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.ProjectFileIndex;
import consulo.msil.impl.representation.fileSystem.MsilFileRepresentationVirtualFile;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 13.04.2015
 */
public class CSharpLocationUtil
{
	@RequiredReadAction
	public static boolean isValidLocation(@Nonnull Project project, @Nullable VirtualFile virtualFile)
	{
		if(virtualFile == null || virtualFile.getFileType() != CSharpFileType.INSTANCE)
		{
			// if not virtual file - we dont need break highlight
			return true;
		}

		// msil representation highlight always
		if(virtualFile instanceof MsilFileRepresentationVirtualFile)
		{
			return true;
		}

		ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(project);
		if(projectFileIndex.isInLibrary(virtualFile))
		{
			return true;
		}

		Module moduleForFile = ModuleUtilCore.findModuleForFile(virtualFile, project);
		if(moduleForFile == null)
		{
			return false;
		}
		DotNetSimpleModuleExtension extension = ModuleUtilCore.getExtension(moduleForFile, DotNetSimpleModuleExtension.class);
		if(extension == null)
		{
			return false;
		}

		if(ModuleUtilCore.getExtension(moduleForFile, CSharpSimpleModuleExtension.class) == null)
		{
			return false;
		}

		if(extension instanceof DotNetModuleExtension)
		{
			if(!((DotNetModuleExtension) extension).isAllowSourceRoots())
			{
				return true;
			}
			else
			{
				ModuleFileIndex fileIndex = ModuleRootManager.getInstance(moduleForFile).getFileIndex();
				return fileIndex.isInSourceContent(virtualFile) || fileIndex.isInTestSourceContent(virtualFile);
			}
		}
		else
		{
			return true;
		}
	}
}
