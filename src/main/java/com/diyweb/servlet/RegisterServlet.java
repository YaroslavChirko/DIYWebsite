package com.diyweb.servlet;

import java.io.IOException;
import java.util.Enumeration;

import com.diyweb.misc.MailUtils;
import com.diyweb.models.User;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

	@Inject
	UserRepoInterface userRepository;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/register.jspx");
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Enumeration<String> attr = req.getParameterNames();
		User user = new User((String)req.getParameter("name"), 
				(String)req.getParameter("email"), 
				(String)req.getParameter("pass"));
		
		if(userRepository.getUserByEmail(user.getEmail()) == null) {
			userRepository.persist(user);
			User retrievedUser = userRepository.getUserByEmail(user.getEmail());
			try {
				MailUtils.sendMessage(retrievedUser.getEmail(), retrievedUser, "http://localhost:8080/DIYWebsite", "gmail.properties", "gmail_user.properties");
			} catch (IOException | MessagingException e) {
				System.out.println("Register message sending failed due to: "+e.getMessage());
				e.printStackTrace();
			}
			//TODO: save token and mail
		}
		RequestDispatcher dispatcher = req.getRequestDispatcher("/");
		dispatcher.include(req, resp);
		
	}
	
	
}
