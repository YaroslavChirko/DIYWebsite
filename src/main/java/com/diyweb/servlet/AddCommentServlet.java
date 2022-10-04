package com.diyweb.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import com.diyweb.misc.HtmlTextEscapingUtils;
import com.diyweb.misc.PathVariableCaster;
import com.diyweb.misc.SessionAttributeRetriever;
import com.diyweb.misc.UrlPathParameterExtractor;
import com.diyweb.misc.UserAuthenticationChecker;
import com.diyweb.models.Cathegory;
import com.diyweb.models.Comment;
import com.diyweb.models.Post;
import com.diyweb.models.User;
import com.diyweb.repo.CommentRepoInterface;
import com.diyweb.repo.PostRepoInterface;
import com.diyweb.repo.UserRepoInterface;
import com.diyweb.websockets.CommentSocketService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Path;

@WebServlet(urlPatterns="/comment/add/*")
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
		String commentBody = req.getParameter("comment-body");
		
		if(commentBody == null || commentBody.trim().equals("")) {
			resp.sendError(400, "Comment body was null");
			return;
		}
		commentBody = HtmlTextEscapingUtils.escapeHtmlText(commentBody);
		
		//getting user with the help of session user attributes
		User persistedUser = null;
		try {
			persistedUser = authChecker.getUserFromSessionAttributes(req.getSession(), "userEmail", "userIdentifier");
		}catch(RuntimeException e) {
			resp.sendError(400, e.getMessage());
			return;
		}
		if(persistedUser == null) {
			resp.sendError(404, "User was not found");
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
		Post persistedPost = postRepo.getPostById(postId);
		if(persistedPost == null) {
			resp.sendError(404, "Requested post wasn\'t found, check passed post id");
			return;
		}
		
		//compare post category to current one
		if(!persistedPost.getCathegory().equals(category)) {
			resp.sendError(400, "Provided category is not applicable for current post");
			return;
		}
		
		Comment addedComment = new Comment(persistedUser, commentBody, persistedPost);
		
		//check if toReply id was added
		boolean isReplying = false;
		
		Enumeration<String> names = req.getParameterNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			if(name.equals("to-reply")) {
				isReplying = true;
				break;
			}
		}
		
		int toReplyId = -1;
		if(isReplying) {
			toReplyId = Integer.parseInt(req.getParameter("to-reply"));
		}
		if(toReplyId > 0) {
			Comment toReply = commentRepo.getCommentById(toReplyId);
			if(toReply != null && toReply.getOrigin().equals(addedComment.getOrigin())) {
				addedComment.setReplyingTo(toReply);
			}
		}
		
		//persist comment
		commentRepo.persist(addedComment);
		CommentSocketService.sendCommentMessage(category.name(), ""+postId);
		resp.sendRedirect("/DIYWebsite/posts/read/"+category.name()+"/"+postId);
	}
}
