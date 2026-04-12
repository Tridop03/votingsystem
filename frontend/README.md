# Online Voting System - Frontend

A modern, secure, and responsive web application for managing and participating in online elections. Built with React 18, TypeScript, and Tailwind CSS.

## 🚀 Features

### 🗳️ For Voters
- **Secure Authentication:** Login, registration, and password recovery.
- **Dashboard:** Overview of active elections, voting statistics, and system announcements.
- **Election Browsing:** Search and filter through upcoming, active, and past elections.
- **Voting Process:** Multi-category voting with candidate selection and secure confirmation.
- **Voting History:** Track past votes and view published election results.
- **Profile Management:** Update personal details and profile picture.

### 🛠️ For Administrators
- **Admin Dashboard:** Real-time statistics on voter registration and election participation.
- **Election Management:** Full CRUD for elections, including category setup and publishing.
- **Candidate Management:** Register and assign candidates to specific election categories.
- **Voter Management:** Review and approve/reject new voter registrations.
- **Results & Analytics:** Visualize voting results using interactive Bar and Pie charts.
- **System Monitoring:** Audit logs to track administrative actions and system announcements.
- **Data Export:** Export election results to CSV and PDF formats.

## 🛠️ Tech Stack

- **Frontend Framework:** React 18 with Vite
- **Language:** TypeScript
- **Styling:** Tailwind CSS
- **Icons:** Lucide React
- **Charts:** Recharts
- **Forms:** React Hook Form + Yup Validation
- **API Client:** Axios with JWT Interceptors
- **Notifications:** React Toastify
- **Animations:** Framer Motion (via `motion/react`)
- **Date Handling:** date-fns

## 📂 Project Structure

```text
src/
├── api/            # Service-based API integration
├── components/     # Reusable UI components, layouts, and charts
├── context/        # Global state (Auth, Notifications)
├── hooks/          # Custom hooks for data fetching
├── pages/          # Feature-specific pages (Voter, Admin, Auth)
├── utils/          # Helper functions (Date, Token, Export)
└── types.ts        # Global TypeScript definitions
```

## ⚙️ Configuration

The application is configured to connect to a Spring Boot backend via a Vite proxy.

- **Development API:** `http://localhost:8080` (Proxied via `/api`)
- **Environment Variables:**
  - `VITE_API_BASE_URL`: Base URL for the backend API.

## 🚦 Getting Started

1. **Install Dependencies:**
   ```bash
   npm install
   ```

2. **Start Development Server:**
   ```bash
   npm run dev
   ```

3. **Build for Production:**
   ```bash
   npm run build
   ```

## 🔒 Security

- **RBAC:** Role-Based Access Control implemented via `ProtectedRoute` and `AdminRoute` guards.
- **JWT:** Secure token management with automatic injection in API requests.
- **Validation:** Strict client-side validation for all forms.
- **Sanitization:** Proper handling of user-generated content.

---
Built with ❤️ for secure and transparent democracy.
