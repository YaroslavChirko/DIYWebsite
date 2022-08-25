package com.diyweb.servlet;

import java.io.IOException;
import java.util.List;

import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.repo.PostRepoInterface;

import jakarta.inject.Inject;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

/**
 * 
 * @author root
 *
 *This servlet is used to retrieve and push posts on a specific topic to view<br/>
 * therefore only doGet method is overriden 
 */
@WebServlet(urlPatterns="/posts/*")
@Path("/{category}")
public class PostsServlet extends HttpServlet {
	@Inject
	PostRepoInterface postRepo;
	
	//TODO: think about using either jms or websockets for updates
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//retrieve category from path
		String categoryStr = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo()).get("category");
		//check if category exists, if not, sendError
		Cathegory category = null;
		if(!(categoryStr == null)) {
			category = Cathegory.valueOf(categoryStr);
		}else {
			resp.sendError(404, "No category found for: " + categoryStr);
			return;
		}
		
		if(category == null) {
			resp.sendError(404, "No category found for: " + categoryStr);
			return;
		}
		//retrieve posts for this category, only first few to do the pagination
		List<Post> posts = postRepo.getNumberOfPostsWithOffsetForCategory(category, 0, 100);
		//put bean to view
		System.out.println("Posts: "+posts);
		req.setAttribute("posts", posts);
		req.setAttribute("typesBean", Cathegory.values());
		req.setAttribute("currentCategory", category);
		//dispatch
		RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/posts-view.jspx");
		dispatcher.forward(req, resp);
	}
	
}
