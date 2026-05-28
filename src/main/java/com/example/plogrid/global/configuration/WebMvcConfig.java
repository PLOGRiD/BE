package com.example.plogrid.global.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.plogrid.global.security.handler.AccessTokenArgumentResolver;
import com.example.plogrid.global.security.handler.AuthUserArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final AuthUserArgumentResolver authUserArgumentResolver;
	private final AccessTokenArgumentResolver accessTokenArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authUserArgumentResolver);
		resolvers.add(accessTokenArgumentResolver);
	}
}
