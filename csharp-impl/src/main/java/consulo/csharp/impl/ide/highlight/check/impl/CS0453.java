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
import consulo.codeEditor.Editor;
import consulo.csharp.impl.ide.highlight.CSharpHighlightContext;
import consulo.csharp.impl.ide.highlight.check.CompilerCheck;
import consulo.csharp.lang.impl.psi.CSharpFileFactory;
import consulo.csharp.lang.psi.CSharpNullableType;
import consulo.csharp.module.extension.CSharpLanguageVersion;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.dotnet.psi.resolve.DotNetTypeResolveResult;
import consulo.language.editor.intention.BaseIntentionAction;
import consulo.language.editor.intention.SyntheticIntentionAction;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.SmartPointerManager;
import consulo.language.psi.SmartPsiElementPointer;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 15.09.14
 */
public class CS0453 extends CompilerCheck<CSharpNullableType> {
  public static class DeleteQuestMarkQuickFix extends BaseIntentionAction implements SyntheticIntentionAction {
    private SmartPsiElementPointer<CSharpNullableType> myPointer;

    public DeleteQuestMarkQuickFix(CSharpNullableType nullableType) {
      myPointer = SmartPointerManager.getInstance(nullableType.getProject()).createSmartPsiElementPointer(nullableType);
      setText("Remove '?'");
    }

    @Override
    public boolean isAvailable(@Nonnull Project project, Editor editor, PsiFile file) {
      return myPointer.getElement() != null;
    }

    @Override
    public void invoke(@Nonnull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
      CSharpNullableType element = myPointer.getElement();
      if (element == null) {
        return;
      }

      DotNetType innerType = element.getInnerType();
      if (innerType == null) {
        return;
      }

      DotNetType type = CSharpFileFactory.createMaybeStubType(project, innerType.getText(), element);
      element.replace(type);
    }
  }

  @RequiredReadAction
  @Nullable
  @Override
  public CompilerCheckBuilder checkImpl(@Nonnull CSharpLanguageVersion languageVersion,
                                        @Nonnull CSharpHighlightContext highlightContext,
                                        @Nonnull CSharpNullableType element) {
    // C# 8 have special handle
    if (languageVersion.isAtLeast(CSharpLanguageVersion._8_0)) {
      return null;
    }

    DotNetType innerType = element.getInnerType();
    if (innerType == null) {
      return null;
    }
    DotNetTypeRef dotNetTypeRef = innerType.toTypeRef();

    DotNetTypeResolveResult typeResolveResult = dotNetTypeRef.resolve();

    if (!typeResolveResult.isNullable()) {
      return null;
    }
    PsiElement questElement = element.getQuestElement();
    return newBuilder(questElement, formatTypeRef(dotNetTypeRef)).withQuickFix(new DeleteQuestMarkQuickFix(element));
  }
}
