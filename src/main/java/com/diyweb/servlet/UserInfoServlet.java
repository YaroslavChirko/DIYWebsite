package com.diyweb.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
import com.diyweb.models.User;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;


@WebServlet(urlPatterns = "/user/*")
@Path("/{userIdentifier}")
public class UserInfoServlet extends HttpServlet {
	@Inject
	UserRepoInterface userRepository;
	@Inject
	UserAuthenticationChecker userAuthChecker;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = (String)req.getSession().getAttribute("userEmail");
		String identifierStr = req.getSession().getAttribute("userIdentifier").toString();
		String pathIdStr = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo()).getOrDefault("userIdentifier", "");
		if(email != null && !email.equals("") && identifierStr != null && identifierStr.equals(pathIdStr)) {
			User user = userRepository.getUserByEmail(email);
			req.setAttribute("currentUser", user);
			RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/user-info.jspx");
			dispatcher.forward(req, resp);
		}
		resp.sendRedirect("./");
		return;
	}
	//TODO: add ability to change profile picture
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userEmail = (String)req.getSession().getAttribute("userEmail");
		UUID  userIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		String pathIdStr = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo()).getOrDefault("userIdentifier", "");
		
		Map<Integer, String> error = userAuthChecker.checkPassedUserCredentials(userEmail, userIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(400, error.get(400));
			return;
		}
		
		User currentUser = userRepository.getUserByEmail(userEmail);
		//check user verification and 
		error = userAuthChecker.checkUserAuthentication(currentUser, userIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(401, error.get(401));
			return;
		}
		
		if(!currentUser.getUserIdentifier().equals(UUID.fromString(pathIdStr))) {
			resp.sendError(403, "Current user cannot make changes to this user\'s info");
			return;
		}
		
		String name = (String)req.getParameter("name");
		if(name == null && name.equals("") && name.equals(currentUser.getName())) {
			resp.sendError(401, "Provided name was null empty or identical to current one");
			return;
		}
		
		currentUser.setName(name);
		userRepository.updateUsername(currentUser);
	}
	
}
