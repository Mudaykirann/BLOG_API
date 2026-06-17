# 📝 Blog REST API

A production-ready **RESTful Blog API** built with **Java 17**, **Spring Boot 4**, and **PostgreSQL** — featuring JWT-based authentication, Refresh Tokens, role-based access control, ImageKit integration, DTO pattern, input validation, and paginated responses.

![In Progress](https://img.shields.io/badge/Status-Active-green?style=flat-square)
![REST API](https://img.shields.io/badge/Type-REST%20API-blue?style=flat-square)
![MIT License](https://img.shields.io/badge/License-MIT-blueviolet?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Springboot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Supported-blue?style=flat-square&logo=docker)

> Built as a backend portfolio project to demonstrate real-world API design with Spring Boot and Spring Security.

---

## 🚀 Features

- 🔐 **JWT Authentication** — Secure login and token-based session management using `jjwt 0.12.6`.
- 🔄 **Refresh Tokens** — Advanced authentication flow with secure refresh tokens.
- 🛡️ **Spring Security** — Role-based access control (e.g. Admin vs. User).
- 📄 **Blog Post CRUD** — Create, read, update, and delete blog posts.
- 🗂️ **Categories** — Categorize blog posts for better organization.
- 💬 **Comments** — Nested comment support per post.
- 🖼️ **ImageKit Integration** — Built-in support for image uploads via ImageKit.
- 📦 **DTO Pattern** — Clean separation between API layer and database entities using MapStruct.
- ✅ **Input Validation** — Bean Validation (`@Valid`) on all request bodies.
- 📃 **Pagination & Sorting** — Paginated responses for posts and comments.
- 📖 **OpenAPI / Swagger** — Auto-generated, interactive API documentation.
- 🐳 **Dockerized** — Fully containerized setup with Docker Compose for Postgres and the app.

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
| Mapping | MapStruct |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Cloud Storage | ImageKit |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/API/BlogV2/
│   │   ├── Controller/      # REST controllers (Auth, Post, Comment, ImageKit, RefreshToken)
│   │   ├── DTO/             # Request/Response DTOs and Mappers
│   │   ├── Entity/          # JPA Entities
│   │   ├── Repository/      # Spring Data JPA repositories
│   │   ├── Utils/           # Security, JWT filter, OpenAPI, ImageKit Config
│   │   ├── Service/         # Business logic
│   │   └── Exception/       # Global Exception Handling
│   └── resources/
│       └── application.yml
```

---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 14+ (if running locally without Docker)
- Maven 3.8+
- Docker & Docker Compose (optional, for containerized run)

### 1. Clone the repository

```bash
git clone https://github.com/Mudaykirann/BLOG_API.git
cd BLOG_API
```

### 2. Running with Docker (Recommended)

Set up your `.env` file based on `.env.example` (or configure directly in `docker-compose.yml` environment variables):

```env
DB_PASSWORD=your_db_password
IMAGEKIT_PUBLIC=your_public_key
IMAGEKIT_PRIVATE=your_private_key
IMAGEKIT_URL=your_url_endpoint
```

Run the application and database using Docker Compose:

```bash
docker-compose up --build
```
The API will start at `http://localhost:9080` (or `8090` based on port mapping).

### 3. Running Locally (Without Docker)

Create a PostgreSQL database:

```sql
CREATE DATABASE BlogThor;
```

Update `src/main/resources/application.yml` with your database and ImageKit credentials. Then, run the app:

```bash
./mvnw spring-boot:run
```

The API will start at `http://localhost:8090`.

---

## 🔑 Authentication

This API uses **JWT Bearer tokens**. To access protected endpoints:

1. Register or login to get an access token and refresh token.
2. Pass the token in the `Authorization` header:

```
Authorization: Bearer <your_access_token>
```

When the token expires, use the refresh token endpoint to generate a new access token.

---

## 📡 API Documentation

Interactive API documentation is available via **Swagger UI**.
Once the application is running, navigate to:

```
http://localhost:8090/swagger-ui/index.html
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

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
