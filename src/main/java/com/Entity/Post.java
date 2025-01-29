package com.Entity;

import java.time.LocalDate;
import java.util.List;

import com.Adapter.LocalDateAdapter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@XmlRootElement
public class Post {

//	----------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	private LocalDate createdAt;

	
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	private LocalDate updatedAt;

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

//	----------------------------------------------------------------------------------------

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

//	----------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


//	----------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Comment> comments;

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

//	----------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------

	public Post() {
		super();

		this.updatedAt = LocalDate.now();
		if (this.createdAt == null) {
			this.createdAt = LocalDate.now();
		}

	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", title=" + title + ", content=" + content + ", username="
				+ (user != null ? user.getUsername() : "null") + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ "]";
	}

}
