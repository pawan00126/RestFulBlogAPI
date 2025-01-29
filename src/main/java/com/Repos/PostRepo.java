package com.Repos;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//import org.hibernate.query.Query;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.DTO.CommentDTO;
import com.DTO.PostDTO;
import com.Entity.Comment;
import com.Entity.Post;
import com.Entity.User;
import com.SessionFactory.SessionFactoryUtil;

import jakarta.ws.rs.core.Response;

public class PostRepo {
	private SessionFactory sessionFactory;

	public PostRepo() {

//    	System.out.println("Initializing SessionFactory from PostRepo()...");
		this.sessionFactory = SessionFactoryUtil.getSessionFactory();

//        System.out.println("done SessionFactory PostRepo()...");
	}

//  add post
	public Response addPost(Post post) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			
			User user = session.get(User.class, post.getUser().getId());

			if (user == null) {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("User with ID " + post.getUser().getId() + " does not exist").build();
			}

			// Set the user entity to the post
			post.setUser(user);
			post.setCreatedAt(LocalDate.now());
			post.setUpdatedAt(LocalDate.now());

			
			session.persist(post);

			// Update the post count for the user
			user.setPostCount(user.getPostCount()); 
			
			session.merge(user); 

			transaction.commit();

			return Response.status(Response.Status.CREATED).entity("Post added successfully with ID: " + post.getId())
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error adding the post: " + e.getMessage()).build();
		}
	}

	
	
	
//    update method
	public Response updatePost(Long id, Post post) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			// Fetch the existing post
			Post existingPost = session.get(Post.class, id);

			if (existingPost == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("Post not found").build();
			}

			
			if (post.getTitle() != null && !post.getTitle().isEmpty()) {
				existingPost.setTitle(post.getTitle());
			}

			if (post.getContent() != null && !post.getContent().isEmpty()) {
				existingPost.setContent(post.getContent());
			}

			
			existingPost.setUpdatedAt(LocalDate.now());

			
			session.merge(existingPost);

			
			transaction.commit();

			return Response.ok("Post updated successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error updating the post: " + e.getMessage()).build();
		}
	}

	
	
	
	
	public Response deletePost(Long id) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			// Fetch the existingPost to delete
			Post existingPost = session.get(Post.class, id);

			if (existingPost == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("Post not found").build();
			}

			// Get the associated user (post owner)
			User postOwner = existingPost.getUser();

			if (postOwner != null) {
				// Update post count for the post owner
				postOwner.getPosts().remove(existingPost);
				postOwner.setPostCount((long) postOwner.getPosts().size());
				session.merge(postOwner);
			}

			// Update comment count for all users who created comments on this post
			if (existingPost.getComments() != null) {
				for (Comment comment : existingPost.getComments()) {
					User commenter = comment.getUser(); // Get the user who created this comment
					if (commenter != null) {
						commenter.getComments().remove(comment);
						commenter.setCommentCount((long) commenter.getComments().size());
						session.merge(commenter); // Merge to update the database
					}
				}
			}

// Remove the post -->> comments will be removed too
			session.remove(existingPost);

			transaction.commit();

			return Response.ok("Post deleted successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error deleting the post: " + e.getMessage()).build();
		}
	}

	
	
	
	
// Get a post by ID (returns PostDTO without comments)
	public PostDTO getPostById(Long id) {
		try (Session session = sessionFactory.openSession()) {
			
//			System.out.println("\nRepo get one post method called \n");
			Post existingPost = session.get(Post.class, id);
			
//			System.out.println("\nWe got the existingPost \n");

			if (existingPost != null) {
//				System.out.println("\nWe got the existingPost and it is not null \n");
				
				Hibernate.initialize(existingPost.getUser()); 
				
//				System.out.println("\nInitialized User \n");

				// Map the Post entity to PostDTO without comments
				return mapToPostDTOWithoutComments(existingPost);
			}

			return null; // post not found -->> null
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving the post by ID: " + e.getMessage(), e);
		}
	}


	private PostDTO mapToPostDTOWithoutComments(Post post) {

		PostDTO postDTO = new PostDTO();
		
		postDTO.setId(post.getId());
		postDTO.setTitle(post.getTitle());
		postDTO.setContent(post.getContent());
		postDTO.setCreatedAt(post.getCreatedAt());
		postDTO.setUpdatedAt(post.getUpdatedAt());
		postDTO.setUsername(post.getUser().getUsername()); 

		return postDTO;
	}

	
	
	
	
	
	
// Get all posts with pagination
	public List<PostDTO> getAllPosts(int page, int size) {
	    try (Session session = sessionFactory.openSession()) {
	        int offset = (page - 1) * size;

	        List<Post> posts = session.createQuery("FROM Post", Post.class)
	                .setFirstResult(offset)
	                .setMaxResults(size)
	                .list();

	        return PostDTO.fromEntityList(posts); 
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving posts: " + e.getMessage(), e);
	    }
	}


	
	
	
// get Post Count
	public long getPostCount() {
	    try (Session session = sessionFactory.openSession()) {
	        
	        Long count = session.createQuery("SELECT COUNT(p) FROM Post p", Long.class).uniqueResult();
	        
	        return count != null ? count : 0;
	        
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving post count: " + e.getMessage(), e);
	    }
	}

	
	
	
	
// get All Comments By Post Id	
	public List<CommentDTO> getAllCommentsByPostId(Long postId) {
	    try (Session session = sessionFactory.openSession()) {
	        Post post = session.get(Post.class, postId);

	        if (post == null) {
	            return Collections.emptyList(); 
	        }

	        Hibernate.initialize(post.getComments()); 

	        
	        return post.getComments().stream()
	                   .map(this::mapToCommentDTO) 
	                   .collect(Collectors.toList());
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving comments for post ID " + postId + ": " + e.getMessage(), e);
	    }
	}


	CommentDTO mapToCommentDTO(Comment comment) {
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setId(comment.getId());
		commentDTO.setText(comment.getText());
		commentDTO.setCreatedAt(comment.getCreatedAt());
		commentDTO.setUsername(comment.getUser().getUsername()); 
		return commentDTO;
	}

}
