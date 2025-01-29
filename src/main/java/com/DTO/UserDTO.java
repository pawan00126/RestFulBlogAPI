package com.DTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.Entity.User;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDTO {

	private Long id;
	private String username;
	private String email;
	private boolean isAdmin;
	private Long postCount;
	private Long commentCount;

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Long getPostCount() {
		return postCount;
	}

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	
	
//	 to-String Method
	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", username=" + username + ", email=" + email + ", isAdmin=" + isAdmin
				+ ", postCount=" + postCount + ", commentCount=" + commentCount + "]";
	}

	
	
// Constructor to create a UserDTO from a User entity
	public UserDTO(Long id, String username, String email, boolean isAdmin, Long postCount, Long commentCount) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.isAdmin = isAdmin;
		this.postCount = postCount;
		this.commentCount = commentCount;
	}

	
	
// Default constructor
	public UserDTO() {
	}

// Static method to convert a User entity to a UserDTO
	public static UserDTO fromEntity(User user) {
		return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin(), user.getPostCount(),
				user.getCommentCount());
	}

	
// User-List to UserDTO-List
	public static List<UserDTO> fromEntityList(List<User> users) {
		if (users == null) {
			return Collections.emptyList();
		}
		return users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
	}

}
