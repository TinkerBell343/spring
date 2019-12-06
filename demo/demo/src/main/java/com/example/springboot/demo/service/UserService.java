package com.example.springboot.demo.service;

import com.example.springboot.demo.repository.entity.User;

public interface UserService {

    public User selectByPrimaryKey(int id);
	
	public int insert(User user);
	
	public int deleteByPrimaryKey(int id);
}
