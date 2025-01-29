package com.Resources;

import java.util.List;

import com.DTO.CommentDTO;
import com.DTO.PostDTO;
import com.DTO.UserDTO;

//import com.Entity.Post;
import com.Entity.User;

import com.Repos.UserRepo;

//import org.hibernate.Hibernate;
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

@Path("users")
public class UserResource {

	private UserRepo userRepo = new UserRepo();

// Create a new user
	@Path("/add")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createUser(User user) {
		return userRepo.addUser(user);
	}

	
	
	
	
// Update an existing user
	@PUT
	@Path("/update/{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response updateUser(@PathParam("id") Long id, User user) {

		if (user.getId() != null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("ID should not be provided; it is auto-generated").build();
		}

		user.setId(id);

		return userRepo.updateUser(user);
	}

	
	
	
	
	// Delete a user with userid
	@DELETE
	@Path("/delete/{id}")
	public Response deleteUser(@PathParam("id") Long id) {
		return userRepo.deleteUser(id);
	}

	
	
	
	
	// Get a specific user by ID (resource method)
	@GET
	@Path("/get/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getUser(@PathParam("id") Long id) {
		try {
			UserDTO user = userRepo.getUserById(id);

			if (user != null) {
				return Response.ok(user).build(); // Return UserDTO
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("User with ID " + id + " not found").build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error retrieving user: " + e.getMessage()).build();
		}
	}

	
	
	
//	get All Users
	@Path("/all")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllUsers(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("size") @DefaultValue("10") int size) {
		try {
			// Fetch users as UserDTO
			List<UserDTO> users = userRepo.getAllUsers(page, size);

			long totalCount = userRepo.getUserCount();

			// Wrap UserDTO list in a GenericEntity
			GenericEntity<List<UserDTO>> entity = new GenericEntity<>(users) {
			};

			// Return response with pagination metadata
			return Response.ok(entity).header("X-Total-Count", totalCount).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error retrieving users: " + e.getMessage()).build();
		}
	}

	
	
	
	
	// Get all posts by user ID as PostDTO (without comments)
	@GET
	@Path("/{id}/posts")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllPostsByUserId(
	        @PathParam("id") Long userId,
	        @QueryParam("page") @DefaultValue("1") int page,
	        @QueryParam("size") @DefaultValue("10") int size) {
	    try {
	        // Fetch posts by user ID as PostDTO
	        List<PostDTO> posts = userRepo.getAllPostsByUserId(userId, page, size);

	        // Fetch total count of posts by user ID
	        long totalCount = userRepo.getPostCountByUserId(userId);

	        // Wrap posts in a GenericEntity for correct serialization
	        GenericEntity<List<PostDTO>> entity = new GenericEntity<>(posts) {};

	        // Add pagination metadata to the response
	        return Response.ok(entity)
	                .header("X-Total-Count", totalCount)
	                .build();
	    } catch (IllegalArgumentException e) {
	        throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
	    } catch (RuntimeException e) {
	        throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
	    }
	}

	
	

	// Get all comments by user ID as CommentDTO with post title
	@GET
	@Path("/{id}/comments")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllCommentsByUserId(@PathParam("id") Long id) {
	    try {
	    	
	        // Fetch comments as CommentDTO
	        List<CommentDTO> comments = userRepo.getAllCommentsByUserId(id);

	        // Wrap CommentDTO list in a GenericEntity for JAXB support
	        GenericEntity<List<CommentDTO>> entity = new GenericEntity<>(comments) {};

	        return Response.ok(entity).build();
	        
	    } catch (IllegalArgumentException e) {
	    	
	        throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
	        
	    } catch (RuntimeException e) {
	    	
	        throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
	        
	    }
	}


}
