# Backend Assignment – Spring Boot + Redis + PostgreSQL

## 🚀 Overview

This project implements a backend system that simulates a social platform with:

* Users and Bots interacting via posts and comments
* Redis-based atomic locks and counters
* Notification throttling and batching
* Concurrency-safe operations

The system is designed to handle **high concurrency**, **prevent spam**, and **batch notifications efficiently**.

---

## 🛠️ Tech Stack

* Java + Spring Boot
* PostgreSQL (persistent storage)
* Redis (caching, atomic operations, throttling)
* Docker (for local setup)

---

## ⚙️ Setup Instructions

### 1. Start services

```bash
docker-compose up
```

### 2. Run application

```bash
mvn spring-boot:run
```

---

## 🗄️ Database Schema

### User

* id
* username
* isPremium

### Bot

* id
* name
* personaDescription

### Post

* id
* authorId (User or Bot)
* content
* createdAt

### Comment

* id
* postId
* authorId
* authorType (USER / BOT)
* parentCommentId
* depthLevel
* content
* createdAt

---

## 📡 API Endpoints

### Create User

```
POST /api/users
```

### Create Bot

```
POST /api/bots
```

### Create Post

```
POST /api/posts
```

### Add Comment

```
POST /api/posts/{postId}/comments
```

### Like Post

```
POST /api/posts/{postId}/like
```

---

## 🔥 Redis-Based Features

---

### 1. Virality Score

* Human Like → +20
* Human Comment → +50
* Bot Comment → +1

Stored in Redis:

```
post:{id}:score
```

Uses:

```
INCR (atomic)
```

---

### 2. Horizontal Cap (Bot Limit)

* Max 100 bot replies per post

Key:

```
post:{id}:bot_count
```

Logic:

```
INCR → if > 100 → reject
```

✅ Ensures strict limit even under concurrency

---

### 3. Vertical Cap (Depth Limit)

* Max depth = 20

Calculated dynamically:

```
depth = parent.depth + 1
```

---

### 4. Cooldown (Bot → User)

* A bot cannot interact with the same user more than once every 10 minutes

Key:

```
cooldown:bot_{botId}:user_{userId}
```

Uses:

```
SET with TTL (600 sec)
```

---

### 5. Notification Throttling

#### Immediate Notification

First interaction:

```
Push Notification Sent to User X
```

#### Cooldown Key

```
notif:cooldown:user_{id}
```

TTL: 15 minutes

---

### 6. Notification Batching

If cooldown exists:

* Notifications are stored in Redis List:

```
user:{id}:pending_notifs
```

---

### 7. Cron Sweeper

Runs every 5 minutes:

```
@Scheduled(fixedRate = 300000)
```

Function:

* Reads all pending notifications
* Counts them
* Logs summary:

```
Summarized Push Notification: Bot X and N others interacted with your posts.
```

* Clears Redis list

---

## 🧠 Concurrency & Thread Safety

The system ensures thread safety using Redis atomic operations:

### Atomic Operations Used

* `INCR` → for bot count and virality score
* `SET with TTL` → for cooldown locks
* `LPUSH / RPUSH` → for notification queue

These operations are **atomic in Redis**, ensuring:

* No race conditions in counters
* Accurate enforcement of limits
* Safe concurrent access

---

## ⚠️ Race Condition Note

The scheduler and request handling run independently.

During testing, it is possible that:

* Scheduler clears notification list
* While new requests are being processed

This can cause partial batching.

In production systems, this would be solved using:

* Distributed locks
* Message queues (Kafka / RabbitMQ)
* Stream processing

---

## 🧪 Testing

A Postman collection is included with:

* Sample requests
* Pre-filled JSON bodies
* Ready-to-run endpoints

---

## 📦 Deliverables

* Spring Boot source code
* docker-compose.yml
* Postman collection
* README (this file)

---

## 🎯 Summary

This system demonstrates:

* Redis-based atomic concurrency control
* Rate limiting using TTL
* Scalable notification batching
* Clean separation of database and cache responsibilities

---

## 👨‍💻 Author

Yash Agrawal

---
