# 📬 Notification Service

A lightweight, standalone microservice for sending email notifications. Built with Spring Boot any application can call it via a simple HTTP request and an email goes out.

---

## Overview

Most applications need to notify users. Instead of building email logic into every app, this service centralizes it. Any system that can make an HTTP POST request can send a notification.

```
Your Application
      ↓
POST /notifications
      ↓
Notification Service
      ↓
Email delivered to user
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Database | PostgreSQL |
| Email | Spring Mail + Brevo SMTP |
| Templates | Thymeleaf |
| Build | Maven |

---

## Features

- Send HTML emails via template or plain message
- Reusable Thymeleaf email templates with dynamic data
- Every notification logged in PostgreSQL with status tracking
- Clean error handling failures are logged, never silent
- Easily extendable to SMS and Push (structure already in place)

---

## Project Structure

```
src/main/java/com/notificationservice/
├── controller/
│   └── NotificationController.java     # REST endpoints
├── service/
│   ├── NotificationService.java        # Orchestration logic
│   └── EmailService.java               # Email sending
├── repository/
│   └── NotificationRepository.java
├── model/
│   └── Notification.java               # DB entity
├── dto/
│   ├── NotificationRequest.java
│   └── NotificationResponse.java
├── enums/
│   ├── NotificationType.java           # EMAIL, SMS, PUSH
│   └── NotificationStatus.java        # PENDING, SENT, FAILED
└── config/
    └── GlobalExceptionHandler.java

src/main/resources/
├── templates/
│   ├── welcome_email.html
│   ├── password_reset.html
│   ├── document_issued.html
│   └── document_revoked.html
└── application.yml
```

---

## API

### Send a Notification

```
POST /notifications
Content-Type: application/json
```

**Using a template:**
```json
{
  "type": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Welcome!",
  "templateName": "welcome_email",
  "data": {
    "name": "Pluto"
  }
}
```

**Using a plain message:**
```json
{
  "type": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Hello",
  "message": "<p>Your document has been verified.</p>"
}
```

**Response:**
```json
{
  "id": 1,
  "type": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Welcome!",
  "status": "SENT",
  "errorMessage": null,
  "createdAt": "2026-03-14T10:30:00",
  "sentAt": "2026-03-14T10:30:01"
}
```

---

### Get Notification Status

```
GET /notifications/{id}
```

---

### Get All Notifications

```
GET /notifications
```

---

## Notification Statuses

| Status | Meaning |
|---|---|
| `PENDING` | Created, not yet sent |
| `SENT` | Successfully delivered |
| `FAILED` | Sending failed, error logged |

---

## Email Templates

Templates live in `src/main/resources/templates/`. Reference them by filename without the `.html` extension.

| Template | File | Variables |
|---|---|---|
| `welcome_email` | `welcome_email.html` | `name` |
| `password_reset` | `password_reset.html` | `name`, `resetLink` |
| `document_issued` | `document_issued.html` | `name`, `documentName`, `verificationCode`, `issuedBy` |
| `document_revoked` | `document_revoked.html` | `name`, `documentName`, `reason` |

To add a new template, create a `.html` file in the templates folder and reference it by name in the request body.

---

## Setup

### Prerequisites

- Java 17
- Maven
- PostgreSQL
- A Brevo account (free tier 300 emails/day)

### 1. Create the database

```sql
CREATE DATABASE notification_db;
```

### 2. Configure `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification_db
    username: postgres
    password: yourpassword

  mail:
    host: smtp-relay.brevo.com
    port: 587
    username: your-brevo-login@email.com
    password: your-brevo-smtp-key

notification:
  email:
    from: your-brevo-login@email.com
    from-name: Notification Service
```

### 3. Run

```bash
mvn spring-boot:run
```

Service runs on `http://localhost:8080`

---

## How Other Apps Call This Service

From any application Java, Node.js, Python, or a simple curl:

```bash
curl -X POST http://localhost:8080/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EMAIL",
    "recipient": "user@example.com",
    "subject": "Your document is ready",
    "templateName": "document_issued",
    "data": {
      "name": "Pluto",
      "documentName": "Bachelor of Science in Computer Science",
      "verificationCode": "TZ-EDU-82911",
      "issuedBy": "University of Dar es Salaam"
    }
  }'
```

---

## Currently Integrated With

- **[DDVS Digital Document Verification System](https://github.com/YOUR_USERNAME/ddvs)**
  - User registration → welcome email
  - Document issued → document issued email
  - Document revoked → document revoked email

---

## Deployment

Deployed on [Render](https://render.com) as a standalone web service.

Environment variables required in production:

| Variable | Description |
|---|---|
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `MAIL_USERNAME` | Brevo SMTP login |
| `MAIL_PASSWORD` | Brevo SMTP key |
| `MAIL_FROM` | Verified sender email |
| `MAIL_FROM_NAME` | Sender display name |

---

## What's Next

| When you need it | Add this |
|---|---|
| Slow responses under load | Redis queue + async worker |
| Emails occasionally fail | Retry logic with backoff |
| Need to text users | Twilio SMS integration |
| Need mobile alerts | Firebase push notifications |
| Want to monitor sends | Admin dashboard |
