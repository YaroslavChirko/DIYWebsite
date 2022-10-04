package com.diyweb.repo;

import java.util.List;
import java.util.UUID;

import com.diyweb.models.User;
import com.diyweb.models.UserEmailToken;

public interface UserRepoInterface {
	public void persist(User user);
	public List<User> getAllUsers();
	public User getUserByEmail(String email);
	public User getUserByEmailAndIdentifier(String email, UUID userIdentifier);
	public boolean updateUsername(User user);
	public void updateVerificationStatus(String userEmail);
	public void updateUserToken(String userEmail, UserEmailToken token);
	public void updateUserPass(String userEmail, String newPass);
	
}
