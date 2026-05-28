package com.example.plogrid.global.security.handler;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.plogrid.global.apiPayload.code.GeneralErrorCode;
import com.example.plogrid.global.apiPayload.exception.GeneralException;
import com.example.plogrid.global.security.userdetails.MemberDetails;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthUser.class);
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName().equals("anonymousUser")) {
			throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
		}

		Object principal = authentication.getPrincipal();

		if (!(principal instanceof MemberDetails memberDetails)) {
			throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
		}

		return memberDetails.getId();
	}
}
