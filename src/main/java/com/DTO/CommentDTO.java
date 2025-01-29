package com.DTO;

import java.time.LocalDate;

import com.Adapter.LocalDateAdapter;
import com.Entity.Comment;


import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class CommentDTO {


    private Long id;

    private String text;

    private LocalDate createdAt;

    private String username;

    private Long postId;

    private String postTitle;
    
    private PostDTO post;
    
    

    public CommentDTO() {}

    public CommentDTO(Long id, String text, LocalDate createdAt, String username, Long postId, String postTitle) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.username = username;
        this.postId = postId;
        this.postTitle = postTitle;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
    
    
    public PostDTO getPost() {
        return post;
    }

    public void setPost(PostDTO post) {
        this.post = post; 
    }
    
    
    

    // Static method to map Comment entity to CommentDTO
    public static CommentDTO fromEntity(Comment comment) {
        return new CommentDTO(
            comment.getId(),
            comment.getText(),
            comment.getCreatedAt(),
            comment.getUser() != null ? comment.getUser().getUsername() : null,
            comment.getPost() != null ? comment.getPost().getId() : null,
            comment.getPost() != null ? comment.getPost().getTitle() : null // Include post title
        );
    }

	
}
