package com.diyweb.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

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

@WebServlet(urlPatterns = "/verify/*")
@Path("/{pathEmail}/{pathIdentifier}/")
public class VerifyServlet extends HttpServlet {

	@Inject
	UserRepoInterface userRepo;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//parameters are being extracted from the annotations
		Map<String, String> pathParams = UrlPathParameterExtractor.processPathParameters(this.getClass(), req.getPathInfo());
		if(pathParams.isEmpty() && pathParams.get("pathEmail")==null) {
			resp.sendError(404, "Email was not present on path");
			return;
		}
		User user = userRepo.getUserByEmail(pathParams.get("pathEmail"));
		if(user == null) {
			resp.sendError(404, "User was not found for provided email");
			return;
		}
		UserEmailToken token = user.getUserToken();	
		if(token == null) {
			resp.sendError(404, "User has no token");
			return;
		}
		
		if(!token.getToken().toString().equals(pathParams.getOrDefault("pathIdentifier", ""))) {
			resp.sendError(400, "Tokens were not equal");
			return;
		}
					
		//this sets isVerified to true and removes the token
		userRepo.updateVerificationStatus(user.getEmail());
		req.getSession().setAttribute("userIdentifier", user.getUserIdentifier());
		req.getSession().setAttribute("userEmail", user.getEmail());
		//delete token
		user.setUserToken(null);
		userRepo.updateUserToken(user.getEmail(), user.getUserToken());
					
		resp.sendRedirect("/");//use root endpoint
		return;
	}
	
}
