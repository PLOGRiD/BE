package com.example.plogrid.global.security.handler;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.plogrid.global.apiPayload.code.GeneralErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;

@Component
public class AccessTokenArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AccessToken.class);
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		String auth = webRequest.getHeader("Authorization");

		if (auth == null || !auth.startsWith("Bearer ")) {
			throw new GeneralException(GeneralErrorCode.INVALID_AUTHORIZATION_HEADER);
		}

		return auth.substring("Bearer ".length());
	}
}
