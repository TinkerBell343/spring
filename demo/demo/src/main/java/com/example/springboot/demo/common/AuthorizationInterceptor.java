package com.example.springboot.demo.common;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.springboot.demo.Exception.AppWebException;
import com.example.springboot.demo.annotation.Authority;
import com.example.springboot.demo.auth.AuthManager;
import com.example.springboot.demo.common.utils.StringUtil;

@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
	
	/**
	 * 拦截器加载在springcontext之前，注入自然为null故不可使用autowired注解
	 */
	private <T> T getBean(Class<T> clazz,HttpServletRequest request){
	    //通过该方法获得的applicationContext 已经是初始化之后的applicationContext 容器
	    WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
	    return applicationContext.getBean(clazz);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			
		AuthManager am = getBean(AuthManager.class,request);
		if(handler instanceof HandlerMethod){
			Authority auth = ((HandlerMethod) handler).getMethodAnnotation(Authority.class);
			if(auth != null){
				return true;
			}
			
			String token = request.getHeader(Constants.PARAM_TOKEN);
			if(!StringUtil.validString(token)){
				token = request.getParameter(Constants.PARAM_TOKEN);
			}
			if(!StringUtil.validString(token) || am.getUser() == null){
				throw new AppWebException(ErrorConstant.LOGIN_FIRST.getCode(),
						ErrorConstant.LOGIN_FIRST.getMsg());
			}
		}
		return super.preHandle(request, response, handler);
	}
}
