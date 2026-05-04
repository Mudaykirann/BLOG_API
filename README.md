# 📝 Blog REST API

Built a production-ready backend system for a RESTful Blog API using Java 17, Spring Boot 4, and PostgreSQL, with JWT authentication, role-based access, validation, and pagination.


![In Progress](https://img.shields.io/badge/Status-In%20Progress-green?style=flat-square)
![REST API](https://img.shields.io/badge/Type-REST%20API-blue?style=flat-square)
![MIT License](https://img.shields.io/badge/License-MIT-blueviolet?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Springboot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-blue?style=flat-square&logo=postgresql)


> Built as a backend portfolio project to demonstrate real-world API design with Spring Boot and Spring Security.

---

## 🚀 Features

- 🔐 **JWT Authentication** — Secure login and token-based session management using `jjwt 0.12.6`
- 🛡️ **Spring Security** — Role-based access control (e.g. Admin vs. User)
- 📄 **Blog Post CRUD** — Create, read, update, and delete blog posts
- 💬 **Comments** — Nested comment support per post
- 📦 **DTO Pattern** — Clean separation between API layer and database entities
- ✅ **Input Validation** — Bean Validation (`@Valid`) on all request bodies
- 📃 **Pagination & Sorting** — Paginated responses for posts and comments
- 🗄️ **PostgreSQL** — Persistent relational data storage via Spring Data JPA
- 🖼️ **ImageKit** — Uploading of user profile and post cover images.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Validation | Spring Boot Starter Validation |
| Image Upload | ImageKit.io |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/API/BlogV2/
│   │   ├── Controller/      # REST controllers (Auth, Post, Comment)
│   │   ├── DTO/             # Request/Response DTOs
│   │   ├── Entity/          # JPA Entities
│   │   ├── Repository/      # Spring Data JPA repositories
│   │   ├── Utils/           # JWT filter, UserDetailsService, config
│   │   └── Service/         # Business logic
│   │   └── Exception/       # Global Exceptions
│   └── resources/
│       └── application.properties
```

---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### 1. Clone the repository

```bash
git clone https://github.com/Mudaykirann/BLOG_API.git
cd BLOG_API
```

### 2. Configure the database

Create a PostgreSQL database:

```sql
CREATE DATABASE blogdb;
```

Update `src/main/resources/application.properties`:

```properties
spring.application.name=BlogV2
spring.datasource.url=jdbc:postgresql://localhost:5433/DB_Name
spring.datasource.username=username
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
server.error.include-message=always
server.port=8083
imagekit.public-key=your-public_key
imagekit.private-key=your-private_key
imagekit.url-endpoint=https://ik.imagekit.io/yourId
```

### 3. Build and run

```bash
./mvnw spring-boot:run
```

The API will start at `http://localhost:8083`

---

## 🔑 Authentication

This API uses **JWT Bearer tokens**. To access protected endpoints:

1. Register or login to get a token
2. Pass the token in the `Authorization` header:

```
Authorization: Bearer <your_token>
```

---

## 📡 API Endpoints

> All endpoints are prefixed with `/api/v1`

### Auth

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Register a new user |
| `POST` | `/api/v1/auth/login` | Login and receive JWT token |

### Users

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/v1/users` | ✅ | Get all Users |
| `GET` | `/api/v1/users/{id}` | ✅ | Get user details by ID |
| `PUT` | `/api/v1/user/{id}` | ✅ | Update the User Details |
| `DELETE` | `/api/v1/user/{id}` | ✅ | Delete the User |

### Posts

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/v1/posts` | ❌ | Get all posts (paginated) |
| `GET` | `/api/v1/users/{userId}/posts` | ❌ | Get all posts by a specific user |
| `POST` | `/api/v1/users/{userId}/posts` | ✅ | Create a new post for a specific user |
| `PUT` | `/api/v1/posts/{postId}` | ✅ | Update a post |
| `DELETE` | `/api/v1/posts/{postId}` | ✅ | Delete a post |

### Comments

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/v1/posts/{postId}/comments` | ❌ | Get all comments for a post |
| `POST` | `/api/v1/posts/{postId}/comments` | ✅ | Add a comment to a post |
| `PUT` | `/api/v1/posts/{postId}/comments/{commentId}` | ✅ | Update a comment on a post |
| `DELETE` | `/api/v1/posts/{postId}/comments/{commentId}` | ✅ | Delete a comment on a post |

### Pagination Example

```
GET /api/v1/posts?page=0&size=10
```

---

## 📬 Sample Request & Response

### Login

**Request:**
```json
POST /api/v1/auth/login
{
    "name": "mohan",
    "email": "mohan@gmail.com",
    "occupation": "barber",
    "password": "m@123",
    "role":"ADMIN"
}
```

**Response:**
```json
{
    "data": "eyJhbGciOiJIUzI1NiJ9............",
    "message": "Token Fetched successfully",
    "success": true,
    "timestamp": "2026-04-22T11:37:20.967537"
}
```

### Create Post

**Request:**
```json
POST /api/v1/users/{userId}/posts
Authorization: Bearer <token>

{
    "user_id":UserId,
    "author":"John Doe",
    "content":"THis is the First Blog Post Featured here. with user id - 1",
    "title":"Blog Post -1 by userid-1"
}
```

**Response:**
```json
{
    "message": "Post created successfully",
    "success": true,
    "timestamp": "2026-04-22T11:39:10.3775333"
}
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 🔮 Roadmap -- Features need to be Added.
- [ ] Add category filtering and tag support
- [ ] Image upload for blog posts
- [ ] Email verification on registration
- [ ] Swagger / OpenAPI documentation
- [ ] Docker support

---

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add my feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👨‍💻 Author

**Mudaykiran**
- GitHub: [@Mudaykirann](https://github.com/Mudaykirann)

---

⭐ If you found this project helpful, please give it a star — it helps others discover it!
