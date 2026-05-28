package com.seohaeng.backend.global.security.handler;

import com.seohaeng.backend.global.apiPayload.code.status.ErrorStatus;
import com.seohaeng.backend.global.apiPayload.exception.handler.AuthException;
import org.springframework.stereotype.Component;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

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
            throw new AuthException(ErrorStatus._UNAUTHORIZED);
        }

        return auth.replace("Bearer ", "");
    }
}