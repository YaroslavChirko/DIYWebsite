package com.diyweb.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.diyweb.misc.ImageSaver;
import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.models.User;
import com.diyweb.repo.PostRepoInterface;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns="/posts/read/delete/*")
@Path("/{category}/{postId}")//TODO: create servlet filter to send traffic here if the last part of the path is delete
public class PostDeleteServlet extends HttpServlet {
	
	@Inject
	private PostRepoInterface postRepo;
	@Inject 
	private UserRepoInterface userRepo;
	@Inject 
	private ImageSaver imageSaver;
	@Inject
	private UserAuthenticationChecker authChecker;
	
	//TODO:Perhaps find a way to do it with post, perhaps form for the button
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//get path parameters
		Map<String, String> pathParams = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo());
		String categoryStr = pathParams.get("category");
		String postIdStr = pathParams.get("postId");
		//check path parameters
		if(categoryStr == null || postIdStr == null || categoryStr.trim().equals("") || postIdStr.trim().equals("")) {
			resp.sendError(400, "Either category or post id was null or empty");
			return;
		}
		
		Cathegory category = null;
		try {
			category = Cathegory.valueOf(categoryStr);
		}catch(IllegalArgumentException e) {
			resp.sendError(400, "Provided category was not found: "+e.getMessage());
			return;
		}
		
		int postId = -1;
		
		try {
			postId = Integer.parseInt(postIdStr);
		}catch(NumberFormatException e) {
			resp.sendError(400, "Provided id was not a number ");
			return;
		}
		
		//get info from session
		String sessionEmail = (String)req.getSession().getAttribute("userEmail");
		UUID sessionIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		
		//check session attributes
		Map<Integer,String> error = authChecker.checkPassedUserCredentials(sessionEmail, sessionIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(error.entrySet().iterator().next().getKey(),
					error.entrySet().iterator().next().getValue());
			return;
		}
		
		User persistedUser = userRepo.getUserByEmail(sessionEmail);
		
		error = authChecker.checkUserAuthentication(persistedUser, sessionIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(error.entrySet().iterator().next().getKey(),
					error.entrySet().iterator().next().getValue());
			return;
		}
		
		//retrieve post by id
		Post currentPost = postRepo.getPostById(postId);
		if(currentPost == null) {
			resp.sendError(404, "Post wasn\'t found for provided id");
			return;
		}
		
		//check if post author is the same as current user
		if(!currentPost.getAuthor().equals(persistedUser)) {
			resp.sendError(403, "Current user cannot delete this post");
			return;
		}
		
		//call delete post
		postRepo.deletePost(currentPost);
		
		//redirect to current category url
		resp.sendRedirect("/DIYWebsite/posts/"+categoryStr);
	}

	
}
