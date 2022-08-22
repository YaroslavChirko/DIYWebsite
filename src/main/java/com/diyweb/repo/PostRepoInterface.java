package com.diyweb.repo;

import java.util.List;
import java.util.Map;

import com.diyweb.models.Cathegory;
import com.diyweb.models.Post;
import com.diyweb.models.User;

public interface PostRepoInterface {
	public void persist(Post post);
	public List<Post> getAllPosts();
	public List<Post> getPostsForCathegory(Cathegory cathegory);
	public List<Post> getLastTenPostsForCathegory(Cathegory cathegory);
	public List<Post> getNumberOfPostsWithOffsetForCategory(Cathegory category, int offset, int numberOfResults);
	public Map<Cathegory, List<Post>> getAllPostsByCathegories();
	public Map<Cathegory, List<Post>> getLastTenPostsByCathegories();
	public List<Post> getPostsByUser(User user);
	public Post getPostById(int id);
	public boolean updatePost(Post post);
	
}
