import api from './axios';

export const voteAPI = {
  castVote: (data: any) => api.post('/api/votes/cast', data),
  checkVoteStatus: (electionId: string | number) => api.get(`/api/votes/status/${electionId}`),
};
