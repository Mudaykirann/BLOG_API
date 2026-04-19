# 📝 Blog REST API

A production-ready **RESTful Blog API** built with **Java 17**, **Spring Boot 4**, and **PostgreSQL** — featuring JWT-based authentication, role-based access control, DTO pattern, input validation, and paginated responses.


![In Progress](https://img.shields.io/badge/Status-In%20Progress-green?style=flat-square)
![REST API](https://img.shields.io/badge/Type-REST%20API-blue?style=flat-square)
![MIT License](https://img.shields.io/badge/License-MIT-blueviolet?style=flat-square)


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

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/API/BlogV2/
│   │   ├── controller/      # REST controllers (Auth, Post, Comment)
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA Entities
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── security/        # JWT filter, UserDetailsService, config
│   │   └── service/         # Business logic
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
spring.datasource.url=jdbc:postgresql://localhost:5433/BlogV2Testing
spring.datasource.username=username
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
server.error.include-message=always
server.port=8083
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

### Auth

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT token |

### Users

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/users` | ✅ | Get all Users |
| `GET` | `/api/users/{id}` | ✅ | Get the User by ID |
| `PUT` | `/api/users/{id}` | ✅ | Update the User Details |
| `DELETE` | `/api/users/{id}` | ✅ | Delete the User |


### Posts

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/posts/all` | ❌ | Get all posts (paginated) |
| `GET` | `/api/posts/{id}/all` | ❌ | Get all post by ID |
| `POST` | `/api/posts/{id}/new` | ✅ | Create a new post |
| `PUT` | `/api/posts/{id}` | ✅ | Update a post |
| `DELETE` | `/api/posts/{id}` | ✅ | Delete a post |

### Comments

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/posts/{postId}/comments` | ❌ | Get all comments for a post |
| `POST` | `/api/posts/{postId}/comments` | ✅ | Add a comment to a post |
| `PUT` | `/api/posts/{postId}/comments{commentId}` | ✅ | Update a comment to a post |
| `DELETE` | `/api/posts/{postId}/comments/{commentId}` | ✅ | Delete a comment to a post |

### Pagination Example

```
GET /api/posts?page=0&size=10
```

---

## 📬 Sample Request & Response

### Login

**Request:**
```json
POST /api/auth/login
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
eyJhbGciOiJIUzI1NiJ9.....
```

### Create Post

**Request:**
```json
POST /api/posts
Authorization: Bearer <token>

{
    "user_id":1,
    "author":"Mohan Ranga",
    "content":"THis is the First Blog Post Featured here. with user id - 1",
    "title":"Blog Post -1 by userid-1"
}
```

**Response:**
```json
{
    "status": "success",
    "message": "Post created successfully",
    "data": null
}
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 🔮 Roadmap -- Features need to be Added.
- [ ] Need to Refine the Response Object
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
