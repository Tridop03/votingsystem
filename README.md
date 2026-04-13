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

a voting system
