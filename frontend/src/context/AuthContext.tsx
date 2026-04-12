import React, { createContext, useState, useEffect, useCallback } from 'react';
import { authAPI } from '../api/authAPI';
import { voterAPI } from '../api/voterAPI';
import { saveToken, getToken, removeToken, decodeToken, isTokenExpired } from '../utils/tokenHelper';
import { toast } from 'react-toastify';

interface AuthContextType {
  user: any;
  token: string | null;
  loading: boolean;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (data: any) => Promise<void>;
  logout: () => void;
  register: (data: any) => Promise<void>;
  updateUser: (data: any) => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<any>(null);
  const [token, setToken] = useState<string | null>(getToken());
  const [loading, setLoading] = useState(true);

  const logout = useCallback(() => {
    removeToken();
    setToken(null);
    setUser(null);
    toast.info('Logged out successfully');
  }, []);

  const fetchUserProfile = useCallback(async () => {
    try {
      const response = await voterAPI.getProfile();
      setUser(response.data);
    } catch (error) {
      console.error('Failed to fetch profile', error);
      logout();
    } finally {
      setLoading(false);
    }
  }, [logout]);

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = getToken();
      if (storedToken) {
        if (isTokenExpired(storedToken)) {
          logout();
          setLoading(false);
        } else {
          setToken(storedToken);
          await fetchUserProfile();
        }
      } else {
        setLoading(false);
      }
    };

    initAuth();
  }, [fetchUserProfile, logout]);

  const login = async (data: any) => {
    try {
      const response = await authAPI.login(data);
      const { token: newToken, user: userData } = response.data;
      saveToken(newToken);
      setToken(newToken);
      setUser(userData);
      toast.success('Login successful!');
    } catch (error: any) {
      const message = error.response?.data?.message || 'Login failed';
      toast.error(message);
      throw error;
    }
  };

  const register = async (data: any) => {
    try {
      await authAPI.register(data);
      toast.success('Registration successful! Please wait for admin approval.');
    } catch (error: any) {
      const message = error.response?.data?.message || 'Registration failed';
      toast.error(message);
      throw error;
    }
  };

  const updateUser = (data: any) => {
    setUser((prev: any) => ({ ...prev, ...data }));
  };

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!token,
    isAdmin: user?.role === 'ADMIN',
    login,
    logout,
    register,
    updateUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
