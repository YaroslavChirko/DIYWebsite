package com.diyweb.servlet;

import java.io.IOException;

import com.diyweb.misc.UrlPathParameterExtractor;
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = (String)req.getSession().getAttribute("userEmail");
		String identifier = (String)req.getSession().getAttribute("userIdentifier");
		String pathId = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo()).getOrDefault("userIdentifier", "");
		if(email != null && !email.equals("") && identifier != null && identifier.equals(pathId)) {
			User user = userRepository.getUserByEmail(email);
			req.setAttribute("currentUser", user);
			RequestDispatcher dispatcher = req.getRequestDispatcher("jsp/user-info.jspx");
			dispatcher.forward(req, resp);
		}
		resp.sendRedirect("./");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
}
