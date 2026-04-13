import { useState, useEffect, useCallback } from 'react';
import { adminAPI } from '../api/adminAPI';

export const useVoters = (status: string = 'ALL') => {
  const [voters, setVoters] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchVoters = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let response;
      if (status === 'PENDING') {
        response = await adminAPI.getPendingVoters();
      } else {
        response = await adminAPI.getAllVoters();
        if (status !== 'ALL') {
          response.data = response.data.filter((v: any) => v.status === status);
        }
      }
      setVoters(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch voters');
    } finally {
      setLoading(false);
    }
  }, [status]);

  useEffect(() => {
    fetchVoters();
  }, [fetchVoters]);

  return { voters, loading, error, refetch: fetchVoters };
};
