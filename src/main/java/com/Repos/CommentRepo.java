package com.Repos;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
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

public class CommentRepo {
	private final SessionFactory sessionFactory;

	public CommentRepo() {
		this.sessionFactory = SessionFactoryUtil.getSessionFactory();
	}

//	add comment
	public Response addComment(Long postId, Comment comment) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

//			System.out.println("add comment on repo started..\n");

			// Ensure post exists
			Post existingPost = session.get(Post.class, postId);

			if (existingPost == null) {
				Response.status(Response.Status.NOT_FOUND).entity("Post with ID " + postId + " does not exist").build();
			}

//			System.out.println("\n" + existingPost + "\n");

			User existingUser = session.get(User.class, comment.getUser().getId());
			if (existingUser == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("User with ID " + comment.getUser().getId() + " does not exist").build();
			}

			// Associate comment with the post and user
			comment.setPost(existingPost);
			comment.setUser(existingUser);
			comment.setCreatedAt(LocalDate.now());

			// Add the comment to the user's comments list
			existingUser.getComments().add(comment);

			existingUser.setCommentCount((long) existingUser.getComments().size());

			
			session.persist(comment);

//			System.out.println("comment object persisted..\n");

			session.merge(existingUser);

			transaction.commit();

			return Response.status(Response.Status.CREATED) // 201 Created
					.entity("Comment saved successfully with ID: " + comment.getId()).build();

		} catch (HibernateException e) {
			throw new RuntimeException("Error saving comment", e);
		}
	}

//  update method
	public Response updateComment(Comment comment) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			// Fetch the existing post
			Comment existingComment = session.get(Comment.class, comment.getId());

			if (existingComment == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("Post not found").build();
			}

			// Update fields dynamically based on non-null values
			if (comment.getText() != null && !comment.getText().isEmpty()) {
				existingComment.setText(comment.getText());
			}

			// Merge the comment (which will update the existing one)
			session.merge(existingComment);

			// Commit the transaction
			transaction.commit();

			return Response.ok("comment updated successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error updating the comment: " + e.getMessage()).build();
		}
	}

	// Delete a Comment by ID
	public Response deleteComment(Long id) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			// Fetch the Comment to delete
			Comment existingComment = session.get(Comment.class, id);

			if (existingComment == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("Comment not found").build();
			}

			// Get the associated user and remove the comment from the user's comments collection
			User user = existingComment.getUser();

			// Remove the comment from the user's comments list
			user.getComments().remove(existingComment);

//	        change commentcount in user table
			user.setCommentCount((long) user.getComments().size());

			// Update the user after removing the comment
			session.merge(user);

			// Remove the Comment from the session
			session.remove(existingComment); 

			transaction.commit();

			return Response.ok("Comment deleted successfully").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error deleting the Comment: " + e.getMessage()).build();
		}
	}

	public CommentDTO getCommentById(Long id) {
		try (Session session = sessionFactory.openSession()) {
			Comment comment = session.get(Comment.class, id);

			if (comment != null) {
				
				// Initialize the post field
				Hibernate.initialize(comment.getPost());
				
			} else {
				
				return null; // Return null if the comment is not found
				
			}

			
			// Map the Comment entity to CommentDTO
			return mapToCommentDTO(comment);
			
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving comment with ID " + id + ": " + e.getMessage(), e);
		}
	}

	private CommentDTO mapToCommentDTO(Comment comment) {
		
		CommentDTO commentDTO = new CommentDTO();
		
		commentDTO.setId(comment.getId());
		commentDTO.setText(comment.getText());
		commentDTO.setCreatedAt(comment.getCreatedAt());

		// Only include necessary post details in the DTO
		PostDTO postDTO = new PostDTO();
		
		postDTO.setId(comment.getPost().getId());
		postDTO.setTitle(comment.getPost().getTitle());
		
		UserDTO userDTO = new UserDTO();
		
		userDTO.setUsername(comment.getUser().getUsername());

		commentDTO.setPost(postDTO); // Associate the PostDTO with the CommentDTO
		commentDTO.setUsername(userDTO.getUsername());

		return commentDTO;
	}

	
	
	
	
	public List<CommentDTO> getAllComments(int page, int size) {
	    try (Session session = sessionFactory.openSession()) {
	        int offset = (page - 1) * size;

	        // Fetch comments with pagination
	        List<Comment> comments = session.createQuery("FROM Comment", Comment.class)
	                                        .setFirstResult(offset)
	                                        .setMaxResults(size)
	                                        .list();

	        // Map the list of Comment entities to a list of CommentDTOs
	        return comments.stream()
	                       .map(this::mapToCommentDTO) // Map each comment to CommentDTO including post details
	                       .collect(Collectors.toList());
	    } catch (Exception e) {
	        throw new RuntimeException("Error retrieving comments with pagination: " + e.getMessage(), e);
	    }
	}

}
