package com.example.springboot.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.demo.annotation.Authority;
import com.example.springboot.demo.auth.AuthManager;
import com.example.springboot.demo.repository.entity.User;
import com.example.springboot.demo.service.UserService;

@RestController
@RequestMapping(value = "/testControl", produces = "application/json")
public class RequestController {
	
	private static final String requestStr = "Success";
	
	private static final String requestDenyStr = "NoAuth";
	
	@Autowired
	private AuthManager am;
	
	@Autowired
	private UserService userService;
	
	@Authority //token验证例外注解
	@RequestMapping("/login")
	public String login(@RequestParam("user")String user){
		String token = am.Authority(user);
		return token;
	}
	
	@RequestMapping("/logout")
	public String logout(){
		am.logout();
		return requestStr;
	}
	
	@RequestMapping("/test")
	public String test(){
		User user = new User();
		user.setId(2l);
		user.setName("test");
		user.setPswd("hahaha");
		user.setRole("cleaner");
		this.userService.insert(user);
		return requestStr;
	}

}
