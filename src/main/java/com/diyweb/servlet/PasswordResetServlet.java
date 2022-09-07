package com.diyweb.servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.models.User;
import com.diyweb.models.UserEmailToken;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns = "/reset/*")
@Path("/{userEmail}/{userToken}")
public class PasswordResetServlet extends HttpServlet {
	@Inject
	UserRepoInterface userRepository;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//check if such user with this token exists and if it has already expired
		Map<String, String> pathAttributes = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo());
		
		if(pathAttributes.isEmpty() || !pathAttributes.containsKey("userEmail")) {
			resp.sendError(404, "Path attributes was empty or didn't contain email");
			return;
		}
		User retrievedUser = userRepository.getUserByEmail(pathAttributes.get("userEmail"));
		if(retrievedUser == null) {
			resp.sendError(404, "User wasn\'t found");
			return;
		}
		
		if(retrievedUser.getUserToken() == null) {
			 resp.sendError(404, "User has no token");
			 return;
		}
		
		if(!pathAttributes.containsKey("userToken")) {
			resp.sendError(404, "User token was not found in url attributes");
			return;
		}
		
		String pathToken = pathAttributes.get("userToken");
		if(!retrievedUser.getUserToken().getToken().equals(UUID.fromString(pathToken))) {
			System.out.println("user token: "+retrievedUser.getUserToken().getToken()+"\nRetrieved one: "+pathToken);
			resp.sendError(401, "Invalid path token");
			return;
		}
		
		//dispatch view
		RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/password-reset.jspx");
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		//check if such user with this token exists and if it has already expired
		Map<String, String> pathAttributes = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo());
		
		if(pathAttributes.isEmpty() || !pathAttributes.containsKey("userEmail")) {
			resp.sendError(404, "Path attributes was empty or didn't contain email");
			return;
		}
		User retrievedUser = userRepository.getUserByEmail(pathAttributes.get("userEmail"));
		if(retrievedUser == null) {
			resp.sendError(404, "User wasn\'t found");
			return;
		}
		
		if(retrievedUser.getUserToken() == null) {
			 resp.sendError(404, "User has no token");
			 return;
		}
		
		System.out.println("Path attributes are: "+pathAttributes);
		System.out.println("Path attributes contain userToken: "+pathAttributes.containsKey("userToken"));
		if(!pathAttributes.containsKey("userToken")) {
			resp.sendError(404, "User token was not found in url attributes");
			return;
		}
		
		String pathToken = pathAttributes.get("userToken");
		if(!retrievedUser.getUserToken().getToken().equals(UUID.fromString(pathToken))) {
			resp.sendError(401, "Invalid path token");
			return;
		}
		
		String newPass = (String)req.getParameter("pass");
		String newPassRepeat = (String)req.getParameter("pass-repeat");
		if(newPass == null || newPass.equals("") || !newPass.equals(newPassRepeat)) {
			resp.sendError(400, "New pass was null, empty or passes didn't match");
			return;
		}
		
		if(retrievedUser.getUserToken().getCreatedAt().plusMinutes(UserEmailToken.timeTillInvalidation).isAfter(LocalDateTime.now())) {
			retrievedUser.setPass(newPass);
			userRepository.updateUserPass(retrievedUser.getEmail(), retrievedUser.getPass());
		}else {
			System.out.println("Token has already expired");
		}
		
		//remove token
		retrievedUser.setUserToken(null);
		userRepository.updateUsername(retrievedUser);
		
		resp.sendRedirect("./");
		return;
	}
	
}
