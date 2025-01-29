package com.Resources;

//import java.io.StringWriter;
import java.util.List;

import com.DTO.CommentDTO;
import com.DTO.PostDTO;

//import org.hibernate.Hibernate;

import com.Entity.Post;
//import com.Entity.User;
import com.Repos.PostRepo;

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
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//import jakarta.xml.bind.JAXBContext;

@Path("posts")
public class PostResource {

	private PostRepo postRepo = new PostRepo();

// Create a new post
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@Path("/add")
	public Response createPost(Post post) {

		if (post.getId() != null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("ID should not be provided; it is auto-generated").build();
		}

		return postRepo.addPost(post);

	}

	
	
// Update an existing post
	@PUT
	@Path("/update/{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	public Response updatePost(@PathParam("id") Long id, Post post) {

		if (post.getId() != null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("ID should not be provided; it is auto-generated").build();
		}

		post.setId(id);

		return postRepo.updatePost(id, post);

	}

// Delete a post
	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deletePost(@PathParam("id") Long id) {

		return postRepo.deletePost(id);

	}

	
	
	
	
	
// Get all posts with pagination
	@GET
    @Path("/all")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getAllPosts(@QueryParam("page") @DefaultValue("1") int page,
                                 @QueryParam("size") @DefaultValue("10") int size) {
        try {
            // Fetch posts as PostDTO
            List<PostDTO> posts = postRepo.getAllPosts(page, size);

            long totalCount = postRepo.getPostCount(); // Assume you have a method to get total count

            // Wrap the list in a GenericEntity for XML and JSON
            GenericEntity<List<PostDTO>> entity = new GenericEntity<>(posts) {
            };

            // Return response with pagination metadata
            return Response.ok(entity)
                    .header("X-Total-Count", totalCount)
                    .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving posts: " + e.getMessage())
                    .build();
        }
    }

	
	
	
	
	
	
	
	
	
// Get a specific post by ID
	@GET
	@Path("/get/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getPost(@PathParam("id") Long id) {
		try {
			
//			System.out.println("\nResource get one post method called \n");
			PostDTO postDTO = postRepo.getPostById(id);
			
//			System.out.println("\nWe got the post from repo method \n");
			
			
			
			

			if (postDTO != null) {
			    try {
//------------------------------------------------------------------------------		    	
			    	
//			    	System.out.println("createdAt value: " + postDTO.getCreatedAt()+"\n");
			    	
			    	
			    	
//			        System.out.println("\n" + postDTO + "\n");
			        
			        // Manually marshal to XML (for debugging)
//			        JAXBContext context = JAXBContext.newInstance(PostDTO.class);
//			        StringWriter writer = new StringWriter();
//			        context.createMarshaller().marshal(postDTO, writer);
			        
//			        System.out.println("\nXML Output:\n" + writer.toString());  // Print the XML output
//			        System.out.println("\nWe got the XML data\n");

//------------------------------------------------------
			    	
			        return Response.ok(postDTO).build(); // Return 200 OK with post DTO
			        
			    } catch (Exception e) {
			        e.printStackTrace();
			        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			                .entity("Error retrieving post: " + e.getMessage()).build();
			    }
			}
 else {
				return Response.status(Response.Status.NOT_FOUND).entity("Post with ID " + id + " not found").build(); // Return 404 Not Found
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error retrieving post: " + e.getMessage()).build(); // Return 500 Internal Server Error
		}
	}

	
	
	// Get all comments for a specific post
	@GET
	@Path("/{postId}/comments")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllCommentsByPostId(@PathParam("postId") Long postId) {
	    try {
	        List<CommentDTO> comments = postRepo.getAllCommentsByPostId(postId);

	        if (comments.isEmpty()) {
	            return Response.status(Response.Status.NOT_FOUND)
	                    .entity("No comments found for post with ID " + postId)
	                    .build(); // Return 404 if no comments found
	        }

	        // Wrap CommentDTO list in a GenericEntity for JAXB support
	        GenericEntity<List<CommentDTO>> entity = new GenericEntity<>(comments) {};

	        return Response.ok(entity).build(); // Return 200 OK with the list of comments
	    } catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity("Error retrieving comments for post: " + e.getMessage())
	                .build(); // Return 500 Internal Server Error
	    }
	}


}