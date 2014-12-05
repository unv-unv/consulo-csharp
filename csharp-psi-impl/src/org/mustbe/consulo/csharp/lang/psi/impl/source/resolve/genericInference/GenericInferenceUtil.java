package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.genericInference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpCallArgument;
import org.mustbe.consulo.csharp.lang.psi.CSharpCallArgumentListOwner;
import org.mustbe.consulo.csharp.lang.psi.CSharpQualifiedNonReference;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpression;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.methodResolving.MethodResolver;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.methodResolving.arguments.NCallArgument;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpGenericExtractor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpLambdaResolveResult;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpRefTypeRef;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import lombok.val;

/**
 * @author VISTALL
 * @since 29.10.14
 */
public class GenericInferenceUtil
{
	public static final Key<GenericInferenceUtil.GenericInferenceResult> INFERENCE_RESULT = Key.create("inference.result");

	public static class GenericInferenceResult
	{
		private boolean mySuccess;
		private DotNetGenericExtractor myExtractor;

		public GenericInferenceResult(boolean success, @NotNull DotNetGenericExtractor extractor)
		{
			mySuccess = success;
			myExtractor = extractor;
		}

		public boolean isSuccess()
		{
			return mySuccess;
		}

		@NotNull
		public DotNetGenericExtractor getExtractor()
		{
			return myExtractor;
		}
	}

	public static final GenericInferenceResult FAIL = new GenericInferenceResult(false, DotNetGenericExtractor.EMPTY);
	public static final GenericInferenceResult SUCCESS = new GenericInferenceResult(true, DotNetGenericExtractor.EMPTY);

	@NotNull
	public static GenericInferenceResult inferenceGenericExtractor(
			@NotNull CSharpQualifiedNonReference referenceElement,
			@NotNull CSharpCallArgumentListOwner callArgumentListOwner,
			@NotNull DotNetLikeMethodDeclaration methodDeclaration)
	{
		CSharpCallArgument[] arguments = callArgumentListOwner.getCallArguments();

		DotNetTypeRef[] typeArgumentListRef = DotNetTypeRef.EMPTY_ARRAY;
		if(referenceElement instanceof CSharpReferenceExpression)
		{
			typeArgumentListRef = ((CSharpReferenceExpression) referenceElement).getTypeArgumentListRefs();
		}
		return inferenceGenericExtractor(arguments, typeArgumentListRef, callArgumentListOwner, methodDeclaration);
	}

	@NotNull
	public static GenericInferenceResult inferenceGenericExtractor(@NotNull CSharpCallArgument[] callArguments,
			@NotNull DotNetTypeRef[] typeArgumentListRefs,
			@NotNull PsiElement scope,
			@NotNull DotNetLikeMethodDeclaration methodDeclaration)
	{
		DotNetGenericParameter[] genericParameters = methodDeclaration.getGenericParameters();
		if(genericParameters.length == 0 || typeArgumentListRefs.length > 0)
		{
			DotNetGenericExtractor extractor = genericParameters.length != typeArgumentListRefs.length ? DotNetGenericExtractor.EMPTY : new
					CSharpGenericExtractor(genericParameters, typeArgumentListRefs);
			return new GenericInferenceResult(genericParameters.length == typeArgumentListRefs.length, extractor);
		}

		List<NCallArgument> methodCallArguments = MethodResolver.buildCallArguments(callArguments, methodDeclaration, scope);

		if(methodCallArguments.isEmpty())
		{
			return new GenericInferenceResult(true, DotNetGenericExtractor.EMPTY);
		}

		val map = new HashMap<DotNetGenericParameter, DotNetTypeRef>();

		for(NCallArgument nCallArgument : methodCallArguments)
		{
			DotNetTypeRef temp = nCallArgument.getParameterTypeRef();
			if(temp == null)
			{
				continue;
			}

			DotNetTypeRef parameterTypeRef = cleanTypeRef(temp);
			DotNetTypeRef expressionTypeRef = cleanTypeRef(nCallArgument.getTypeRef());

			DotNetTypeResolveResult parameterTypeResolveResult = parameterTypeRef.resolve(scope);
			DotNetTypeResolveResult expressionTypeResolveResult = expressionTypeRef.resolve(scope);

			if(parameterTypeResolveResult instanceof CSharpLambdaResolveResult && expressionTypeResolveResult instanceof CSharpLambdaResolveResult)
			{
				CSharpLambdaResolveResult pLambdaResolveResult = (CSharpLambdaResolveResult) parameterTypeResolveResult;
				CSharpLambdaResolveResult eLambdaResolveResult = (CSharpLambdaResolveResult) expressionTypeResolveResult;

				DotNetTypeRef[] pParameterTypeRefs = pLambdaResolveResult.getParameterTypeRefs();
				DotNetTypeRef[] eParameterTypeRefs = eLambdaResolveResult.getParameterTypeRefs();

				if(pParameterTypeRefs.length == eParameterTypeRefs.length)
				{
					for(int i = 0; i < eParameterTypeRefs.length; i++)
					{
						DotNetTypeRef pParameterTypeRef = pParameterTypeRefs[i];
						DotNetTypeRef eParameterTypeRef = eParameterTypeRefs[i];

						inferenceGenericFromExpressionTypeRefAndParameterTypeRef(genericParameters, map, pParameterTypeRef, eParameterTypeRef,
								scope);
					}
				}

				inferenceGenericFromExpressionTypeRefAndParameterTypeRef(genericParameters, map, pLambdaResolveResult.getReturnTypeRef(),
						eLambdaResolveResult.getReturnTypeRef(), scope);
			}

			inferenceGenericFromExpressionTypeRefAndParameterTypeRef(genericParameters, map, parameterTypeRef, expressionTypeRef, scope);
		}

		return new GenericInferenceResult(genericParameters.length == map.size(), new CSharpGenericExtractor(map));
	}

	private static void inferenceGenericFromExpressionTypeRefAndParameterTypeRef(DotNetGenericParameter[] methodGenericParameters,
			Map<DotNetGenericParameter, DotNetTypeRef> map,
			DotNetTypeRef parameterTypeRef,
			DotNetTypeRef expressionTypeRef,
			PsiElement scope)
	{
		if(expressionTypeRef == DotNetTypeRef.AUTO_TYPE || expressionTypeRef == DotNetTypeRef.UNKNOWN_TYPE)
		{
			return;
		}

		DotNetTypeResolveResult parameterTypeResolveResult = parameterTypeRef.resolve(scope);

		PsiElement parameterElement = parameterTypeResolveResult.getElement();
		for(DotNetGenericParameter genericParameter : methodGenericParameters)
		{
			if(map.containsKey(genericParameter))
			{
				continue;
			}

			if(genericParameter.isEquivalentTo(parameterElement))
			{
				map.put(genericParameter, expressionTypeRef);
				return;
			}
		}

		DotNetTypeResolveResult typeRefFromExtends = CSharpTypeUtil.findTypeRefFromExtends(expressionTypeRef, parameterTypeRef, scope);
		if(typeRefFromExtends == null)
		{
			return;
		}

		PsiElement element = typeRefFromExtends.getElement();
		DotNetGenericExtractor genericExtractor = typeRefFromExtends.getGenericExtractor();

		if(element instanceof DotNetGenericParameterListOwner)
		{
			DotNetGenericParameter[] genericParametersOfResolved = ((DotNetGenericParameterListOwner) element).getGenericParameters();

			for(DotNetGenericParameter genericParameter : methodGenericParameters)
			{
				if(map.containsKey(genericParameter))
				{
					continue;
				}

				int indexOfGeneric = findIndexOfGeneric(parameterTypeRef, genericParameter, scope);
				if(indexOfGeneric == -1)
				{
					continue;
				}

				DotNetGenericParameter genericParameterOfResolved = ArrayUtil2.safeGet(genericParametersOfResolved, indexOfGeneric);
				if(genericParameterOfResolved == null)
				{
					continue;
				}

				DotNetTypeRef extract = genericExtractor.extract(genericParameterOfResolved);
				if(extract != null)
				{
					map.put(genericParameter, extract);
				}
			}
		}
	}

	private static int findIndexOfGeneric(DotNetTypeRef parameterTypeRef, DotNetGenericParameter parameter, PsiElement scope)
	{
		DotNetTypeResolveResult typeResolveResult = parameterTypeRef.resolve(scope);

		PsiElement element = typeResolveResult.getElement();
		if(element instanceof DotNetGenericParameterListOwner)
		{
			DotNetGenericParameter[] genericParameters = ((DotNetGenericParameterListOwner) element).getGenericParameters();
			if(genericParameters.length == 0)
			{
				return -1;
			}

			DotNetGenericExtractor genericExtractor = typeResolveResult.getGenericExtractor();

			for(int i = 0; i < genericParameters.length; i++)
			{
				DotNetTypeRef extractedTypeRef = genericExtractor.extract(genericParameters[i]);
				if(extractedTypeRef == null)
				{
					continue;
				}
				DotNetTypeResolveResult extractedTypeResolveResult = extractedTypeRef.resolve(scope);
				if(parameter.isEquivalentTo(extractedTypeResolveResult.getElement()))
				{
					return i;
				}
			}
		}
		return -1;
	}

	@NotNull
	private static DotNetTypeRef cleanTypeRef(@NotNull DotNetTypeRef typeRef)
	{
		if(typeRef instanceof CSharpRefTypeRef)
		{
			typeRef = ((CSharpRefTypeRef) typeRef).getInnerTypeRef();
		}
		return typeRef;
	}
}
