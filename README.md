# 🚀 BlogV2 API

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%2B-brightgreen.svg)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## 📖 Intro
BlogV2 is a robust, production-ready RESTful API designed to power modern blogging platforms. It provides a complete backend solution with secure user authentication, advanced post and comment management, caching, image uploading, and real-time observability.

## 📝 Description
Built entirely on the Java Spring Boot ecosystem, BlogV2 is engineered for speed and scalability. It handles everything from JWT-based authentication and role-based access control (RBAC) to integrating seamlessly with external services like ImageKit for fast CDN image delivery. The system is fully containerized using Docker, making deployment a breeze, and includes a complete Prometheus + Grafana stack for monitoring API health and performance in real-time.

## 🎯 What It Solves
Building a blogging platform from scratch requires solving the same complex backend challenges over and over again. BlogV2 solves this by providing an out-of-the-box solution for:
- **Authentication:** Securing endpoints and managing user sessions safely.
- **Data Relationships:** Handling the complex One-to-Many and Many-to-Many relationships between Users, Posts, Comments, and Categories.
- **Performance bottlenecks:** Utilizing Redis to cache frequent database queries (like paginated post feeds) to prevent database overload.
- **Media Management:** Offloading image storage to a dedicated CDN rather than bloating the local database.

## 💡 Why It Exists
This project exists to serve as both a high-performance backend for a frontend application (like React, Vue, or Next.js) and as a masterclass template demonstrating enterprise-level Spring Boot architecture. It incorporates best practices such as DTO pattern mapping (via MapStruct), unified exception handling, Spring Security, and DevSecOps observability tools.

## 🛠️ Tech Stack

| Technology | Version / Details | Purpose |
| :--- | :--- | :--- |
| **Java** | 17 | Core Programming Language |
| **Spring Boot** | 3.x+ | Application Framework |
| **Spring Security** | Latest | JWT Authentication & Authorization |
| **Spring Data JPA** | Latest | ORM / Database Interaction |
| **PostgreSQL** | 15+ | Primary Relational Database |
| **Redis** | 7-alpine | In-Memory Data Structure Store / Caching |
| **MapStruct** | 1.5.5.Final | Entity to DTO Mapping |
| **ImageKit** | 3.0.0 | Cloud Image Storage & CDN |
| **Prometheus** | Latest | Metrics Scraping & Time-Series DB |
| **Grafana** | Latest | Metrics Visualization & Dashboards |
| **Docker Compose** | 3.8 | Container Orchestration |

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose installed
- Java 17 (if running locally without Docker)
- Maven

### Installation & Running (Docker)
The easiest way to run the entire stack (API, Database, Cache, and Monitoring) is via Docker.

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/BlogV2.git
   cd BlogV2
   ```

2. **Set up Environment Variables**
   Create a `.env` file in the root directory and add your secrets:
   ```env
   DB_PASSWORD=your_secure_password
   JWT_SECRET=your_super_secret_jwt_key_here
   IMAGEKIT_PUBLIC=your_imagekit_public_key
   IMAGEKIT_PRIVATE=your_imagekit_private_key
   IMAGEKIT_URL=https://ik.imagekit.io/your_endpoint
   ```

3. **Spin up the stack**
   ```bash
   docker-compose up --build -d
   ```
   *The API will be available at `http://localhost:9080`.*
   *Grafana Dashboards will be available at `http://localhost:3000`.*

---

## 📡 API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/v1/auth/register` | Register a new user | ❌ |
| `POST` | `/api/v1/auth/login` | Login and receive JWT token | ❌ |

### 📝 Posts
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/v1/posts` | Get all posts (Paginated & Cached) | ❌ |
| `GET` | `/api/v1/posts/{id}` | Get a single post by ID | ❌ |
| `GET` | `/api/v1/users/{userId}/posts` | Get all posts by a specific user | ❌ |
| `POST` | `/api/v1/users/{userId}/posts` | Create a new post | ✅ (User/Admin) |
| `PUT` | `/api/v1/posts/{id}` | Update an existing post | ✅ (Owner) |
| `DELETE`| `/api/v1/posts/{id}` | Delete a post | ✅ (Owner/Admin) |
| `GET` | `/api/v1/posts/search/{keyword}`| Search posts by title | ❌ |
| `GET` | `/api/v1/posts/category/{cat}` | Filter posts by category | ❌ |
| `PATCH` | `/api/v1/posts/{id}/cover-image`| Update the cover image URL | ✅ (Owner) |

### 💬 Comments
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/v1/posts/{post_id}/comments`| Get all comments for a post | ❌ |
| `POST` | `/api/v1/posts/{post_id}/comments`| Add a comment to a post | ✅ |
| `PUT` | `/api/v1/comments/{id}` | Update a comment | ✅ (Owner) |
| `DELETE`| `/api/v1/comments/{id}` | Delete a comment | ✅ (Owner/Admin) |

### 👤 Users
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/v1/users` | Get all users | ✅ (Admin) |
| `GET` | `/api/v1/users/{id}` | Get user profile | ❌ |

---

## 💻 Sample Request & Response

**Request:** `GET /api/v1/posts?page=0&size=5`

**Response:** `200 OK`
```json
{
    "success": true,
    "message": "Post retrieved",
    "data": {
        "content": [
            {
                "id": 1,
                "title": "Getting started with Spring Boot",
                "content": "Spring boot makes Java fun again...",
                "coverImageUrl": "https://ik.imagekit.io/abc/spring.png",
                "authorName": "Thorfinn",
                "categories": ["TECH", "TUTORIAL"],
                "commentCount": 5,
                "createdAt": "2026-06-15T10:00:00.000Z",
                "comments": []
            }
        ],
        "pageNumber": 0,
        "pageSize": 5,
        "totalElements": 1,
        "totalPages": 1,
        "last": true
    },
    "timestamp": "2026-06-16T12:00:00.000Z"
}
```

---

## 📊 Observability (Monitoring)
This project includes a fully configured metrics pipeline out of the box!
- **Prometheus** scrapes API data (Cache hits, HTTP response times, Memory usage) via Spring Boot Actuator every 5 seconds.
- **Grafana** visualizes this data. 
- *To access:* Navigate to `http://localhost:3000` (admin/admin) and import dashboard `4701` or `11378`.

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! 
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.

---

## ✍️ Author
**Your Name / GitHub Username**
- [GitHub Profile](https://github.com/yourusername)
- [LinkedIn](https://linkedin.com/in/yourusername)
