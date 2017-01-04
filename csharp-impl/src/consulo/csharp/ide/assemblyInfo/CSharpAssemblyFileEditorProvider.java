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

package consulo.csharp.ide.assemblyInfo;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import consulo.csharp.assemblyInfo.CSharpAssemblyConstants;
import consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import consulo.ide.eap.EarlyAccessProgramDescriptor;
import consulo.ide.eap.EarlyAccessProgramManager;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public class CSharpAssemblyFileEditorProvider implements FileEditorProvider
{
	public static class EapDescriptor extends EarlyAccessProgramDescriptor
	{
		@NotNull
		@Override
		public String getName()
		{
			return "AssemblyInfo.cs UI Editor";
		}

		@NotNull
		@Override
		public String getDescription()
		{
			return "";
		}
	}

	@Override
	public boolean accept(@NotNull final Project project, @NotNull final VirtualFile virtualFile)
	{
		if(!EarlyAccessProgramManager.getInstance().getState(EapDescriptor.class))
		{
			return false;
		}
		PsiFile file = ApplicationManager.getApplication().runReadAction(new Computable<PsiFile>()
		{
			@Override
			public PsiFile compute()
			{
				return PsiManager.getInstance(project).findFile(virtualFile);
			}
		});
		return virtualFile.getName().equals(CSharpAssemblyConstants.FileName) && file instanceof CSharpFileImpl;
	}

	@NotNull
	@Override
	public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile)
	{
		return new CSharpAssemblyFileEditor(project, virtualFile);
	}

	@Override
	public void disposeEditor(@NotNull FileEditor fileEditor)
	{

	}

	@NotNull
	@Override
	public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile)
	{
		return FileEditorState.INSTANCE;
	}

	@Override
	public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element)
	{

	}

	@NotNull
	@Override
	public String getEditorTypeId()
	{
		return "csharp.assembly.editor";
	}

	@NotNull
	@Override
	public FileEditorPolicy getPolicy()
	{
		return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
	}
}
