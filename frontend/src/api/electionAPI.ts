import api from './axios';

export const electionAPI = {
  getAllElections: () => api.get('/api/elections'),
  getActiveElections: () => api.get('/api/elections/active'),
  getUpcomingElections: () => api.get('/api/elections/upcoming'),
  getPastElections: () => api.get('/api/elections/past'),
  getElectionById: (id: string | number) => api.get(`/api/elections/${id}`),
};
