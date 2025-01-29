package com.Repos;

import java.util.Collections;
//import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.DTO.CommentDTO;
import com.DTO.PostDTO;
import com.DTO.UserDTO;
import com.Entity.Comment;
import com.Entity.Post;
import com.Entity.User;
import com.SessionFactory.SessionFactoryUtil;

import jakarta.ws.rs.core.Response;

public class UserRepo {

	private SessionFactory sessionFactory;

	public UserRepo() {
		this.sessionFactory = SessionFactoryUtil.getSessionFactory();
	}

// add a new user
	public Response addUser(User user) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			if (user.getId() != null) {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("ID should not be provided; it is auto-generated").build();
			}

			session.persist(user);
			transaction.commit();

			return Response.status(Response.Status.CREATED) // 201 Created
					.entity("User saved successfully with ID: " + user.getId()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error saving user or the id " + user.getId() + " already present").build();
		}
	}

	
	
	
// Update a user
	public Response updateUser(User user) {
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();

			// Fetch the existing user by ID
			User existingUser = session.get(User.class, user.getId());
			if (existingUser == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("User with ID " + user.getId() + " does not exist").build();
			}

			// Update fields dynamically based on non-null values
			if (user.getUsername() != null && !user.getUsername().isEmpty()) {
				existingUser.setUsername(user.getUsername());
			}
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				existingUser.setPassword(user.getPassword());
			}
			if (user.getEmail() != null && !user.getEmail().isEmpty()) {
				existingUser.setEmail(user.getEmail());
			}
			if (user.isAdmin()) {
				existingUser.setAdmin(user.isAdmin());
			}

			// Save the updated user
			session.merge(existingUser);
			transaction.commit();

			return Response.status(Response.Status.OK).entity("User with ID " + user.getId() + " updated successfully")
					.build();
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error updating user: " + e.getMessage()).build();
		}
	}

	
	
	
	
// Delete a user by ID
	public Response deleteUser(Long id) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			// Fetch the user to delete
			User user = session.get(User.class, id);

			if (user == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("User with ID " + id + " not found").build();
			}

			// Track users who commented on this user's posts
			if (user.getPosts() != null) {

				for (Post post : user.getPosts()) {

					if (post.getComments() != null) {
						for (Comment comment : post.getComments()) {

							User commentingUser = comment.getUser();

							if (commentingUser != null && !commentingUser.equals(user)) {

								// Decrease the comment count for other users
								commentingUser.getComments().remove(comment);
								commentingUser.setCommentCount((long) commentingUser.getComments().size());
								session.merge(commentingUser);
							}

						}

					}

				}

			}

			// Remove the user (associated posts and comments will be deleted too
			session.remove(user);

			transaction.commit();
			return Response.status(Response.Status.OK).entity("User with ID " + id + " deleted successfully").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error deleting user: " + e.getMessage()).build();
		}
	}

	
	
	
	
	
// get user by username and password
	public User getUserByUsernameAndPassword(String username, String password) {
		try (Session session = sessionFactory.openSession()) {
			String hql = "FROM User WHERE username = :username AND password = :password";
			User user = session.createQuery(hql, User.class).setParameter("username", username)
					.setParameter("password", password).uniqueResult();

			return user; // Return the User object directly
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving user by credentials: " + e.getMessage());
		}
	}



// Get a user by ID 
	public UserDTO getUserById(Long id) {
		try (Session session = sessionFactory.openSession()) {
			User user = session.get(User.class, id);

			if (user != null) {
				Hibernate.initialize(user.getPosts());
				Hibernate.initialize(user.getComments());

				return UserDTO.fromEntity(user); // Use the utility method -->> refer to UserDTO class
			}

			return null;
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving user: " + e.getMessage(), e);
		}
	}

	
	
	
	
// Get all users with pagination
	public List<UserDTO> getAllUsers(int page, int size) {
		try (Session session = sessionFactory.openSession()) {
			int offset = (page - 1) * size;

			List<User> users = session.createQuery("FROM User", User.class).setFirstResult(offset).setMaxResults(size)
					.list();

			return UserDTO.fromEntityList(users); // Use the utility method
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving users: " + e.getMessage());
		}
	}

	
	
	
	
	
// Get users count
	public long getUserCount() {
		try (Session session = sessionFactory.openSession()) {

			String hql = "SELECT COUNT(u) FROM User u";

			return session.createQuery(hql, Long.class).uniqueResult();

		} catch (Exception e) {
			throw new RuntimeException("Error retrieving user count: " + e.getMessage());
		}
	}

	

	
	
// Get all posts by user ID
	public List<PostDTO> getAllPostsByUserId(Long userId, int page, int size) {
	    try (Session session = sessionFactory.openSession()) {
	        int offset = (page - 1) * size;

	        // Fetch posts for the given user ID with pagination
	        List<Post> posts = session.createQuery("FROM Post p WHERE p.user.id = :userId", Post.class)
	                .setParameter("userId", userId)
	                .setFirstResult(offset)
	                .setMaxResults(size)
	                .list();

	        // Map Post entities to PostDTO
	        return posts.stream()
	                .map(post -> mapToPostDTO(post, post.getUser().getUsername()))
	                .toList();
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving posts for user with ID " + userId + ": " + e.getMessage(), e);
	    }
	}

	
	
// Get total post count by user ID
	public long getPostCountByUserId(Long userId) {
	    try (Session session = sessionFactory.openSession()) {
	    	
	        
	        Long count = session.createQuery("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId", Long.class)
	                .setParameter("userId", userId)
	                .uniqueResult();
	        
	        return count != null ? count : 0;
	        
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving post count for user with ID " + userId + ": " + e.getMessage(), e);
	    }
	}
	
	
	
// Map Post entity to PostDTO
	private PostDTO mapToPostDTO(Post post, String username) {
	    return new PostDTO(
	            post.getId(),
	            post.getTitle(),
	            post.getContent(),
	            post.getCreatedAt(),
	            post.getUpdatedAt(),
	            username
	    );
	}



// Get all comments by user ID as CommentDTO
	public List<CommentDTO> getAllCommentsByUserId(Long userId) {
	    try (Session session = sessionFactory.openSession()) {
	    	
	        // Fetch user with comments
	        User user = session.get(User.class, userId);

	        if (user == null) {
	            throw new IllegalArgumentException("User with ID " + userId + " not found.");
	        }

	        // Initialize comments to handle lazy loading
	        Hibernate.initialize(user.getComments());

	        // Map Comment entities to CommentDTO with post title
	        return user.getComments() == null ? Collections.emptyList()
	                : user.getComments().stream().map(CommentDTO::fromEntity).toList();
	        
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving comments for user with ID " + userId + ": " + e.getMessage());
	    }
	}

	
	
	

	

	
	
	
	
	
	
	
	
	
	
	
	
}
