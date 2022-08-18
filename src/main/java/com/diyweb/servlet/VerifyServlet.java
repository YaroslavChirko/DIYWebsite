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
		
		Map<String, String> pathParams = UrlPathParameterExtractor.processPathParameters(this.getClass(), req.getPathInfo());
		if(!pathParams.isEmpty() && pathParams.get("pathEmail")!=null) {
			User user = userRepo.getUserByEmail(pathParams.get("pathEmail"));
			if(user != null) {
				UserEmailToken token = user.getUserToken();
				if(token != null) {
					System.out.println("tokens are equal: "+token.getToken().toString().equals(pathParams.getOrDefault("pathIdentifier", "")));
					if(token.getToken().toString().equals(pathParams.getOrDefault("pathIdentifier", ""))) {
						userRepo.updateVerificationStatus(user.getEmail());

						req.getSession().setAttribute("userIdentifier", user.getUserIdentifier());
						req.getSession().setAttribute("userEmail", user.getEmail());
					}else {
						System.out.println(token.getToken().toString()+" != "+pathParams.getOrDefault("pathIdentifier", ""));
					}
				}
				
			}
		}
		RequestDispatcher dispatcher = req.getRequestDispatcher("/");
		
		dispatcher.include(req, resp);
	}
	
}
