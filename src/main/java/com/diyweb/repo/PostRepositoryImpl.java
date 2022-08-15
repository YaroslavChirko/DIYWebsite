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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

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
	private static final long serialVersionUID = -8999669614367529104L;
	@PersistenceContext(unitName = "diyWebUnit")
	EntityManager entityManager;
	
	
	@Override
	public void persist(Post post) {
		entityManager.persist(post);		
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

	@Override
	public boolean updatePost(Post post) {
		Post current = getPostById(post.getId());
		
		boolean titleChanged = false;
		boolean bodyChanged = false;
		boolean cathegoryChanged = false;
		boolean picturesChanged = false;
		
		entityManager.getTransaction().begin();
		
		if(!(current.getTitle().equals(post.getTitle()))) {
			current.setTitle(post.getTitle());
			titleChanged = true;
		}
		
		if(!(current.getBody().equals(post.getBody()))) {
			current.setBody(post.getBody());
			bodyChanged = true;
		}
		
		if(!(current.getCathegory().equals(post.getCathegory()))) {
			current.setCathegory(post.getCathegory());
			cathegoryChanged = true;
		}
		
		if(!(current.getPictureUrls().equals(post.getPictureUrls()))) {
			current.setPictureUrls(post.getPictureUrls());
			picturesChanged = true;
		}
		
		
		return titleChanged || bodyChanged || cathegoryChanged || picturesChanged;
	}
	
	@PreDestroy
	private void cleanup() {
		entityManager.close();
	}

}
