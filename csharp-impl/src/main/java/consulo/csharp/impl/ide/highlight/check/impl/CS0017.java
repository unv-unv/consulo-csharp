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

package consulo.csharp.impl.ide.highlight.check.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.codeEditor.Editor;
import consulo.csharp.impl.ide.highlight.CSharpHighlightContext;
import consulo.csharp.impl.ide.highlight.check.CompilerCheck;
import consulo.csharp.lang.psi.CSharpMethodDeclaration;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.DotNetTarget;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.extension.DotNetMutableModuleExtension;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.dotnet.psi.DotNetRunUtil;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.editor.intention.PsiElementBaseIntentionAction;
import consulo.language.editor.intention.SyntheticIntentionAction;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import consulo.language.util.ModuleUtilCore;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 17.04.2016
 */
public class CS0017 extends CompilerCheck<CSharpMethodDeclaration> {
  public static class SetMainTypeFix extends PsiElementBaseIntentionAction implements SyntheticIntentionAction {
    private final String myVmQName;

    @RequiredReadAction
    public SetMainTypeFix(@Nonnull DotNetTypeDeclaration typeDeclaration) {
      myVmQName = typeDeclaration.getVmQName();
      setText("Set main to '" + myVmQName + "'");
    }

    @Override
    @RequiredWriteAction
    public void invoke(@Nonnull Project project, Editor editor, @Nonnull PsiElement element) throws IncorrectOperationException {
      DotNetModuleExtension<?> extension = ModuleUtilCore.getExtension(element, DotNetModuleExtension.class);
      if (extension == null || extension.getMainType() != null) {
        return;
      }

      ModuleRootManager rootManager = ModuleRootManager.getInstance(extension.getModule());

      ModifiableRootModel modifiableModel = rootManager.getModifiableModel();

      final DotNetMutableModuleExtension<?> mutable = modifiableModel.getExtension(DotNetMutableModuleExtension.class);
      assert mutable != null;
      mutable.setMainType(myVmQName);

      modifiableModel.commit();
    }

    @Override
    @RequiredUIAccess
    public boolean isAvailable(@Nonnull Project project, Editor editor, @Nonnull PsiElement element) {
      DotNetModuleExtension extension = ModuleUtilCore.getExtension(element, DotNetModuleExtension.class);
      return extension != null && extension.getMainType() == null;
    }
  }

  @RequiredReadAction
  @Nullable
  @Override
  public HighlightInfoFactory checkImpl(@Nonnull CSharpLanguageVersion languageVersion,
                                        @Nonnull CSharpHighlightContext highlightContext,
                                        @Nonnull CSharpMethodDeclaration element) {
    DotNetSimpleModuleExtension<?> dotNetModuleExtension = highlightContext.getDotNetModuleExtension();
    // simple extensions - skip
    if (!(dotNetModuleExtension instanceof DotNetModuleExtension)) {
      return null;
    }
    // is not executable - skip
    if (((DotNetModuleExtension)dotNetModuleExtension).getTarget() != DotNetTarget.EXECUTABLE) {
      return null;
    }
    // if main type is set - skip
    String mainType = ((DotNetModuleExtension)dotNetModuleExtension).getMainType();
    if (mainType != null) {
      return null;
    }

    if (DotNetRunUtil.isEntryPoint(element)) {
      PsiElement[] entryPointElements = ((DotNetModuleExtension)dotNetModuleExtension).getEntryPointElements();
      if (entryPointElements.length > 1) {
        PsiElement nameIdentifier = element.getNameIdentifier();
        assert nameIdentifier != null;

        PsiElement parent = element.getParent();
        return newBuilder(nameIdentifier).withQuickFix(new SetMainTypeFix((DotNetTypeDeclaration)parent));
      }
    }
    return null;
  }
}
