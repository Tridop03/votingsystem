import api from './axios';

export const adminAPI = {
  // Voters
  getAllVoters: () => api.get('/api/admin/voters'),
  getPendingVoters: () => api.get('/api/admin/voters/pending'),
  getVoterById: (id: string | number) => api.get(`/api/admin/voters/${id}`),
  getVoterActivity: (id: string | number) => api.get(`/api/admin/voters/${id}/activity`),
  approveVoter: (id: string | number) => api.put(`/api/admin/voters/${id}/approve`),
  deactivateVoter: (id: string | number) => api.put(`/api/admin/voters/${id}/deactivate`),
  resetVoterPassword: (id: string | number) => api.put(`/api/admin/voters/${id}/reset-password`),

  // Elections
  getAllElections: () => api.get('/api/admin/elections'),
  createElection: (data: any) => api.post('/api/admin/elections', data),
  updateElection: (id: string | number, data: any) => api.put(`/api/admin/elections/${id}`, data),
  deleteElection: (id: string | number) => api.delete(`/api/admin/elections/${id}`),
  publishElection: (id: string | number) => api.put(`/api/admin/elections/${id}/publish`),
  lockResults: (id: string | number) => api.put(`/api/admin/elections/${id}/lock-results`),

  // Candidates
  getAllCandidates: () => api.get('/api/admin/candidates'),
  createCandidate: (data: any) => api.post('/api/admin/candidates', data),
  updateCandidate: (id: string | number, data: any) => api.put(`/api/admin/candidates/${id}`, data),
  deleteCandidate: (id: string | number) => api.delete(`/api/admin/candidates/${id}`),

  // System
  getAuditLogs: () => api.get('/api/admin/audit-logs'),
  getAnnouncements: () => api.get('/api/admin/announcements'),
  createAnnouncement: (data: any) => api.post('/api/admin/announcements', data),
  updateAnnouncement: (id: string | number, data: any) => api.put(`/api/admin/announcements/${id}`, data),
  deleteAnnouncement: (id: string | number) => api.delete(`/api/admin/announcements/${id}`),
};
