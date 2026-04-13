# Voting System


## Full structure voting system 

Fully functional frontend for an Online Voting System using React, 
Tailwind CSS, and Axios. This frontend connects to a Spring 
Boot REST API running on http://localhost:8080.

## TECH STACK:
```
    - React 18 (Vite)
    - Tailwind CSS
    - Axios (API calls)
    - React Router DOM v6 (navigation)
    - Context API (global state)
    - Recharts (vote result charts)
    - React Hook Form + Yup (form validation)
    - React Toastify (notifications)
    - date-fns (date formatting)
```

## PROJECT STRUCTURE TO GENERATE:

```
    frontend/
    ├── public/
    │   └── index.html
    ├── src/
    │   ├── api/
    │   │   ├── axios.js
    │   │   ├── authAPI.js
    │   │   ├── voterAPI.js
    │   │   ├── adminAPI.js
    │   │   ├── electionAPI.js
    │   │   ├── candidateAPI.js
    │   │   ├── voteAPI.js
    │   │   ├── resultsAPI.js
    │   │   └── notificationAPI.js
    │   ├── assets/
    │   │   ├── logo.png
    │   │   └── default-avatar.png
    │   ├── components/
    │   │   ├── layout/
    │   │   │   ├── Navbar.jsx
    │   │   │   ├── Sidebar.jsx
    │   │   │   ├── Footer.jsx
    │   │   │   └── PageWrapper.jsx
    │   │   ├── common/
    │   │   │   ├── Button.jsx
    │   │   │   ├── Input.jsx
    │   │   │   ├── Modal.jsx
    │   │   │   ├── Table.jsx
    │   │   │   ├── Badge.jsx
    │   │   │   ├── Avatar.jsx
    │   │   │   ├── LoadingSpinner.jsx
    │   │   │   ├── ConfirmDialog.jsx
    │   │   │   ├── Pagination.jsx
    │   │   │   ├── SearchBar.jsx
    │   │   │   └── Toast.jsx
    │   │   ├── charts/
    │   │   │   ├── BarChart.jsx
    │   │   │   ├── PieChart.jsx
    │   │   │   └── StatsCard.jsx
    │   │   ├── election/
    │   │   │   ├── ElectionCard.jsx
    │   │   │   ├── ElectionTimer.jsx
    │   │   │   └── ElectionStatusBadge.jsx
    │   │   ├── candidate/
    │   │   │   ├── CandidateCard.jsx
    │   │   │   └── CandidateForm.jsx
    │   │   └── guards/
    │   │       ├── ProtectedRoute.jsx
    │   │       └── AdminRoute.jsx
    │   ├── context/
    │   │   ├── AuthContext.jsx
    │   │   └── NotificationContext.jsx
    │   ├── hooks/
    │   │   ├── useAuth.js
    │   │   ├── useElections.js
    │   │   ├── useCandidates.js
    │   │   ├── useVoters.js
    │   │   └── useNotifications.js
    │   ├── pages/
    │   │   ├── auth/
    │   │   │   ├── Login.jsx
    │   │   │   ├── Register.jsx
    │   │   │   ├── ForgotPassword.jsx
    │   │   │   └── ResetPassword.jsx
    │   │   ├── voter/
    │   │   │   ├── VoterDashboard.jsx
    │   │   │   ├── ElectionList.jsx
    │   │   │   ├── ElectionDetail.jsx
    │   │   │   ├── VotingPage.jsx
    │   │   │   ├── VoteConfirmation.jsx
    │   │   │   ├── VotingHistory.jsx
    │   │   │   ├── Results.jsx
    │   │   │   └── VoterProfile.jsx
    │   │   └── admin/
    │   │       ├── AdminDashboard.jsx
    │   │       ├── elections/
    │   │       │   ├── ManageElections.jsx
    │   │       │   └── ElectionForm.jsx
    │   │       ├── candidates/
    │   │       │   ├── ManageCandidates.jsx
    │   │       │   └── CandidateForm.jsx
    │   │       ├── voters/
    │   │       │   ├── ManageVoters.jsx
    │   │       │   └── VoterDetail.jsx
    │   │       ├── results/
    │   │       │   ├── ResultsPage.jsx
    │   │       │   └── ResultsChart.jsx
    │   │       ├── AuditLogs.jsx
    │   │       └── Announcements.jsx
    │   ├── utils/
    │   │   ├── tokenHelper.js
    │   │   ├── dateFormatter.js
    │   │   ├── roleHelper.js
    │   │   └── exportHelper.js
    │   ├── App.jsx
    │   ├── main.jsx
    │   └── index.css
    ├── .env
    ├── tailwind.config.js
    ├── vite.config.js
    └── package.json


    backend/
    ├── src/main/java/com/voting/
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   ├── JwtConfig.java
    │   │   └── CorsConfig.java
    │   ├── controller/
    │   │   ├── AuthController.java
    │   │   ├── VoterController.java
    │   │   ├── AdminController.java
    │   │   ├── CandidateController.java
    │   │   ├── ElectionController.java
    │   │   ├── VoteController.java
    │   │   ├── ResultsController.java
    │   │   ├── NotificationController.java
    │   │   ├── AnnouncementController.java
    │   │   └── AuditLogController.java
    │   ├── model/
    │   │   ├── Voter.java
    │   │   ├── Candidate.java
    │   │   ├── Vote.java
    │   │   ├── Election.java
    │   │   ├── ElectionCategory.java
    │   │   ├── Notification.java
    │   │   ├── Announcement.java
    │   │   ├── AuditLog.java
    │   │   └── Role.java
    │   ├── repository/
    │   │   ├── VoterRepository.java
    │   │   ├── CandidateRepository.java
    │   │   ├── VoteRepository.java
    │   │   ├── ElectionRepository.java
    │   │   ├── ElectionCategoryRepository.java
    │   │   ├── NotificationRepository.java
    │   │   ├── AnnouncementRepository.java
    │   │   └── AuditLogRepository.java
    │   ├── service/
    │   │   ├── AuthService.java
    │   │   ├── VoterService.java
    │   │   ├── AdminService.java
    │   │   ├── VotingService.java
    │   │   ├── ElectionService.java
    │   │   ├── CandidateService.java
    │   │   ├── ResultsService.java
    │   │   ├── NotificationService.java
    │   │   ├── AnnouncementService.java
    │   │   ├── AuditLogService.java
    │   │   ├── EmailService.java
    │   │   └── ExportService.java
    │   ├── security/
    │   │   ├── JwtUtil.java
    │   │   ├── JwtFilter.java
    │   │   └── UserDetailsServiceImpl.java
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── RegisterRequest.java
    │   │   │   ├── VoteRequest.java
    │   │   │   ├── CandidateRequest.java
    │   │   │   ├── ElectionRequest.java
    │   │   │   ├── AnnouncementRequest.java
    │   │   │   ├── ResetPasswordRequest.java
    │   │   │   └── UpdateProfileRequest.java
    │   │   └── response/
    │   │       ├── ApiResponse.java
    │   │       ├── AuthResponse.java
    │   │       ├── VoterResponse.java
    │   │       ├── CandidateResponse.java
    │   │       ├── ElectionResponse.java
    │   │       └── ResultsResponse.java
    │   ├── exception/
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── ResourceNotFoundException.java
    │   │   ├── AlreadyVotedException.java
    │   │   ├── ElectionClosedException.java
    │   │   └── UnauthorizedException.java
    │   └── VotingApplication.java
    ├── src/main/resources/
    │   ├── application.properties
    │   └── schema.sql
    └── pom.xml
    
```

## DATABASE STRUCTURE REFERENCE (read-only,
just use this to understand the data shape for API calls):

```
    voters table:
    - id, full_name, email, password, national_id, phone,
    address, profile_picture, role (ADMIN/VOTER),
    status (PENDING/ACTIVE/DEACTIVATED),
    email_verified, created_at

    elections table:
    - id, title, description, start_time, end_time,
    is_active, results_locked, created_by, created_at

    election_categories table:
    - id, election_id, category_name

    candidates table:
    - id, full_name, party, bio, photo_url,
    election_category_id

    votes table:
    - id, voter_id, candidate_id,
    election_category_id, voted_at

    notifications table:
    - id, voter_id, message, is_read, created_at

    announcements table:
    - id, title, message, created_by, created_at

    audit_logs table:
    - id, user_id, action, details, timestamp
```

## API CALLS REFERENCE - All axios calls must use these 
```
    exact endpoints from http://localhost:8080:

    authAPI.js:
    - register(data) → POST /api/auth/register
    - login(data) → POST /api/auth/login
    - forgotPassword(email) → POST /api/auth/forgot-password
    - resetPassword(data) → POST /api/auth/reset-password
    - verifyEmail(token) → GET /api/auth/verify-email?token=

    voterAPI.js:
    - getProfile() → GET /api/voter/profile
    - updateProfile(data) → PUT /api/voter/profile
    - uploadPhoto(file) → PUT /api/voter/profile/photo
    - deleteAccount() → DELETE /api/voter/account
    - getVotingHistory() → GET /api/voter/history
    - getNotifications() → GET /api/voter/notifications
    - markNotificationRead(id) → PUT /api/voter/notifications/{id}/read

    electionAPI.js:
    - getAllElections() → GET /api/elections
    - getActiveElections() → GET /api/elections/active
    - getUpcomingElections() → GET /api/elections/upcoming
    - getPastElections() → GET /api/elections/past
    - getElectionById(id) → GET /api/elections/{id}

    voteAPI.js:
    - castVote(data) → POST /api/votes/cast
    - checkVoteStatus(electionId) → GET /api/votes/status/{electionId}

    resultsAPI.js:
    - getResults(electionId) → GET /api/admin/results/{electionId}
    - exportPDF(electionId) → GET /api/admin/results/{electionId}/export/pdf
    - exportCSV(electionId) → GET /api/admin/results/{electionId}/export/csv

    notificationAPI.js:
    - getNotifications() → GET /api/voter/notifications
    - markAsRead(id) → PUT /api/voter/notifications/{id}/read

    adminAPI.js (voters):
    - getAllVoters() → GET /api/admin/voters
    - getPendingVoters() → GET /api/admin/voters/pending
    - getVoterById(id) → GET /api/admin/voters/{id}
    - getVoterActivity(id) → GET /api/admin/voters/{id}/activity
    - approveVoter(id) → PUT /api/admin/voters/{id}/approve
    - deactivateVoter(id) → PUT /api/admin/voters/{id}/deactivate
    - resetVoterPassword(id) → PUT /api/admin/voters/{id}/reset-password

    adminAPI.js (elections):
    - getAllElections() → GET /api/admin/elections
    - createElection(data) → POST /api/admin/elections
    - updateElection(id, data) → PUT /api/admin/elections/{id}
    - deleteElection(id) → DELETE /api/admin/elections/{id}
    - publishElection(id) → PUT /api/admin/elections/{id}/publish
    - lockResults(id) → PUT /api/admin/elections/{id}/lock-results

    adminAPI.js (candidates):
    - getAllCandidates() → GET /api/admin/candidates
    - createCandidate(data) → POST /api/admin/candidates
    - updateCandidate(id, data) → PUT /api/admin/candidates/{id}
    - deleteCandidate(id) → DELETE /api/admin/candidates/{id}

    adminAPI.js (system):
    - getAuditLogs() → GET /api/admin/audit-logs
    - getAnnouncements() → GET /api/admin/announcements
    - createAnnouncement(data) → POST /api/admin/announcements
    - updateAnnouncement(id, data) → PUT /api/admin/announcements/{id}
    - deleteAnnouncement(id) → DELETE /api/admin/announcements/{id}

```
## AXIOS SETUP (axios.js):

```
    - Base URL: http://localhost:8080
    - Attach JWT token automatically from localStorage 
    to every request Authorization header as Bearer token
    - Response interceptor: if 401 received, clear token 
    and redirect to /login
    - Request interceptor: attach token if it exists
```

```
    ROUTE PROTECTION:
    - ProtectedRoute.jsx: redirect to /login if not 
    authenticated
    - AdminRoute.jsx: redirect to /voter/dashboard if 
    authenticated but not ADMIN role
    - App.jsx routes:
    / → redirect based on role after login
    /login → Login.jsx (public)
    /register → Register.jsx (public)
    /forgot-password → ForgotPassword.jsx (public)
    /reset-password → ResetPassword.jsx (public)
    /voter/dashboard → VoterDashboard.jsx (protected)
    /voter/elections → ElectionList.jsx (protected)
    /voter/elections/:id → ElectionDetail.jsx (protected)
    /voter/vote/:id → VotingPage.jsx (protected)
    /voter/vote/:id/confirm → VoteConfirmation.jsx (protected)
    /voter/history → VotingHistory.jsx (protected)
    /voter/results/:id → Results.jsx (protected)
    /voter/profile → VoterProfile.jsx (protected)
    /admin/dashboard → AdminDashboard.jsx (admin only)
    /admin/elections → ManageElections.jsx (admin only)
    /admin/elections/new → ElectionForm.jsx (admin only)
    /admin/elections/:id/edit → ElectionForm.jsx (admin only)
    /admin/candidates → ManageCandidates.jsx (admin only)
    /admin/candidates/new → CandidateForm.jsx (admin only)
    /admin/candidates/:id/edit → CandidateForm.jsx (admin only)
    /admin/voters → ManageVoters.jsx (admin only)
    /admin/voters/:id → VoterDetail.jsx (admin only)
    /admin/results/:id → ResultsPage.jsx (admin only)
    /admin/audit-logs → AuditLogs.jsx (admin only)
    /admin/announcements → Announcements.jsx (admin only)
```

```
    FORM VALIDATION with React Hook Form + Yup:

    Register form schema:
    - full_name: required, min 3 chars
    - email: required, valid email format
    - national_id: required, min 6 chars
    - phone: required, valid phone number
    - password: required, min 8 chars,
    must contain uppercase, number, special char
    - confirm_password: must match password

    Login form schema:
    - email: required, valid email
    - password: required

    Election form schema:
    - title: required, min 5 chars
    - description: required, min 10 chars
    - start_time: required, must be future date
    - end_time: required, must be after start_time

    Candidate form schema:
    - full_name: required
    - party: required
    - bio: required, min 20 chars
    - election_category_id: required

    .env file:
    VITE_API_BASE_URL=http://localhost:8080

```



You are an expert Java Spring Boot developer. Create a complete, 
fully functional backend for an Online Voting System with the 
following specifications:

TECH STACK:
- Java Spring Boot
- Spring Security + JWT Authentication
- Spring Data JPA + Hibernate
- MySQL Database
- Maven build tool
- JavaMailSender for email services
- iTextPDF for PDF export
- OpenCSV for CSV export

DATABASE SCHEMA - Create these exact tables:

1. voters (id, full_name, email, password, national_id, phone, 
   address, profile_picture, role ENUM(ADMIN,VOTER), 
   status ENUM(PENDING,ACTIVE,DEACTIVATED), 
   email_verified BOOLEAN, created_at TIMESTAMP)

2. elections (id, title, description, start_time DATETIME, 
   end_time DATETIME, is_active BOOLEAN, results_locked BOOLEAN, 
   created_by, created_at TIMESTAMP)

3. election_categories (id, election_id FK, category_name)

4. candidates (id, full_name, party, bio, photo_url, 
   election_category_id FK)

5. votes (id, voter_id FK, candidate_id FK, 
   election_category_id FK, voted_at TIMESTAMP)

6. notifications (id, voter_id FK, message, is_read BOOLEAN, 
   created_at TIMESTAMP)

7. announcements (id, title, message, created_by FK, 
   created_at TIMESTAMP)

8. audit_logs (id, user_id FK, action, details, 
   timestamp TIMESTAMP)

FULL PROJECT STRUCTURE TO GENERATE:




ENDPOINTS TO IMPLEMENT:

```

    Auth:
    POST /api/auth/register
    POST /api/auth/login
    POST /api/auth/forgot-password
    POST /api/auth/reset-password
    GET  /api/auth/verify-email?token=

    Voter:
    GET    /api/voter/profile
    PUT    /api/voter/profile
    PUT    /api/voter/profile/photo
    DELETE /api/voter/account
    GET    /api/voter/history
    GET    /api/voter/notifications
    PUT    /api/voter/notifications/{id}/read

    Elections (voter view):
    GET /api/elections
    GET /api/elections/active
    GET /api/elections/upcoming
    GET /api/elections/past
    GET /api/elections/{id}

    Voting:
    POST /api/votes/cast
    GET  /api/votes/status/{electionId}

    Admin - Voters:
    GET  /api/admin/voters
    GET  /api/admin/voters/pending
    GET  /api/admin/voters/{id}
    GET  /api/admin/voters/{id}/activity
    PUT  /api/admin/voters/{id}/approve
    PUT  /api/admin/voters/{id}/deactivate
    PUT  /api/admin/voters/{id}/reset-password

    Admin - Elections:
    GET    /api/admin/elections
    POST   /api/admin/elections
    PUT    /api/admin/elections/{id}
    DELETE /api/admin/elections/{id}
    PUT    /api/admin/elections/{id}/publish
    PUT    /api/admin/elections/{id}/lock-results

    Admin - Candidates:
    GET    /api/admin/candidates
    POST   /api/admin/candidates
    PUT    /api/admin/candidates/{id}
    DELETE /api/admin/candidates/{id}

    Admin - Results:
    GET /api/admin/results/{electionId}
    GET /api/admin/results/{electionId}/export/pdf
    GET /api/admin/results/{electionId}/export/csv

    Admin - System:
    GET  /api/admin/audit-logs
    GET  /api/admin/announcements
    POST /api/admin/announcements
    PUT  /api/admin/announcements/{id}
    DELETE /api/admin/announcements/{id}

```

DTOs:
- All request DTOs need @Valid, @NotBlank, @Email 
  validation annotations
- All response DTOs should be clean (no passwords)
- ApiResponse should wrap all responses with 
  success boolean, message, and data fields

Exception Handling:
- GlobalExceptionHandler: handle all custom exceptions,
  return proper HTTP status codes (404, 400, 401, 403, 409)
- Each exception should return meaningful error message

application.properties must include:

```
    spring.datasource.url=jdbc:mysql://localhost:3306/voting_db
    spring.datasource.username=root
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    jwt.secret=your_jwt_secret_key_here
    jwt.expiration=86400000
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your_email
    spring.mail.password=your_app_password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
```


pom.xml must include these dependencies:
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-mail
- spring-boot-starter-validation
- mysql-connector-java
- jjwt-api, jjwt-impl, jjwt-jackson (version 0.11.5)
- itext7-core (for PDF export)
- opencsv (for CSV export)
- lombok
- spring-boot-starter-test



a voting system
