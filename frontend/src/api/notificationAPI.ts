import api from './axios';

export const notificationAPI = {
  getNotifications: () => api.get('/api/voter/notifications'),
  markAsRead: (id: number) => api.put(`/api/voter/notifications/${id}/read`),
};
