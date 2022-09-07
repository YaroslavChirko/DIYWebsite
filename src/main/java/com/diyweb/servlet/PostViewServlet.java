package com.diyweb.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.repo.PostRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns="/posts/read/*")
@Path("/{category}/{postId}")
public class PostViewServlet extends HttpServlet {

	private static String propertyPath = Thread.currentThread().getContextClassLoader().getResource("images/images.properties").getPath();
	
	@Inject
	private PostRepoInterface postRepository;
	
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
		}
		
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/jsp/read-post.jspx");
		
		String formattedBody = post.getBody().replaceAll("\n", "<br/>");
		String imagePatternStr = "\\[\\[(\\d+?)\\]\\]";
		String imageTagStrStart = "<br/><img alt='' src='"+imageProps.getOrDefault("images.save.host", "http://localhost:8080");
		String imageTagStrEnd = "'  style='width:auto; height:auto; object-fit: contain;'/><br/>";
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
		
		requestDispatcher.forward(req, resp);
	}

	
	/**
	 * This method is provided in servlet to enable editing the posts
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	
	
}
