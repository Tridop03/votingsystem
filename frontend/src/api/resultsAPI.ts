import api from './axios';

export const resultsAPI = {
  getResults: (electionId: string | number) => api.get(`/api/admin/results/${electionId}`),
  exportPDF: (electionId: string | number) => api.get(`/api/admin/results/${electionId}/export/pdf`, { responseType: 'blob' }),
  exportCSV: (electionId: string | number) => api.get(`/api/admin/results/${electionId}/export/csv`),
};
