# 🚀 Backend Engineering Assignment – Spring Boot + Redis

## 📌 Overview

This project implements a high-performance Spring Boot microservice that acts as a central API layer with strict guardrails enforced using Redis. The system ensures safe bot interactions, prevents spam, and maintains real-time virality scoring.

---

## 🛠 Tech Stack

* Java 17
* Spring Boot 3
* PostgreSQL
* Redis (via Docker)
* Spring Data JPA
* Spring Data Redis

---

## ⚙️ Setup Instructions

### 1. Start Infrastructure

```bash
Run any one:

docker compose up

OR (if above doesn't work):

docker-compose up
```

This will start:

* PostgreSQL → `localhost:5433`
* Redis → `localhost:6379`

---

### 2. Run Backend

Run from IntelliJ or:

```bash
mvn spring-boot:run
```

---



---

## 📦 API Endpoints

### 👤 Users

* `POST /api/users` → Create user
* `GET /api/users` → Get all users

---

### 🤖 Bots

* `POST /api/bots` → Create bot

---

### 📝 Posts

* `POST /api/posts` → Create post
* `GET /api/posts/{id}` → Get post
* `GET /api/posts` → Get all posts
* `POST /api/posts/{postId}/like` → Like post (+20 score)

---

### 💬 Comments

* `POST /api/posts/{postId}/comments` → Add comment

Supports:

* Root comments
* Nested replies
* Bot replies

---

## ⚡ Redis-Based Features

### 1. Virality Score (Real-time)

Stored in Redis:

```
post:{id}:score
```

| Action       | Score |
| ------------ | ----- |
| User Comment | +50   |
| Bot Comment  | +1    |
| Like         | +20   |

---

### 2. Atomic Locks (Concurrency Control)

#### Horizontal Cap

```
post:{id}:bot_count
```

* Max 100 bot replies per post
* Enforced using Redis `INCR`

---

#### Vertical Cap

* Max depth = 20
* Checked using parent comment

---

#### Cooldown Cap

```
cooldown:bot_{botId}:user_{userId}
```

* Prevents bot spamming same user
* TTL = 10 minutes

---

## 🔔 Notification Engine

### Redis Throttler

* Key: `notif:cooldown:user_{id}`
* If cooldown exists:

  * Push message to:

    ```
    user:{id}:pending_notifs
    ```
* Else:

  * Send immediate notification
  * Set 15 min cooldown

---

### CRON Scheduler

Runs every 5 minutes:

* Scans:

  ```
  user:*:pending_notifs
  ```
* Aggregates messages
* Logs:

  ```
  Summarized Push Notification: Bot X and N others interacted with your posts.
  ```
* Clears list

---

## 🧠 Thread Safety & Concurrency

Thread safety is guaranteed using Redis atomic operations:

* `INCR` → ensures accurate bot count even under concurrent requests
* `EXISTS` → ensures cooldown enforcement
* TTL-based keys → eliminate race conditions without in-memory state

No in-memory structures (like HashMap) are used → system is fully stateless.

---

## 🧪 Testing Guide

### Flow

1. Create User
2. Create Bot
3. Create Post
4. Add Comments

---

### Edge Cases

#### Bot Limit

* After 100 bot comments → returns `429 Too Many Requests`

#### Depth Limit

* Depth > 20 → rejected

#### Cooldown

* Same bot → same user → blocked within 10 minutes

#### Notifications

* First → immediate
* Next → batched
* Scheduler → summarized output

---

## 🔍 Redis Verification

```bash
docker exec -it redis_db redis-cli
```

Check keys:

```bash
KEYS *
```

Check values:

```bash
GET post:1:score
GET post:1:bot_count
TTL cooldown:bot_1:user_1
LRANGE user:1:pending_notifs 0 -1
```





---



## Yash Agrawal
