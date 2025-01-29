# RestFulBlogAPI

A RESTful Blog API built using Java, Jersey, Hibernate, and MySQL. This API allows users to create, manage, and interact with blog posts and comments while keeping track of user-specific statistics like post count and comment count.

## Features
### User Management
- **Add User**: Create a new user.
- **Update User**: Modify user details.
- **Delete User**: Remove a user.
- **Get All Users**: Retrieve a list of all users.
- **Get User By ID**: Fetch details of a specific user.
- **Get All Posts By User ID**: List all posts created by a specific user.
- **Get All Comments By User ID**: List all comments made by a specific user.

### Post Management
- **Add Post**: Create a new post.
- **Update Post**: Modify an existing post.
- **Delete Post**: Remove a post.
- **Get Post By ID**: Fetch details of a specific post.
- **Get All Posts**: Retrieve all posts in the system.
- **Get All Comments By Post ID**: Fetch all comments associated with a specific post.

### Comment Management
- **Add Comment**: Create a new comment.
- **Update Comment**: Modify an existing comment.
- **Delete Comment**: Remove a comment.
- **Get All Comments**: Retrieve all comments in the system.
- **Get Comment By ID**: Fetch details of a specific comment.

### User-Specific Statistics
- Real-time tracking of:
  - **Post Count**: Total number of posts created by a user.
  - **Comment Count**: Total number of comments made by a user.

## Technology Stack
- **Programming Language**: Java 21
- **Frameworks**: Jersey (for RESTful APIs), Hibernate (for ORM)
- **Database**: MySQL
- **Server**: Apache Tomcat (or any Java EE-compliant server)
- **Build Tool**: Maven

## API Endpoints
### User Endpoints
- `POST /users/add`: Add a new user.
- `PUT /users/update/{id}`: Update an existing user.
- `DELETE /users/delete/{id}`: Delete a user.
- `GET /users/all`: Get all users.
- `GET /users/get/{id}`: Get user details by ID.
- `GET /users/{id}/posts`: Get all posts by a specific user.
- `GET /users/{id}/comments`: Get all comments by a specific user.

### Post Endpoints
- `POST /posts/add`: Add a new post.
- `PUT /posts/update/{id}`: Update an existing post.
- `DELETE /posts/delete/{id}`: Delete a post.
- `GET /posts/all`: Get all posts.
- `GET /posts/get/{id}`: Get post details by ID.
- `GET /posts/{id}/comments`: Get all comments for a specific post.

### Comment Endpoints
- `POST /comments/post/{Id}/addComment`: Add a new comment.
- `PUT /comments/update/{id}`: Update an existing comment.
- `DELETE /comments/delete/{id}`: Delete a comment.
- `GET /comments/all`: Get all comments.
- `GET /comments/get/{id}`: Get comment details by ID.

## Prerequisites
- **Java**: Ensure Java 21 is installed.
- **Maven**: Used for dependency management and building the project.
- **MySQL**: Set up a MySQL database and configure `hibernate.cfg.xml` for database connection.
- **Tomcat**: Install Apache Tomcat or any compatible server.

## Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/pawan00126/RestFulBlogAPI
   cd RestFulBlogAPI
