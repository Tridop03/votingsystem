import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import ProtectedRoute from './components/guards/ProtectedRoute';
import AdminRoute from './components/guards/AdminRoute';

// Auth Pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import ForgotPassword from './pages/auth/ForgotPassword';
import ResetPassword from './pages/auth/ResetPassword';

// Voter Pages
import VoterDashboard from './pages/voter/VoterDashboard';
import ElectionList from './pages/voter/ElectionList';
import ElectionDetail from './pages/voter/ElectionDetail';
import VotingPage from './pages/voter/VotingPage';
import VoteConfirmation from './pages/voter/VoteConfirmation';
import VotingHistory from './pages/voter/VotingHistory';
import Results from './pages/voter/Results';
import VoterProfile from './pages/voter/VoterProfile';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import ManageElections from './pages/admin/elections/ManageElections';
import ElectionForm from './pages/admin/elections/ElectionForm';
import ManageCandidates from './pages/admin/candidates/ManageCandidates';
import AdminCandidateForm from './pages/admin/candidates/CandidateForm';
import ManageVoters from './pages/admin/voters/ManageVoters';
import VoterDetail from './pages/admin/voters/VoterDetail';
import ResultsPage from './pages/admin/results/ResultsPage';
import AuditLogs from './pages/admin/AuditLogs';
import Announcements from './pages/admin/Announcements';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <NotificationProvider>
        <Router>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* Voter Routes */}
            <Route path="/voter/dashboard" element={<ProtectedRoute><VoterDashboard /></ProtectedRoute>} />
            <Route path="/voter/elections" element={<ProtectedRoute><ElectionList /></ProtectedRoute>} />
            <Route path="/voter/elections/:id" element={<ProtectedRoute><ElectionDetail /></ProtectedRoute>} />
            <Route path="/voter/vote/:id" element={<ProtectedRoute><VotingPage /></ProtectedRoute>} />
            <Route path="/voter/vote/:id/confirm" element={<ProtectedRoute><VoteConfirmation /></ProtectedRoute>} />
            <Route path="/voter/history" element={<ProtectedRoute><VotingHistory /></ProtectedRoute>} />
            <Route path="/voter/results/:id" element={<ProtectedRoute><Results /></ProtectedRoute>} />
            <Route path="/voter/profile" element={<ProtectedRoute><VoterProfile /></ProtectedRoute>} />

            {/* Admin Routes */}
            <Route path="/admin/dashboard" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
            <Route path="/admin/elections" element={<AdminRoute><ManageElections /></AdminRoute>} />
            <Route path="/admin/elections/new" element={<AdminRoute><ElectionForm /></AdminRoute>} />
            <Route path="/admin/elections/:id/edit" element={<AdminRoute><ElectionForm /></AdminRoute>} />
            <Route path="/admin/candidates" element={<AdminRoute><ManageCandidates /></AdminRoute>} />
            <Route path="/admin/candidates/new" element={<AdminRoute><AdminCandidateForm /></AdminRoute>} />
            <Route path="/admin/candidates/:id/edit" element={<AdminRoute><AdminCandidateForm /></AdminRoute>} />
            <Route path="/admin/voters" element={<AdminRoute><ManageVoters /></AdminRoute>} />
            <Route path="/admin/voters/:id" element={<AdminRoute><VoterDetail /></AdminRoute>} />
            <Route path="/admin/results/:id" element={<AdminRoute><ResultsPage /></AdminRoute>} />
            <Route path="/admin/audit-logs" element={<AdminRoute><AuditLogs /></AdminRoute>} />
            <Route path="/admin/announcements" element={<AdminRoute><Announcements /></AdminRoute>} />

            {/* Root Redirect */}
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Router>
      </NotificationProvider>
    </AuthProvider>
  );
};

export default App;
