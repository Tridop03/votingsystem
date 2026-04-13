import api from './axios';

export const voterAPI = {
  getProfile: () => api.get('/api/voter/profile'),
  updateProfile: (data: any) => api.put('/api/voter/profile', data),
  uploadPhoto: (file: File) => {
    const formData = new FormData();
    formData.append('photo', file);
    return api.put('/api/voter/profile/photo', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  deleteAccount: () => api.delete('/api/voter/account'),
  getVotingHistory: () => api.get('/api/voter/history'),
  getNotifications: () => api.get('/api/voter/notifications'),
  markNotificationRead: (id: number) => api.put(`/api/voter/notifications/${id}/read`),
};
