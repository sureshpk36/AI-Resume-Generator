# 💼 AI Resume Generator

AI-powered Resume Builder that helps users generate, edit, and download professional resumes from natural language prompts.

## 🚀 Tech Stack

* **Backend:** Java Spring Boot
* **Frontend:** React + Vite
* **Database:** MySQL (Docker Container)
* **AI Integration:** Gemini / DeepSeek APIs

  * *Gemini → Creative resume text generation*
  * *DeepSeek → Structured data extraction*
* **Build Tools:** Maven, Node.js
* **Deployment:** Docker

## 🧠 Overview

The app allows users to enter a short prompt describing their background.
The backend calls an AI model (Gemini or DeepSeek) to generate structured resume data, which the frontend displays dynamically for review, editing, and template-based PDF download.

## 🗂️ Project Structure

```
AI-Resume-Generator/
├── AI-resume-backend/
│   ├── src/main/java/com/resume/backend/AI_resume/backend/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/           # AI prompt + response handling
│   │   ├── parser/            # Gemini & DeepSeek response parsers
│   │   ├── model/             # Resume data models
│   │   └── repo/              # MySQL repositories
│   └── resources/
│       ├── application.properties
│       └── resume_description.txt
│
└── AI-Resume-Generator-Frontend/
    ├── src/
    │   ├── api/               # API client
    │   ├── context/           # FormProvider / useForm
    │   └── pages/             # Dynamic Form, Templates
    ├── package.json
    └── vite.config.js
```

## 🧩 How to Run

### Backend

1. Ensure **Docker** is running.
2. Start **MySQL container**:

   ```bash
   docker run --name mysql-resume -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=resumeDB -p 3306:3306 -d mysql:latest
   ```
3. Update `application.properties` with DB credentials and AI keys.
4. Run backend:

   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend

```bash
cd AI-Resume-Generator-Frontend
npm install
npm run dev
```

Then open your browser at **[http://localhost:5173](http://localhost:5173)** (Vite default).


