package com.diyweb.servlet;

import java.io.IOException;
import java.util.UUID;

import com.diyweb.models.Cathegory;
import com.diyweb.models.User;
import com.diyweb.repo.PostRepoInterface;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns = "/posts/add-post/*")
@Path("/{category}")
@MultipartConfig(
		fileSizeThreshold = 1024*1024*1,
		maxFileSize = 1024*1024*24,
		maxRequestSize = 1024*1024*100
)
public class AddPostServlet extends HttpServlet {

	@Inject
	UserRepoInterface userRepo;
	@Inject
	PostRepoInterface postRepo;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//check user credentials
		String userEmail = (String)req.getSession().getAttribute("userEmail");
		UUID userIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		
		if(userEmail == null || userIdentifier ==null) {
			resp.sendError(404, "User was not authenticated properly");
			return;
		}
		//retrieve user
		User persistedUser = userRepo.getUserByEmail(userEmail);
		if(persistedUser == null) {
			resp.sendError(401, "No registered user found for email: "+userEmail);
			return;
		}
		
		//check verification
		if(!persistedUser.isVerified() && !persistedUser.getUserIdentifier().equals(userIdentifier)) {
			resp.sendError(401, "User email was not verified, please follow the link in the email message to verify.");
			return;
		}
		
		req.setAttribute("typesBean", Cathegory.values());
		//deploy view
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/jsp/add-post.jspx");
		requestDispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
	}

	
}
