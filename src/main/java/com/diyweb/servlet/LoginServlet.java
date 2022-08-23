package com.diyweb.servlet;

import java.io.IOException;

import com.diyweb.models.User;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

	@Inject
	UserRepoInterface userRepository;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher dispatcher = req.getRequestDispatcher("jsp/login.jspx");
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		String pass = req.getParameter("pass");
		HttpSession session = req.getSession();
		
		User persistedUser = userRepository.getUserByEmail(email);
		if(persistedUser.comparePass(pass)) {
			session.setAttribute("userIdentifier", persistedUser.getUserIdentifier());
			session.setAttribute("userEmail", persistedUser.getEmail());
		}else {
			System.out.println("No such user found, check pass and email");//TODO: put as an error
			resp.sendError(403, "User was not found, check credentials.");
			return;
		}
		resp.sendRedirect("./");
		return;
	}
	
}
