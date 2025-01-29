package com.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class User {

//	------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private boolean isAdmin = false;

//	------------------------------------------------------------------------------------------

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

//	----------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------

	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL, CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Post> posts;

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

//	----------------------------------
	
	@SuppressWarnings("unused")
	private Long postCount;

	public Long getPostCount() {
		return (long) (posts != null ? posts.size() : 0);
	}

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}

	
//	------------------------------------
	
	@OneToMany(mappedBy = "user", cascade = { CascadeType.ALL, CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Comment> comments;

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	
//	----------------------------------------
	
	@SuppressWarnings("unused")
	private Long commentCount;

	public Long getCommentCount() {
		return (long) (comments != null ? comments.size() : 0);
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	
	
//	toString Method
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", isAdmin=" + isAdmin + ", postCount=" + getPostCount() + "commentCount= " + getCommentCount() + "]";
	}

	
	
// constructor from super class
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

}
