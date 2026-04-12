export const isAdmin = (user: any) => {
  return user?.role === 'ADMIN';
};

export const isVoter = (user: any) => {
  return user?.role === 'VOTER';
};

export const getRedirectPath = (role: string) => {
  return role === 'ADMIN' ? '/admin/dashboard' : '/voter/dashboard';
};
