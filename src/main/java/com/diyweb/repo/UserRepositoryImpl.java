package com.diyweb.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.diyweb.models.User;
import com.diyweb.models.UserEmailToken;

import jakarta.annotation.PreDestroy;
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
 * User repository implementation allows us to find all registered users, any user by e-mail<br/>
 * and update user's username if desired.
 * @author erick
 *
 */
@Named(value = "userRepository")
@ApplicationScoped
public class UserRepositoryImpl implements UserRepoInterface, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5377217999793172892L;
	@PersistenceContext(unitName = "diyWebUnit")
	private EntityManager entityManager;
	private StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
	
	@Resource
	UserTransaction transaction;
	
	@Override
	public void persist(User user) {
		UserEmailToken token = new UserEmailToken(user);
		user.setUserToken(token);
		user.setPass(encryptor.encryptPassword(user.getPass()));

		try {
			transaction.begin();
				entityManager.persist(user);
			transaction.commit();
		} catch (NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<User> getAllUsers() {
		String queryStr = "Select u from User u;";
		Query query = entityManager.createQuery(queryStr);
		List<User> users = (List<User>) query.getResultStream().collect(Collectors.<User, List<User>>toCollection(ArrayList<User>::new));
		
		return users;
	}

	//perhaps left join is needed
	public User getUserByEmail(String email) {
		return entityManager.find(User.class, email);
	}

	public boolean updateUsername(User user) {
		User current = getUserByEmail(user.getEmail());
		if(current.getName().equals(user.getName())) {
			return false;
		}
		
		//entityManager.getTransaction().begin();
		
		current.setName(user.getName());
		
		//entityManager.getTransaction().commit();
		
		
		return true;
	}
	
	@PreDestroy
	private void cleanup() {
		entityManager.close();
	}

}
