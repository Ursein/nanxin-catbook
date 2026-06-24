# NanXin CatBook

Campus Stray Cat Information Management & Recommendation System — Software Engineering Course Project

---

## Overview

| Item | Content |
|------|---------|
| **Project** | NanXin CatBook |
| **Course** | Software Engineering (Prof. Cheng Yong) |
| **School** | NUIST, School of Software |
| **Tech Stack** | Backend Spring Boot 3.4.5 + Java 17, Frontend Vue 3 + Vite |
| **Database** | MySQL 8, database `nanxin_maopu_v2` |
| **Core Algorithm** | Cosine Similarity + Heat Factor Fusion Recommendation |

---

## Features

1. **User Authentication** — Register, Login, Logout; 3-tier roles (USER / VERIFIED / ADMIN)
2. **Cat Profile Management** — Add/Edit/Delete (soft delete); parental relationships; status management
3. **Cat Search & Filter** — Paginated list; status tabs; color tags; keyword search; conditional filtering
4. **Similarity Recommendation** — Feature extraction → One-Hot encoding → Cosine similarity → Heat factor fusion → Top-N recommendation
5. **Comments** — Post/View/Delete comments (admin can delete); username display
6. **Photo Management** — Upload/Display/Delete; auto-generated compressed thumbnails; photo likes; cover photo setting
7. **Follow Cats** — Follow/Unfollow; following list page (`/my-follows`)
8. **5-Dimension Rating** — Temper/Looks/Social/Appetite/Energy (1-5 stars); overall score = average of 5 dimensions
9. **Activity Log** — Auto-record browsing, search, likes, ratings, follows

---

## Course Progress

| Content | Output | Status |
|:-------:|--------|:------:|
| 1. Requirements Analysis | `docs/需求分析/需求规格说明书.md` + 3 DFD Diagrams | ✅ |
| 2. Outline Design | `docs/概要设计/概要设计说明书.md` | ✅ |
| 3. Detailed Design | `docs/详细设计/详细设计说明书.md` + Algorithm Pseudocode + UI Design | ✅ |
| Implementation | Spring Boot Backend + Vue 3 Frontend + Recommendation Algorithm | ✅ |
| 4. Software Testing | `docs/测试/测试报告.md` (44 test cases, 100% pass) | ✅ |

---

## Project Structure

```
nanxin-catbook/
├── backend/                        # Spring Boot Backend
│   ├── src/main/java/com/nanxin/catbook/
│   │   ├── config/                 # JWT, CORS, Exception handling
│   │   ├── controller/             # 7 REST controllers
│   │   ├── dto/                    # 17 DTOs
│   │   ├── entity/                 # 8 entity classes
│   │   ├── repository/             # 8 data access interfaces
│   │   └── service/                # 8 business services + core algorithm engine
│   └── src/main/resources/
│       ├── application.yml
│       └── schema.sql              # Database schema
├── frontend/                       # Vue 3 Frontend
│   └── src/
│       ├── router/                 # Routes (9 routes)
│       ├── api/                    # Axios + API wrappers
│       └── views/                  # 9 page components
│           ├── Home.vue            # Home (status tabs + card grid)
│           ├── CatDetail.vue       # Detail (photo carousel/rating/follow/comments)
│           ├── Search.vue          # Search
│           ├── AddCat.vue          # Add cat
│           ├── EditCat.vue         # Edit cat (photo management + cover)
│           ├── MyProfile.vue       # My profile
│           ├── MyFollows.vue       # My follows
│           ├── Login.vue / Register.vue
├── docs/
    ├── 需求分析/                    # Requirements Analysis (Chinese)
    ├── 概要设计/                    # Outline Design (Chinese)
    ├── 详细设计/                    # Detailed Design (Chinese)
    ├── 测试/                        # Testing Report (Chinese)
    └── diagram/                    # 3 DFD SVGs
```

---

## Algorithm

```
Score = α · cos(θ) + β₁ · LikeNorm + β₂ · FollowNorm + β₃ · RatingNorm

cos(θ) = (A · B) / (|A| · |B|)
```

- α=0.7, β₁=β₂=β₃=0.1 (configurable)
- Features: color tags, personality tags, location, gender (One-Hot encoding)
- Heat factors: like count, follow count, 5-dimension average rating (Min-Max normalization)

### 5-Dimension Rating

| Dim | Meaning | Description |
|:---:|:--------|:------------|
| r1 | Temper | How gentle the cat is |
| r2 | Looks | How good-looking the cat is |
| r3 | Social | How friendly the cat is |
| r4 | Appetite | How well the cat eats |
| r5 | Energy | How active the cat is |

Overall = (r1 + r2 + r3 + r4 + r5) / 5

---

## Setup Guide

### Prerequisites

| Tool | Purpose | Download |
|:----|:--------|:---------|
| JDK 17+ | Backend runtime | https://adoptium.net |
| Maven 3.9+ | Backend build | https://maven.apache.org |
| MySQL 8.0 | Database | https://dev.mysql.com/downloads |
| Node.js 18+ | Frontend | https://nodejs.org |
| Git | Version control | https://git-scm.com |

### Quick Start

```bash
# 1. Clone
git clone https://github.com/Ursein/nanxin-catbook.git
cd nanxin-catbook

# 2. Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS nanxin_maopu_v2 DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p nanxin_maopu_v2 < backend/src/main/resources/schema.sql

# 3. Configure database password
# Edit backend/src/main/resources/application.yml → spring.datasource.password

# 4. Start backend (runs on http://localhost:8080)
cd backend
mvn spring-boot:run

# 5. Start frontend (runs on http://localhost:5173)
cd frontend
npm install
npm run dev
```

---

## Key Project Info

- **Backend**: Spring Boot 3.4.5, entry `backend/src/main/java/com/nanxin/catbook/NanxinCatbookApplication.java`
- **Frontend**: Vue 3 + Vite, entry `frontend/src/main.js`
- **Database**: MySQL 8, db `nanxin_maopu_v2`, schema `backend/src/main/resources/schema.sql`
- **Core Algorithm**: Cosine similarity recommendation, see `CatSimilarityAlgorithm.java`
- **Photo Storage**: Local `backend/uploads/`, accessible via `/uploads/`, auto-compressed on upload
- **Cat Deletion**: Soft delete (`deleted=1`), data preserved
- **Rating**: 5-dimension (Temper/Looks/Social/Appetite/Energy), overall = average

---

## License

MIT License
