package com.diyweb.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

import com.diyweb.misc.PathVariableCaster;
import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Comment;
import com.diyweb.models.Post;
import com.diyweb.models.User;
import com.diyweb.repo.CommentRepoInterface;
import com.diyweb.repo.CommentRepositoryImpl;
import com.diyweb.repo.PostRepoInterface;
import com.diyweb.repo.UserRepoInterface;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns="/comment/delete/*")
@Path("/{category}/{postId}")
public class DeleteCommentServlet extends HttpServlet {

	
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
		String sessionEmail = (String)req.getSession().getAttribute("userEmail");
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
		
		//retrieve path parameters
		Map<String, String> pathVariables = UrlPathParameterExtractor.processPathParameters(getClass(), req.getPathInfo());
		int postId = -1;
		try {
			postId = PathVariableCaster.castPathVariableByName(pathVariables, "postId", Integer.class);
		} catch (ParseException e) {
			resp.sendError(400, e.getMessage());
			return;
		}
		
		Cathegory category = null;
		try {
			category = PathVariableCaster.castPathVariableByName(pathVariables, "category", Cathegory.class);
		} catch (ParseException e) {
			resp.sendError(400, e.getMessage());
			return;
		}
		
		//retrieve post
		Post persistedPost = postRepo.getPostByIdAndCategory(postId, category);
		if(persistedPost == null) {
			resp.sendError(404, "Requested post wasn\'t found, check passed post id or category");
			return;
		}
		
		String commentIdStr = req.getParameter("comment-id");
		int commentId = -1;
		try{
			commentId = Integer.parseInt(commentIdStr);
		}catch(NumberFormatException e) {
			resp.sendError(400, "Provided id was not an integer: "+e.getMessage());
			return;
		}
		
		Comment comment = commentRepo.getCommentById(commentId);
		if(comment == null) {
			resp.sendError(400, "No comment was found for provided id");
			return;
		}
		if(!comment.getOrigin().equals(persistedPost)) {
			resp.sendError(400, "Current comment does not belong to post specified");
			return;
		}
		if(!comment.getAuthor().equals(persistedUser)) {
			resp.sendError(403, "Current user has no rights to manipulate this comment");
		}
		
		if(!comment.getReplies().isEmpty()) {
			//if the comments contains replies update post body
			comment.setBody("[comment was deleted by author]");
			commentRepo.updateComment(comment);
		}else {
			//if the comment wasn't replied to, delete it
			commentRepo.deleteComment(comment);
		}
	}
	
}
