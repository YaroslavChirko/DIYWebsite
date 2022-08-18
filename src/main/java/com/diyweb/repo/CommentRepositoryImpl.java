package com.diyweb.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.diyweb.models.Comment;
import com.diyweb.models.Post;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

/**
 * Commentary repository implementation, it allows us to get all comments for the specific post,<br/>
 * get specific comment by id, update comment and add reply to a specific comment.
 * 
 * @author erick
 *
 */
@Named(value = "commentRepository")
@ApplicationScoped
public class CommentRepositoryImpl implements CommentRepoInterface, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6574783147721104860L;
	@PersistenceContext( unitName = "diyWebUnit")
	EntityManager entityManager;
	
	@Resource
	UserTransaction transaction;
	
	@Override
	public void persist(Comment comment) {
		try {
			transaction.begin();
				entityManager.persist(comment);
			transaction.commit();
		}catch(NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Comment> getCommentsByPost(Post post) {
		String queryStr = "Select c from Comment c Where c.origin = :post";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("post", post);
		
		List<Comment> comments = (List<Comment>)query.getResultStream().collect(Collectors.<Comment, List<Comment>>toCollection(ArrayList::new));
		return comments;
	}

	@Override
	public Comment getCommentById(int id) {
		return entityManager.find(Comment.class, id);
	}

	@Override
	public boolean updateComment(Comment comment) {
		Comment current = getCommentById(comment.getId());
		if(current != null && current.getBody().equals(comment.getBody())) {
			return false;
		}
		
		try {
			transaction.begin();
				current.setBody(comment.getBody());
			transaction.commit();
		}catch(NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void addReply(Comment reply, Comment toReply) {
		try {
			transaction.begin();
				reply.setReplyingTo(toReply);
				entityManager.persist(reply);
			transaction.commit();
		}catch(NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			e.printStackTrace();
		}
		
	}

}
