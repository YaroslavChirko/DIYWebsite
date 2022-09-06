package com.diyweb.servlet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import com.diyweb.misc.HtmlTextEscapingUtils;
import com.diyweb.misc.ImageSaver;
import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
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
	@Inject
	ImageSaver imageSaver;
	@Inject
	UserAuthenticationChecker userAuthChecker;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//check user credentials
		Map<Integer, String> error = userAuthChecker.checkUserCredentialsFromSession(req.getSession());
		if(!error.isEmpty()) {
			//since we don't now whichever error was added we get the entry
			Entry<Integer,String> errorEntry = error.entrySet().iterator().next();
			resp.sendError(errorEntry.getKey(), errorEntry.getValue());
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
		
		if(body == null || body.trim().equals("")) {
			resp.sendError(400, "Body was not provided or was empty");
			return;
		}
		
		if(pathCategory == null || pathCategory.trim().equals("")) {
			resp.sendError(400, "Category empty or is null");
			return;
		}
		
		Cathegory category = Cathegory.valueOf(pathCategory);
		if(category == null) {
			resp.sendError(400, "Category not found in a list");
			return;
		}
		
		//get user email, check it and retrieve user
		String userEmail = (String)req.getSession().getAttribute("userEmail");
		UUID  userIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		Map<Integer, String> error = userAuthChecker.checkPassedUserCredentials(userEmail, userIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(400, error.get(400));
			return;
		}
		
		User currentUser = userRepo.getUserByEmail(userEmail);
		//check user verification and 
		error = userAuthChecker.checkUserAuthentication(currentUser, userIdentifier);
		if(!error.isEmpty()) {
			resp.sendError(401, error.get(401));
			return;
		}
		
		
		
		List<String> pictureUrls = new ArrayList<>();
		title = HtmlTextEscapingUtils.escapeHtmlText(title);
		body = HtmlTextEscapingUtils.escapeHtmlText(body);
		
		Post currentPost = new Post(currentUser, title, category, body, pictureUrls);
		//persist pictures and get urls
		pictureUrls = imageSaver.saveToLocation(userIdentifier, currentPost.hashCode(), req.getParts());
		if(pictureUrls != null && !pictureUrls.isEmpty()) {
			currentPost.setPictureUrls(pictureUrls);
		}
		
		postRepo.persist(currentPost);
	}

	
}
