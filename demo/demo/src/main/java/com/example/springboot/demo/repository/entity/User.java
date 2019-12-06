package com.example.springboot.demo.repository.entity;

import lombok.Data;

@Data
public class User {

	private Long id;
	
	private String name;
	
	private String pswd;
	
	private String role;
}
