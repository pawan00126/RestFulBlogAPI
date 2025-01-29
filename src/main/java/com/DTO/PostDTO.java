package com.DTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.Entity.Post;
import com.Adapter.*;

//import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class PostDTO {

	private Long id;
	private String title;
	private String content;

	
	private LocalDate createdAt;

	private LocalDate updatedAt;
	
	private String username; 
	
	private List<CommentDTO> comments; 

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@XmlJavaTypeAdapter(LocalDateAdapter.class) // -->> used to solve XML date serialization issue
	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	
	@XmlJavaTypeAdapter(LocalDateAdapter.class) // -->> used to solve XML date serialization issue
	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<CommentDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentDTO> comments) {
		this.comments = comments;
	}

	

	public PostDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PostDTO [id=" + id + ", title=" + title + ", content=" + content + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", username=" + username + "]";
	}
	

	
	
	public PostDTO(Long id, String title, String content, LocalDate createdAt, LocalDate updatedAt, String username) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.username = username;
    }
	
	

	
	// Convert a single Post entity to PostDTO
    public static PostDTO fromEntity(Post post) {
    	
        PostDTO dto = new PostDTO();
        
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUsername(post.getUser().getUsername()); 
        
        return dto;
    }

    
    
    
    // Convert a list of Post entities to a list of PostDTOs
    public static List<PostDTO> fromEntityList(List<Post> posts) {
        if (posts == null) {
            return Collections.emptyList();
        }
        return posts.stream().map(PostDTO::fromEntity).collect(Collectors.toList());
    }
    

	
}
