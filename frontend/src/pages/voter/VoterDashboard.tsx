import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Vote, 
  Calendar, 
  Clock, 
  Megaphone, 
  ArrowRight, 
  CircleCheckBig as CheckCircle2,
  CircleAlert as AlertCircle
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useElections } from '../../hooks/useElections';
import { adminAPI } from '../../api/adminAPI';
import { electionAPI } from '../../api/electionAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import StatsCard from '../../components/charts/StatsCard';
import ElectionCard from '../../components/election/ElectionCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const VoterDashboard: React.FC = () => {
  const { user } = useAuth();
  const { elections, loading: electionsLoading } = useElections('active');
  const [announcements, setAnnouncements] = useState<any[]>([]);
  const [stats, setStats] = useState({
    active: 0,
    voted: 0,
    upcoming: 0
  });

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [annResponse, allElectionsResponse] = await Promise.all([
          adminAPI.getAnnouncements(),
          electionAPI.getAllElections()
        ]);
        setAnnouncements(annResponse.data.slice(0, 3));
        
        const allElections = allElectionsResponse.data;
        setStats({
          active: allElections.filter((e: any) => e.status === 'ACTIVE').length,
          voted: 2, // Mock
          upcoming: allElections.filter((e: any) => e.status === 'UPCOMING').length
        });
      } catch (error) {
        console.error('Failed to fetch dashboard data', error);
      }
    };

    fetchDashboardData();
  }, []);

  return (
    <PageWrapper
      title={`Welcome back, ${user?.full_name?.split(' ')[0]}!`}
      subtitle="Here's what's happening in your voting community."
    >
      {/* Stats Row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <StatsCard 
          title="Active Elections" 
          value={stats.active} 
          icon={Vote} 
          color="indigo" 
        />
        <StatsCard 
          title="Elections Voted In" 
          value={stats.voted} 
          icon={CheckCircle2} 
          color="green" 
        />
        <StatsCard 
          title="Upcoming Elections" 
          value={stats.upcoming} 
          icon={Calendar} 
          color="blue" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content - Active Elections */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-gray-900">Active Elections</h2>
            <Link to="/voter/elections" className="text-sm font-bold text-indigo-600 hover:text-indigo-500 flex items-center gap-1">
              View All <ArrowRight size={16} />
            </Link>
          </div>

          {electionsLoading ? (
            <LoadingSpinner />
          ) : elections.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {elections.map(election => (
                <ElectionCard key={election.id} election={election} />
              ))}
            </div>
          ) : (
            <div className="card flex flex-col items-center justify-center py-12 text-center">
              <div className="w-16 h-16 bg-gray-100 text-gray-400 rounded-full flex items-center justify-center mb-4">
                <Vote size={32} />
              </div>
              <h3 className="text-lg font-bold text-gray-900">No Active Elections</h3>
              <p className="text-gray-500 max-w-xs mx-auto mt-1">
                There are currently no active elections. Check back later or view upcoming ones.
              </p>
              <Link to="/voter/elections" className="mt-6 btn btn-secondary">
                View Upcoming Elections
              </Link>
            </div>
          )}
        </div>

        {/* Sidebar Content */}
        <div className="space-y-8">
          {/* Announcements */}
          <section>
            <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
              <Megaphone size={20} className="text-indigo-600" />
              Announcements
            </h2>
            <div className="space-y-4">
              {announcements.length > 0 ? (
                announcements.map(ann => (
                  <div key={ann.id} className="card p-4 hover:border-indigo-100 transition-colors border border-transparent">
                    <h4 className="font-bold text-gray-900 mb-1">{ann.title}</h4>
                    <p className="text-sm text-gray-500 line-clamp-2 mb-2">{ann.message}</p>
                    <span className="text-[10px] font-bold text-indigo-600 uppercase tracking-wider">
                      {new Date(ann.created_at).toLocaleDateString()}
                    </span>
                  </div>
                ))
              ) : (
                <p className="text-sm text-gray-500 italic">No recent announcements.</p>
              )}
            </div>
          </section>

          {/* Voting Tips */}
          <section className="bg-indigo-600 rounded-2xl p-6 text-white shadow-xl shadow-indigo-200">
            <h3 className="font-bold text-lg mb-3 flex items-center gap-2">
              <AlertCircle size={20} />
              Voting Tips
            </h3>
            <ul className="space-y-3 text-sm text-indigo-100">
              <li className="flex gap-2">
                <span className="font-bold">•</span>
                Research candidates before casting your vote.
              </li>
              <li className="flex gap-2">
                <span className="font-bold">•</span>
                Ensure your internet connection is stable.
              </li>
              <li className="flex gap-2">
                <span className="font-bold">•</span>
                Votes cannot be changed once submitted.
              </li>
            </ul>
          </section>
        </div>
      </div>
    </PageWrapper>
  );
};

export default VoterDashboard;
