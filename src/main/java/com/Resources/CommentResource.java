package com.Resources;

import java.util.List;

import com.DTO.CommentDTO;
import com.Entity.Comment;
import com.Repos.CommentRepo;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

//import com.SessionFactory.SessionFactoryUtil;

@Path("comments")
public class CommentResource {

	private final CommentRepo commentRepo = new CommentRepo();

	// Create a new comment
	@POST
	@Path("/post/{postId}/addComment")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createComment(@PathParam("postId") Long postId, Comment comment) {
		try {

//        	System.out.println("create comment on resources started...\n");

			Response response = commentRepo.addComment(postId, comment);
//            return Response.status(Response.Status.CREATED).entity(comment).build();

			return response;

		} catch (RuntimeException e) {
			throw new WebApplicationException("Error creating comment", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	
	// Update an existing comment
	@PUT
	@Path("/update/{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response updateComment(@PathParam("id") Long id, Comment comment) {
		try {

//        	Comment existingComment = session.get(Comment.class, id);

			if (comment.getId() != null) {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("ID should not be provided; it is auto-generated").build();
			}

			comment.setId(id);

			return commentRepo.updateComment(comment);

		} catch (RuntimeException e) {
			throw new WebApplicationException("Error updating comment", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	
	// Delete a comment
	@DELETE
	@Path("/delete/{id}")
	public Response deleteComment(@PathParam("id") Long id) {
		try {
			return commentRepo.deleteComment(id);

		} catch (RuntimeException e) {
			throw new WebApplicationException("Error deleting comment", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	
	// Get a specific comment by ID
	@GET
	@Path("/get/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getComment(@PathParam("id") Long id) {
		try {
			CommentDTO commentDTO = commentRepo.getCommentById(id);

			if (commentDTO == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("Comment with ID " + id + " not found")
						.build(); // Return 404 Not Found
			}

//	        System.out.println("\nRetrieved Comment: " + commentDTO + "\n");

			return Response.ok(commentDTO).build(); // Return 200 OK with the CommentDTO

		} catch (RuntimeException e) {
			throw new WebApplicationException("Error retrieving comment", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	
	
	// Get all comments with pagination
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllComments(@QueryParam("page") @DefaultValue("1") int page,
	        @QueryParam("size") @DefaultValue("10") int size) {
	    try {
	        // Fetch comments as CommentDTO with pagination
	        List<CommentDTO> comments = commentRepo.getAllComments(page, size);

	        if (comments.isEmpty()) {
	            return Response.status(Response.Status.NOT_FOUND).entity("No comments found").build(); // Return 404 if no comments
	        }

	        // Wrap CommentDTO list in a GenericEntity for JAXB support
	        GenericEntity<List<CommentDTO>> entity = new GenericEntity<>(comments) {};

	        return Response.ok(entity).build(); // Return 200 OK with the list of comments
	    } catch (RuntimeException e) {
	        throw new WebApplicationException("Error retrieving comments", Response.Status.INTERNAL_SERVER_ERROR);
	    }
	}


}
