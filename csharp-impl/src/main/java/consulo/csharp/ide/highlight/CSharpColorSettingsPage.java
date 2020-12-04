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

package consulo.csharp.ide.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.OptionsBundle;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.io.FileUtil;
import consulo.csharp.lang.doc.ide.highlight.CSharpDocHighlightKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 08.04.14
 */
public class CSharpColorSettingsPage implements ColorSettingsPage
{
	private static final AttributesDescriptor[] ourDescriptors = new AttributesDescriptor[]{
			new AttributesDescriptor("Comments//Block comment", CSharpHighlightKey.BLOCK_COMMENT),
			new AttributesDescriptor("Comments//Line comment", CSharpHighlightKey.LINE_COMMENT),
			new AttributesDescriptor("Keywods//Keyword", CSharpHighlightKey.KEYWORD),
			new AttributesDescriptor("Keywods//Context keyword", CSharpHighlightKey.SOFT_KEYWORD),
			new AttributesDescriptor("Keywods//Preprocessor keyword", CSharpHighlightKey.MACRO_KEYWORD),
			new AttributesDescriptor("Preprocessor//Preprocessor variable", CSharpHighlightKey.MACRO_VARIABLE),
			new AttributesDescriptor("Preprocessor//Disabled preprocessor block", CSharpHighlightKey.DISABLED_BLOCK),
			new AttributesDescriptor("String", CSharpHighlightKey.STRING),
			new AttributesDescriptor("Number", CSharpHighlightKey.NUMBER),
			new AttributesDescriptor("Classes and Interfaces//Class name", CSharpHighlightKey.CLASS_NAME),
			new AttributesDescriptor("Classes and Interfaces//Attribute name", CSharpHighlightKey.ATTRIBUTE_NAME),
			new AttributesDescriptor("Classes and Interfaces//Enum name", CSharpHighlightKey.ENUM_NAME),
			new AttributesDescriptor("Classes and Interfaces//Inteface name", CSharpHighlightKey.INTERFACE_NAME),
			new AttributesDescriptor("Methods//Constructor declaration", CSharpHighlightKey.CONSTRUCTOR_NAME),
			new AttributesDescriptor("Methods//Method declaration", CSharpHighlightKey.METHOD_NAME),
			new AttributesDescriptor("Methods//Delegate method name", CSharpHighlightKey.DELEGATE_METHOD_NAME),
			new AttributesDescriptor("Methods//Extension method call", CSharpHighlightKey.EXTENSION_METHOD_CALL),
			new AttributesDescriptor("Methods//Static method call", CSharpHighlightKey.STATIC_METHOD_CALL),
			new AttributesDescriptor("Methods//Instance method call", CSharpHighlightKey.INSTANCE_METHOD_CALL),
			new AttributesDescriptor("Methods//Method reference", CSharpHighlightKey.METHOD_REF),
			new AttributesDescriptor("Class Fieds & Properties & Events//Instance field", CSharpHighlightKey.INSTANCE_FIELD),
			new AttributesDescriptor("Class Fieds & Properties & Events//Static field", CSharpHighlightKey.STATIC_FIELD),
			new AttributesDescriptor("Class Fieds & Properties & Events//Instance property", CSharpHighlightKey.INSTANCE_PROPERTY),
			new AttributesDescriptor("Class Fieds & Properties & Events//Static property", CSharpHighlightKey.STATIC_PROPERTY),
			new AttributesDescriptor("Class Fieds & Properties & Events//Static event", CSharpHighlightKey.STATIC_EVENT),
			new AttributesDescriptor("Class Fieds & Properties & Events//Instance event", CSharpHighlightKey.INSTANCE_EVENT),
			new AttributesDescriptor("Class Fieds & Properties & Events//Constant", CSharpHighlightKey.CONSTANT),
			new AttributesDescriptor("Parameters//Generic parameter name", CSharpHighlightKey.GENERIC_PARAMETER_NAME),
			new AttributesDescriptor("Parameters//Parameter", CSharpHighlightKey.PARAMETER),
			new AttributesDescriptor("Implicit or explicit cast", CSharpHighlightKey.IMPLICIT_OR_EXPLICIT_CAST),
			new AttributesDescriptor("Comments//Documentation//Documentation comment", CSharpDocHighlightKey.DOC_COMMENT),
			new AttributesDescriptor("Comments//Documentation//Documentation tag", CSharpDocHighlightKey.DOC_COMMENT_TAG),
			new AttributesDescriptor("Comments//Documentation//Documentation attribute", CSharpDocHighlightKey.DOC_COMMENT_ATTRIBUTE),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.dot"), CSharpHighlightKey.DOT),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.comma"), CSharpHighlightKey.COMMA),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.brackets"), CSharpHighlightKey.BRACKETS),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.braces"), CSharpHighlightKey.BRACES),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.parentheses"), CSharpHighlightKey.PARENTHESES),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.semicolon"), CSharpHighlightKey.SEMICOLON),
			new AttributesDescriptor(OptionsBundle.message("options.language.defaults.operation"), CSharpHighlightKey.OPERATION_SIGN),
			new AttributesDescriptor("Braces and Operators//Colon", CSharpHighlightKey.COLON),
			new AttributesDescriptor("Braces and Operators//Arrows (->, =>)", CSharpHighlightKey.ARROW),
			new AttributesDescriptor("Variables//Local variable", CSharpHighlightKey.LOCAL_VARIABLE),
	};

	private static final Map<String, TextAttributesKey> ourAdditionalTags = new HashMap<String, TextAttributesKey>()
	{
		{
			put("class_name", CSharpHighlightKey.CLASS_NAME);
			put("attribute_name", CSharpHighlightKey.ATTRIBUTE_NAME);
			put("interface_name", CSharpHighlightKey.INTERFACE_NAME);
			put("enum_name", CSharpHighlightKey.ENUM_NAME);
			put("generic_parameter_name", CSharpHighlightKey.GENERIC_PARAMETER_NAME);
			put("delegate_method_name", CSharpHighlightKey.DELEGATE_METHOD_NAME);
			put("soft_keyword", CSharpHighlightKey.SOFT_KEYWORD);
			put("method_name", CSharpHighlightKey.METHOD_NAME);
			put("constructor_name", CSharpHighlightKey.CONSTRUCTOR_NAME);
			put("macro_keyword", CSharpHighlightKey.MACRO_KEYWORD);
			put("macro_variable", CSharpHighlightKey.MACRO_VARIABLE);
			put("disabled_block", CSharpHighlightKey.DISABLED_BLOCK);
			put("extension_call", CSharpHighlightKey.EXTENSION_METHOD_CALL);
			put("static_call", CSharpHighlightKey.STATIC_METHOD_CALL);
			put("instance_call", CSharpHighlightKey.INSTANCE_METHOD_CALL);
			put("static_field", CSharpHighlightKey.STATIC_FIELD);
			put("static_property", CSharpHighlightKey.STATIC_PROPERTY);
			put("constant", CSharpHighlightKey.CONSTANT);
			put("instance_field", CSharpHighlightKey.INSTANCE_FIELD);
			put("instance_property", CSharpHighlightKey.INSTANCE_PROPERTY);
			put("parameter", CSharpHighlightKey.PARAMETER);
			put("method_ref", CSharpHighlightKey.METHOD_REF);
			put("static_event_name", CSharpHighlightKey.STATIC_EVENT);
			put("instance_event_name", CSharpHighlightKey.INSTANCE_EVENT);
			put("implicit_or_explicit", CSharpHighlightKey.IMPLICIT_OR_EXPLICIT_CAST);
			put("doc_comment", CSharpDocHighlightKey.DOC_COMMENT);
			put("doc_tag", CSharpDocHighlightKey.DOC_COMMENT_TAG);
			put("doc_attribute", CSharpDocHighlightKey.DOC_COMMENT_ATTRIBUTE);
			put("local_var", CSharpHighlightKey.LOCAL_VARIABLE);
		}
	};

	private final NotNullLazyValue<String> myDemoTextValue = NotNullLazyValue.createValue(() ->
	{
		try
		{
			return FileUtil.loadTextAndClose(getClass().getResourceAsStream("/colorSettingsPage/C#.txt"), true);
		}
		catch(IOException e)
		{
			throw new Error(e);
		}
	});

	@Nonnull
	@Override
	public SyntaxHighlighter getHighlighter()
	{
		return new CSharpSyntaxHighlighter();
	}

	@Nonnull
	@Override
	public String getDemoText()
	{
		return myDemoTextValue.getValue();
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
	{
		return ourAdditionalTags;
	}

	@Nonnull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors()
	{
		return ourDescriptors;
	}

	@Nonnull
	@Override
	public ColorDescriptor[] getColorDescriptors()
	{
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@Nonnull
	@Override
	public String getDisplayName()
	{
		return "C#";
	}
}
