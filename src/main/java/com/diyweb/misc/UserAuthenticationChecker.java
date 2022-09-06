package com.diyweb.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.diyweb.models.User;
import com.diyweb.repo.UserRepoInterface;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
/**
 * This class should be used to check passed user credentials
 * @author erick
 *
 */
@Named("userAuthChecker")
@ApplicationScoped
public class UserAuthenticationChecker {
		
	@Inject
	private UserRepoInterface userRepo;
	
	/**
	 * Checks if passed session contains proper user email and identifier
	 * @param session
	 * @return either empty map or one that contains errors to use in sendError with code as key and value as message
	 */
	public Map<Integer, String> checkUserCredentialsFromSession(HttpSession session){
		Map<Integer, String> error = new HashMap<>(); //only one value should be pushed and only if credentials are wrong
		
		String userEmail = (String)session.getAttribute("userEmail");
		UUID userIdentifier = (UUID)session.getAttribute("userIdentifier");
		
		if(userEmail == null || userIdentifier ==null || userEmail.equals("")) {
			error.put(404, "User was not authenticated properly");
			return error;
		}
		//retrieve user, check verification and identifier
		User persistedUser = userRepo.getUserByEmail(userEmail);
		if(persistedUser == null) {
			error.put(401, "No registered user found for email: "+userEmail);
			return error;
		}else if(!persistedUser.isVerified()) {
			error.put(401, "User email was not verified, please follow the link in the email message to verify.");
			return error;
		}else if(!persistedUser.getUserIdentifier().equals(userIdentifier)) {
			error.put(401, "Provided user identifier was incorrect for current user");
			return error;
		}
		
		return error;
	}
	
	/**
	 * The credentials are checked only to ensure that they are not empty
	 * @param userEmail
	 * @param userIdentifier
	 * @return either empty map or one that contains errors to use in sendError with code as key(404) and value as message
	 */
	public Map<Integer, String> checkPassedUserCredentials(String userEmail, UUID userIdentifier){
		Map<Integer, String> error = new HashMap<>();
		if(userEmail == null || userIdentifier ==null || userEmail.equals("")) {
			error.put(404, "User was not authenticated properly");
		}
		return error;
	}
	
	/**
	 * Performs checks to ensure that user was verified,<br/>
	 * is not null and his identifier is same as one that was passed
	 * @param userToCheck
	 * @param userIdentifier
	 * @return either empty map or one that contains errors to use in sendError with code as key(401) and value as message
	 */
	public Map<Integer, String> checkUserAuthentication(User userToCheck, UUID userIdentifier){
		Map<Integer, String> error = new HashMap<>();
		if(userToCheck == null) {
			error.put(401, "No registered user found");
			return error;
		}else if(!userToCheck.isVerified()) {
			error.put(401, "User email was not verified, please follow the link in the email message to verify.");
			return error;
		}else if(!userToCheck.getUserIdentifier().equals(userIdentifier)) {
			error.put(401, "Provided user identifier was incorrect for current user");
			return error;
		}
		return error;
	}
}
