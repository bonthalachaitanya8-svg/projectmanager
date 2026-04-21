# Project Manager — Full Stack Spring Boot

A full-stack project management web application built with Spring Boot, Spring Security, Thymeleaf, and H2/MySQL.

---

## Tech Stack (All FREE)

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Backend      | Spring Boot 3.2, Spring MVC       |
| Security     | Spring Security (role-based)      |
| Database     | H2 (dev) / MySQL (prod)           |
| ORM          | Spring Data JPA + Hibernate       |
| Frontend     | Thymeleaf + Bootstrap 5 (CDN)     |
| Build        | Maven                             |
| IDE          | VS Code + Java Extension Pack     |

---

## Prerequisites

1. **Java 17** → https://adoptium.net (Download Temurin JDK 17)
2. **VS Code** → https://code.visualstudio.com
3. **VS Code Extensions:**
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack (VMware)

---

## How to Run

### Option 1 — VS Code Terminal
```bash
cd projectmanager
./mvnw spring-boot:run
```

### Option 2 — VS Code Spring Boot Dashboard
- Open the Spring Boot Dashboard (bottom-left panel)
- Click the ▶ play button on `projectmanager`

### Open in browser:
```
http://localhost:8080/login
```

---

## Default Login Credentials

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | admin    | admin123  |

---

## H2 Database Console (Dev Only)

URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:projectdb`
- Username: `sa`
- Password: *(leave empty)*

---

## Switching to MySQL (Production)

1. Install MySQL Community Server (free): https://dev.mysql.com/downloads/mysql/
2. Create a database:
   ```sql
   CREATE DATABASE projectdb;
   ```
3. In `src/main/resources/application.properties`:
   - Comment out the H2 section
   - Uncomment the MySQL section
   - Set your MySQL username/password

4. In `pom.xml`, uncomment the MySQL dependency block.

---

## Features

### Admin (admin / admin123)
- Dashboard with stats (total projects, active, completed, users)
- Create / Edit / Delete projects
- Assign projects to users
- Add / Remove team members (name, role, email)
- Manage users (create, delete)

### User (alice, bob, carol)
- Read-only portal showing ONLY their assigned projects
- Full project details: description, briefing, dates, team
- Cannot edit anything — contact admin message shown

---

## Project Structure

```
src/main/java/com/projectmanager/
├── config/
│   ├── SecurityConfig.java       ← Spring Security rules
│   └── DataInitializer.java      ← Seeds demo data on startup
├── controller/
│   ├── AuthController.java       ← Login + role-based redirect
│   ├── AdminController.java      ← Full CRUD for admin
│   └── UserController.java       ← Read-only for users
├── model/
│   ├── User.java
│   ├── Project.java
│   └── TeamMember.java
├── repository/
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   └── TeamMemberRepository.java
├── service/
│   ├── UserService.java
│   └── ProjectService.java
└── ProjectManagerApplication.java

src/main/resources/
├── templates/
│   ├── login.html
│   ├── fragments.html            ← Shared navbar fragments
│   ├── admin/
│   │   ├── dashboard.html
│   │   ├── project-form.html
│   │   ├── project-list.html
│   │   ├── user-list.html
│   │   └── user-form.html
│   └── user/
│       ├── dashboard.html
│       └── project-detail.html
└── application.properties
```
