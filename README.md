# 🚀 BlogV2 API - Next-Generation Blogging Backend

![Java](https://img.shields.io/badge/Java-17-orange.svg?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%2B-brightgreen.svg?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-blue.svg?style=flat-square)
![Redis](https://img.shields.io/badge/Redis-Caching-red.svg?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg?style=flat-square)
![Security](https://img.shields.io/badge/Security-Spring_Security_%2B_JWT-success.svg?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)

## 📖 Introduction
BlogV2 is a robust, production-ready RESTful API designed to power modern blogging platforms and content management systems. Built on the Java 17 and Spring Boot 3.x ecosystem, it provides a highly scalable backend solution with secure user authentication, advanced post and comment management, intelligent caching, seamless media integration, and enterprise-grade observability.

Whether you are building a headless CMS, a personal blog, or a large-scale publishing platform, BlogV2 provides the foundational architecture to support high traffic and complex data relationships.

## ✨ Core Features & Capabilities

### 🔐 Advanced Authentication & Security
- **JWT & Refresh Tokens**: Secure, stateless authentication using JSON Web Tokens. Access tokens are short-lived, while Refresh tokens are stored securely in HttpOnly cookies.
- **Role-Based Access Control (RBAC)**: Fine-grained authorization differentiating between `USER` and `ADMIN` roles.
- **Rate Limiting**: Built-in API rate limiting using Bucket4j to prevent abuse and brute-force attacks.
- **Input Sanitization**: Cross-Site Scripting (XSS) prevention using OWASP Java HTML Sanitizer to cleanse all rich-text content before persistence.

### 📝 Comprehensive Content Management
- **Posts Lifecycle**: Create, Read, Update, and Delete (CRUD) operations for posts. Supports `DRAFT` and `PUBLISHED` states.
- **Categories & Tagging**: Organize content efficiently with predefined categories (e.g., TECH, TUTORIAL).
- **SEO-Friendly Slugs**: Automatic, collision-free URL slug generation from post titles.
- **Rich Text & Images**: Seamless integration with **ImageKit** for CDN-backed media storage, ensuring lightning-fast image delivery.

### 💬 Engaging Commenting System
- **Interactive Discussions**: Users can comment on posts, edit their comments, and delete them.
- **Moderation**: Admins have overarching privileges to manage or remove any comments.

### ⚡ Performance Optimization
- **Redis Caching**: Aggressive caching of frequently accessed endpoints (like paginated post feeds and individual posts) using Redis to minimize database load.
- **Pagination & Sorting**: Efficient data retrieval using Spring Data JPA's Pageable interface, preventing memory bloat on large datasets.
- **MapStruct**: High-performance, compile-time object mapping between Entities and DTOs.

### 📊 Observability & DevOps
- **Dockerized Architecture**: Fully containerized environment orchestrating the API, PostgreSQL, and Redis via `docker-compose`.
- **Metrics & Monitoring**: Integrated Spring Boot Actuator with Micrometer. Exposes endpoints for **Prometheus** scraping.
- **Structured JSON Logging**: Centralized logging via Logback and SLF4J with MDC correlation ID tracing across all requests.
- **Grafana Dashboards**: Ready-to-use metrics pipeline to monitor JVM memory, HTTP request latency, and cache hit ratios.
- **Swagger / OpenAPI 3**: Interactive, auto-generated API documentation available out-of-the-box.

---

## 🛠️ Technology Stack

| Category | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Core** | Java | 17 | Primary Programming Language |
| **Framework** | Spring Boot | 3.x+ | Application Framework (Web, Data JPA, Security) |
| **Database** | PostgreSQL | 15+ | Relational Database Management System |
| **Caching** | Redis | 7-alpine | In-Memory Data Store for caching responses |
| **Mapping** | MapStruct | 1.5.5.Final | Entity-to-DTO Mapper |
| **Security** | Spring Security, JWT, Bucket4j | Latest | Auth & Rate Limiting |
| **Media** | ImageKit | 3.0.0 | Cloud Image Storage & CDN |
| **Monitoring** | Prometheus & Grafana | Latest | Telemetry & Observability |
| **Logging** | Logback & SLF4J | Latest | Structured JSON Logging & MDC Tracing |
| **Containerization**| Docker & Docker Compose | 3.8 | Orchestration |
| **Documentation** | Springdoc OpenAPI | 2.8.0 | Swagger UI |

---

## 🚀 Getting Started

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) and Docker Compose
- Java 17 & Maven (if running locally without Docker)

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/BlogV2.git
cd BlogV2
```

### 2. Environment Configuration
Create a `.env` file in the root directory and populate it with your specific credentials:
```env
# Database
DB_PASSWORD=your_secure_postgres_password

# Security
JWT_SECRET=your_super_secret_jwt_key_here_must_be_long

# ImageKit Configuration (Get these from your ImageKit dashboard)
IMAGEKIT_PUBLIC=your_imagekit_public_key
IMAGEKIT_PRIVATE=your_imagekit_private_key
IMAGEKIT_URL=https://ik.imagekit.io/your_endpoint
```

### 3. Launch via Docker Compose
The simplest way to start the entire infrastructure:
```bash
docker-compose up --build -d
```

**Access Points:**
- 🌐 **API Base URL:** `http://localhost:9080/api/v1`
- 📚 **Swagger UI / API Docs:** `http://localhost:9080/swagger-ui/index.html`
- 📈 **Grafana Dashboards:** `http://localhost:3000` (Default login: `admin`/`admin`)

---

## 📡 API Endpoints Overview

*Note: For a complete, interactive list of endpoints and request/response models, visit the Swagger UI.*

### 🔐 Authentication (`/api/v1/auth`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/register` | Register a new user account | Public |
| `POST` | `/login` | Authenticate and receive JWT & Refresh tokens | Public |
| `POST` | `/refresh` | Generate a new access token using a refresh token | Public |

### 📝 Posts (`/api/v1/posts`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Get paginated posts (Cached) | Public |
| `GET` | `/{id}` | Get post details by ID or Slug | Public |
| `GET` | `/search/{keyword}`| Search posts by title | Public |
| `GET` | `/category/{cat}` | Filter posts by category | Public |
| `POST` | `/user/{userId}` | Create a new post | Auth (Owner) |
| `PUT` | `/{id}` | Update an existing post | Auth (Owner) |
| `DELETE`| `/{id}` | Delete a post | Auth (Owner/Admin)|
| `PATCH` | `/{id}/cover-image`| Update the post's cover image | Auth (Owner) |

### 💬 Comments (`/api/v1/posts/{postId}/comments` & `/api/v1/comments`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/posts/{id}/comments`| Get all comments for a post | Public |
| `POST` | `/posts/{id}/comments`| Add a comment to a post | Auth |
| `PUT` | `/comments/{id}` | Update a specific comment | Auth (Owner) |
| `DELETE`| `/comments/{id}` | Delete a comment | Auth (Owner/Admin)|

### 👤 Users (`/api/v1/users`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | List all users | Auth (Admin) |
| `GET` | `/{id}` | Get user profile details | Public |
| `GET` | `/{id}/posts` | Get all posts authored by a user | Public |

### 🖼️ Media (`/api/v1/images`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/upload` | Upload an image to ImageKit | Auth |

---

## 💻 Sample Data Exchange

**Request:** Fetching a Post
`GET /api/v1/posts/1`

**Response:** `200 OK`
```json
{
    "success": true,
    "message": "Post retrieved successfully",
    "data": {
        "id": 1,
        "title": "Mastering Spring Boot 3",
        "slug": "mastering-spring-boot-3",
        "content": "<p>Spring Boot makes it easy to create stand-alone...</p>",
        "coverImageUrl": "https://ik.imagekit.io/your_endpoint/spring.png",
        "authorName": "Jane Doe",
        "status": "PUBLISHED",
        "categories": ["TECH", "TUTORIAL"],
        "commentCount": 12,
        "createdAt": "2026-07-20T10:00:00.000Z",
        "comments": [
            {
                "id": 101,
                "text": "Great article! Very helpful.",
                "authorName": "John Smith",
                "createdAt": "2026-07-21T08:30:00.000Z"
            }
        ]
    },
    "timestamp": "2026-07-31T12:00:00.000Z"
}
```

---

## 📊 Observability (Monitoring & Logging)

BlogV2 is designed with DevSecOps in mind. Out of the box, it provides a comprehensive monitoring stack.
- **Spring Boot Actuator** exposes `/actuator/prometheus` containing JVM, Tomcat, HikariCP, and HTTP metrics.
- **Prometheus** (configured via `prometheus.yml`) periodically scrapes these metrics.
- **Grafana** connects to Prometheus as a data source. 
  - To view metrics, navigate to `http://localhost:3000` (admin/admin).
  - You can import community dashboards like JVM (ID: `4701`) or Spring Boot Observability (ID: `11378`) to instantly visualize API health, latency, and throughput.

### 📜 Structured Logging & Tracing
- **JSON Format**: Uses `logstash-logback-encoder` to output logs in strict JSON format for easy ingestion by log aggregators (like ELK/EFK stack).
- **Log Routing**: Automatically separates `application.log` (INFO+) and `error.log` (ERROR) into the `logs/` directory.
- **MDC Correlation**: A custom `MdcCorrelationFilter` injects a unique UUID (`X-Correlation-Id`) into every request. This ID is included in all business logic and exception logs, providing end-to-end traceability for any transaction.

---

## 🏗️ Architecture & Design Patterns

- **Controller-Service-Repository Pattern**: Clean separation of concerns. Controllers handle HTTP layer, Services contain business logic, Repositories interact with the database.
- **DTO (Data Transfer Object) Pattern**: MapStruct is used to map Entities (which model database tables) to DTOs (which are returned to the client), preventing the exposure of sensitive data like passwords or internal IDs.
- **Exception Handling**: Global exception handling using `@ControllerAdvice` ensures consistent, well-formatted API error responses across all endpoints.

---

## 🤝 Contributing
Contributions, bug reports, and feature requests are always welcome! 

1. **Fork** the repository.
2. **Create a branch** (`git checkout -b feature/AmazingFeature`).
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`).
4. **Push** to the branch (`git push origin feature/AmazingFeature`).
5. **Open a Pull Request**.

---

## 📄 License
This project is open-source and available under the **MIT License**. See the `LICENSE` file for more details.
