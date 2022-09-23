package com.diyweb.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diyweb.misc.HtmlTextEscapingUtils;
import com.diyweb.misc.ImageSaver;
import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.models.User;
import com.diyweb.repo.CommentRepoInterface;
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

@WebServlet(urlPatterns="/posts/read/*")
@Path("/{category}/{postId}")
@MultipartConfig(
		fileSizeThreshold = 1024*1024*1,
		maxFileSize = 1024*1024*24,
		maxRequestSize = 1024*1024*100
)
public class PostViewServlet extends HttpServlet {

	private static String propertyPath = Thread.currentThread().getContextClassLoader().getResource("images/images.properties").getPath();
	
	@Inject
	private UserRepoInterface userRepository;
	@Inject
	private PostRepoInterface postRepository;
	@Inject 
	private ImageSaver imageSaver;
	@Inject
	private UserAuthenticationChecker userAuthChecker;
	
	@Inject
	private CommentRepoInterface commentRepo;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Properties imageProps = new Properties();
		imageProps.load(new FileInputStream(propertyPath));
		
		Map<String, String> pathVariables = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo());
		
		String categoryStr = pathVariables.get("category");
		String postIdStr = pathVariables.get("postId");
		
		if(categoryStr == null || postIdStr == null || categoryStr.equals("") || postIdStr.equals("")) {
			resp.sendError(404, "Post or category was either null or empty");
			return;
		}
		
		int postId = -1;
		
		try {			
			postId = Integer.parseInt(postIdStr);
		}catch(NumberFormatException e) {
			resp.sendError(404, "Provided id was not formatted properly");
			return;
		}
		
		Cathegory category = null;
		try {
			category = Cathegory.valueOf(categoryStr);
		}catch(IllegalArgumentException e) {
			resp.sendError(404, "Invalid category provided");
			return;
		}
		
		Post post = postRepository.getPostById(postId);
		
		if(post == null || !post.getCathegory().equals(category)) {
			resp.sendError(404, "No post found for provided id and category");
			return;
		}
		
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/jsp/read-post.jspx");
		
		String formattedBody = post.getBody().replaceAll("\n", "<br/>");
		String imagePatternStr = "\\[\\[(\\d+?)\\]\\]";
		String imageTagStrStart = "<br/><img alt='' src='"+imageProps.getOrDefault("images.save.host", "http://localhost:8080");
		String imageTagStrEnd = "'  style='width:80%; height:auto; object-fit: contain;'/><br/>";
		Pattern imagePattern = Pattern.compile(imagePatternStr);
		Matcher matcher = imagePattern.matcher(formattedBody);
		//put proper images to the tags
		while(matcher.find()) {
			int imageIndex = Integer.parseInt(matcher.group(1))-1;
			if(imageIndex >= 0 && post.getPictureUrls().size() > imageIndex) {
				formattedBody = formattedBody.replaceFirst(imagePatternStr, imageTagStrStart+post.getPictureUrls().get(imageIndex)+imageTagStrEnd);
			}else {
				formattedBody = formattedBody.replaceFirst(imagePatternStr, "<br/>");
			}
		}
		post.setBody(formattedBody);
		
		
		req.setAttribute("post", post);
		req.setAttribute("typesBean",Cathegory.values());
		req.setAttribute("host", imageProps.getOrDefault("images.save.host", "http://localhost:8080"));
		req.setAttribute("comments", /*post.getComments()*/commentRepo.getCommentsByPost(post));
		//System.out.println("Comments from here: "+post.getComments());
		
		System.out.println("Comments from base: "+commentRepo.getCommentsByPost(post));
		
		requestDispatcher.forward(req, resp);
	}

	
	/**
	 * This method is provided in servlet to enable editing the posts
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//check path variables and category
		Map<String, String> pathVariables = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo());
		
		if(pathVariables.isEmpty()) {
			resp.sendError(400, "No path variables provided");
			return;
		}
		
		String categoryStr = pathVariables.get("category");
		String postIdStr = pathVariables.get("postId");
		
		if(categoryStr == null || postIdStr == null || categoryStr.equals("") || postIdStr.equals("")) {
			resp.sendError(404, "Post or category was either null or empty");
			return;
		}
		
		int postIdInt = -1;
		try {
			postIdInt = Integer.parseInt(postIdStr);
		}catch(NumberFormatException e) {
			resp.sendError(400, "Provided id was not an integer: "+e.getMessage());
			return;
		}
		
		Cathegory category = null;
		
		try {
			category = Cathegory.valueOf(categoryStr);
		}catch(IllegalArgumentException e) {
			resp.sendError(400, "Provided category wasn\'t found: "+e.getMessage());
			return;
		}
		
		//retrieve original post by id and compare it's category
		Post currentPost = postRepository.getPostById(postIdInt);
		
		if(currentPost == null) {
			resp.sendError(404, "Post for provided id wasn\'t found");
			return;
		}
		
		if(!currentPost.getCathegory().equals(category)){
			resp.sendError(400, "Provided category didn\'t mach provided post category");
			return;
		}
		
		//compare post author credentials with current user info from session
		String sessionEmail = (String)req.getSession().getAttribute("userEmail");
		UUID sessionIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		Map<Integer,String> error = userAuthChecker.checkPassedUserCredentials(sessionEmail, sessionIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(error.entrySet().iterator().next().getKey(),
					error.entrySet().iterator().next().getValue());
			return;
		}
		
		User persistedUser = userRepository.getUserByEmail(sessionEmail);
		
		error = userAuthChecker.checkUserAuthentication(persistedUser, sessionIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(error.entrySet().iterator().next().getKey(),
					error.entrySet().iterator().next().getValue());
			return;
		}
		
		//If user was deleted and then a new one was registered with the same email he will be the owner
		if(!persistedUser.equals(currentPost.getAuthor())) {
			resp.sendError(403, "Current user cannot modify this post");
			return;
		}
		
		//check passed body for null and compare to current one
		//we don't check pictures, just set them
		String newBody = req.getParameter("body");
		
		
		if(newBody != null && !newBody.trim().equals("") && !newBody.equals(currentPost.getBody())) {
			currentPost.setBody(HtmlTextEscapingUtils.escapeHtmlText(newBody));
		}
		
		List<String> pictureUrls = new ArrayList<>();
		
		//change images
		pictureUrls = imageSaver.saveToLocation(currentPost.getAuthor().getEmail(), currentPost.getPostedAt(), currentPost.hashCode(), req.getParts());
		
		currentPost.setPictureUrls(pictureUrls);
		
		//persist updated post
		postRepository.updatePost(currentPost);
		resp.sendRedirect("./");
	}
	
	
	
}
