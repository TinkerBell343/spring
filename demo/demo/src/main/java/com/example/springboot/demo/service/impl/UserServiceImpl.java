package com.example.springboot.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.demo.repository.dao.UserMapper;
import com.example.springboot.demo.repository.entity.User;
import com.example.springboot.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper mapper;
	
	@Override
	public User selectByPrimaryKey(int id) {
		// TODO Auto-generated method stub
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public int insert(User user) {
		// TODO Auto-generated method stub
		return mapper.insert(user);
	}

	@Override
	public int deleteByPrimaryKey(int id) {
		// TODO Auto-generated method stub
		return mapper.deleteByPrimaryKey(id);
	}

}
