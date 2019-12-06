package com.example.springboot.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.springboot.demo.common.AuthorizationInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

	/**
	 * 配置类，使自定义的Interceptor生效
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor());
    }
}
