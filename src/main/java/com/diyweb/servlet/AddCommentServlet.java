package com.diyweb.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
import com.diyweb.models.User;
import com.diyweb.repo.CommentRepoInterface;
import com.diyweb.repo.PostRepoInterface;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns="comment/add/*")
@Path("/{category}/{postId}")
public class AddCommentServlet extends HttpServlet {
	
	@Inject
	private UserAuthenticationChecker authChecker;
	@Inject
	private UserRepoInterface userRepo;
	@Inject
	private PostRepoInterface postRepo;
	@Inject
	private CommentRepoInterface commentRepo;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String comment = req.getParameter("comment-body");
		
		if(comment == null || comment.trim().equals("")) {
			resp.sendError(400, "Comment body was null");
			return;
		}
		
		String sessionEmail = (String)req.getSession().getAttribute("userName");
		UUID sessionIdentifier = (UUID)req.getSession().getAttribute("userIdentifier");
		
		Map<Integer, String> error = authChecker.checkPassedUserCredentials(sessionEmail, sessionIdentifier);
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
		
		if(persistedUser == null) {
			resp.sendError(404, "Requested user was not found");
			return;
		}
		
		
		
	}
	
	
}
