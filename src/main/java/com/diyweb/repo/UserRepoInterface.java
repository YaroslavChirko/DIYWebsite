package com.diyweb.repo;

import java.util.List;

import com.diyweb.models.User;

public interface UserRepoInterface {
	public void persist(User user);
	public List<User> getAllUsers();
	public User getUserByEmail(String email);
	public boolean updateUsername(User user);
	//public void addUserPost(User user, Post post);
	
}
