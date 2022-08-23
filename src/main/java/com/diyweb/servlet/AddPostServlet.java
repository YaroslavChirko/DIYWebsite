package com.diyweb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
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
import jakarta.servlet.http.Part;
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
		//get title from request
		String title = req.getParameter("title");
		//get body from request
		String body = req.getParameter("postBody");
		//get category from path
		String  pathCategory = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo()).get("category");
		
		if(title == null || title.trim().equals("")) {
			resp.sendError(400, "Title was not provided or was empty");
			return;
		}
		
		if(body == null || title.trim().equals("")) {
			resp.sendError(400, "Body was not provided or was empty");
			return;
		}
		
		if(pathCategory == null || pathCategory.trim().equals("")) {
			resp.sendError(400, "Category empty or is null");
			return;
		}
		
		Cathegory category = Cathegory.valueOf(pathCategory);
		if(category != null) {
			resp.sendError(400, "Category");
			return;
		}
		
		//get user email, check it and retrieve user
		String userEmail = (String)req.getSession().getAttribute("userEmail");
		UUID  userIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		if(userEmail == null || userIdentifier == null || userEmail.equals("")) {
			resp.sendError(400, "User credentials are either null or empty");
			return;
		}
		
		User currentUser = userRepo.getUserByEmail(userEmail);
		if(currentUser == null || currentUser.isVerified()) {
			resp.sendError(401, "User not found, try with another account");
			return;
		}
		
		
		
		List<String> pictureUrls = new ArrayList<>();
		Post currentPost = new Post(currentUser, title, category, body, pictureUrls);
		//save images to some folder and then add paths to them into pictureUrls array
		String pathToPictures = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"/"+userIdentifier+"/"+currentPost.hashCode();
		
		for() {
			
		}
		
		postRepo.persist(currentPost);
	}

	
}
