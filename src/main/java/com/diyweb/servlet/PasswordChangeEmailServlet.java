package com.diyweb.servlet;

import java.io.IOException;

import com.diyweb.misc.MailUtils;
import com.diyweb.models.User;
import com.diyweb.models.UserEmailToken;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/user/passwordReset")
public class PasswordChangeEmailServlet extends HttpServlet {
	
	@Inject
	UserRepoInterface userRepo;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/pass-mail-reset.jspx");
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userEmail = (String)req.getParameter("email");
		if(userEmail == null || userEmail.equals("")) {
			resp.sendError(404, "Email was not provided or is empty");
		}
		
		User retrievedUser = userRepo.getUserByEmail(userEmail);
		if(retrievedUser == null || !retrievedUser.isVerified()) {
			resp.sendError(404, "User for provided email was not found or was not yet verified");
			return;
		}
		
		try {
			retrievedUser.setUserToken(new UserEmailToken(retrievedUser));
			userRepo.updateUserToken(retrievedUser.getEmail(), retrievedUser.getUserToken());
			MailUtils.sendMessage(retrievedUser.getEmail(), retrievedUser, "http://localhost:8080/DIYWebsite", "gmail.properties", "gmail_user.properties", MailUtils.EmailType.PASSWORD_RESET);
		} catch (IOException | MessagingException e) {
			resp.sendError(500, e.getMessage());
			e.printStackTrace();
		}
	}
	
}
