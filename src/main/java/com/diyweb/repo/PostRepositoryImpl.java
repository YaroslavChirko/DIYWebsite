package com.diyweb.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.models.User;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
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
 * Post repository implementation, contains methods for getting all posts, posts by specific user,<br/>
 * category and update one if needed.
 * @author erick
 *
 */
@Named(value = "postRepository")
@ApplicationScoped
public class PostRepositoryImpl implements PostRepoInterface, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1445738415022236250L;

	@PersistenceContext(unitName = "diyWebUnit")
	EntityManager entityManager;
	
	@Resource
	UserTransaction transaction;
	
	@Override
	public void persist(Post post) {
		try {
			transaction.begin();
			entityManager.persist(post);
			transaction.commit();
		}catch(NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public List<Post> getAllPosts() {
		String queryStr = "Select p from Post p";
		Query query = entityManager.createQuery(queryStr);
		List<Post> posts = (List<Post>) query.getResultStream().collect(Collectors.<Post, List<Post>>toCollection(ArrayList<Post>::new));
		posts = posts==null ? new ArrayList<Post>(): posts;
		return posts;
	}

	@Override
	public List<Post> getPostsForCathegory(Cathegory cathegory) {
		String queryStr = "Select p from Post p Where p.cathegory = :cathegory";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("cathegory", cathegory);
		
		List<Post> posts = (List<Post>) query.getResultStream().collect(Collectors.<Post, List<Post>>toCollection(ArrayList<Post>::new));
		posts = posts==null ? new ArrayList<Post>(): posts;
		return posts;
	}
	
	@Override
	public List<Post> getLastTenPostsForCathegory(Cathegory cathegory) {
		String queryStr = "Select p from Post p Where p.cathegory = :cathegory Order by p.postedAt Desc";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("cathegory", cathegory);
		query.setMaxResults(10);
		
		List<Post> posts = (List<Post>) query.getResultStream().collect(Collectors.<Post, List<Post>>toCollection(ArrayList<Post>::new));
		posts = posts==null ? new ArrayList<Post>(): posts;
		return posts;
	}
	
	@Override
	public List<Post> getNumberOfPostsWithOffsetForCategory(Cathegory category, int offset, int numberOfResults){
		String queryStr = "Select p from Post p Where p.cathegory = :category Order by p.postedAt Desc";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("category", category);
		
		query.setFirstResult(offset);
		query.setMaxResults(numberOfResults);
		
		List<Post> posts = (List<Post>) query.getResultStream().collect(Collectors.<Post, List<Post>>toCollection(ArrayList<Post>::new));
		posts = posts ==null ? new ArrayList<Post>(): posts;
		return posts;
	}
	
	@Override
	public Map<Cathegory, List<Post>> getAllPostsByCathegories() {
		Map<Cathegory, List<Post>> posts = new HashMap<>();
		for(Cathegory type: Cathegory.values()) {
			posts.put(type, getPostsForCathegory(type));
		}
		return posts;
	}
	
	@Override
	public Map<Cathegory, List<Post>> getLastTenPostsByCathegories() {
		Map<Cathegory, List<Post>> posts = new HashMap<>();
		for(Cathegory type: Cathegory.values()) {
			posts.put(type, getLastTenPostsForCathegory(type));
		}
		return posts;
	}

	@Override
	public List<Post> getPostsByUser(User user) {
		String queryStr = "Select p from Post p Where p.author = :user";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("user", user);
		
		List<Post> posts = (List<Post>) query.getResultStream().collect(Collectors.<Post, List<Post>>toCollection(ArrayList<Post>::new));
		posts = posts==null ? new ArrayList<Post>(): posts;
		return posts;
	}

	@Override
	public Post getPostById(int id) {
		return entityManager.find(Post.class, id);
	}

	/**
	 * Only body and pictures should be changeable since title is used to persist images<br/>
	 * in future users should be able to find post by title therefore changing it would disable this function
	 */
	@Override
	public boolean updatePost(Post post) {
		Post current = getPostById(post.getId());
		
		boolean bodyChanged = false;
		boolean picturesChanged = false;
		
		
		if(!(current.getBody().equals(post.getBody()))) {
			current.setBody(post.getBody());
			bodyChanged = true;
		}
		
		if(!(current.getPictureUrls().equals(post.getPictureUrls()))) {
			current.setPictureUrls(post.getPictureUrls());
			picturesChanged = true;
		}
		
		try {
			transaction.begin();
				entityManager.merge(current);
			transaction.commit();
		}catch(NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		return bodyChanged || picturesChanged;
	}
	
	@PreDestroy
	private void cleanup() {
		entityManager.close();
	}

}
