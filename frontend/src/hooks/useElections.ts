import { useState, useEffect, useCallback } from 'react';
import { electionAPI } from '../api/electionAPI';
import { adminAPI } from '../api/adminAPI';
import { useAuth } from './useAuth';

export const useElections = (type: 'all' | 'active' | 'upcoming' | 'past' = 'all') => {
  const { isAdmin } = useAuth();
  const [elections, setElections] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchElections = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let response;
      if (isAdmin) {
        response = await adminAPI.getAllElections();
      } else {
        switch (type) {
          case 'active': response = await electionAPI.getActiveElections(); break;
          case 'upcoming': response = await electionAPI.getUpcomingElections(); break;
          case 'past': response = await electionAPI.getPastElections(); break;
          default: response = await electionAPI.getAllElections();
        }
      }
      setElections(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch elections');
    } finally {
      setLoading(false);
    }
  }, [isAdmin, type]);

  useEffect(() => {
    fetchElections();
  }, [fetchElections]);

  return { elections, loading, error, refetch: fetchElections };
};
