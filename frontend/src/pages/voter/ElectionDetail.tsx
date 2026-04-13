import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { 
  Calendar, 
  Clock, 
  Users, 
  ArrowLeft, 
  Vote as VoteIcon,
  ChartBarBig as BarChart3,
  CircleCheckBig as CheckCircle2
} from 'lucide-react';
import { electionAPI } from '../../api/electionAPI';
import { voteAPI } from '../../api/voteAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ElectionStatusBadge from '../../components/election/ElectionStatusBadge';
import CandidateCard from '../../components/candidate/CandidateCard';
import Button from '../../components/common/Button';
import { formatDateTime, getElectionStatus } from '../../utils/dateFormatter';

const ElectionDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [election, setElection] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [hasVoted, setHasVoted] = useState(false);
  const [activeCategory, setActiveCategory] = useState<number | null>(null);

  useEffect(() => {
    const fetchElectionData = async () => {
      if (!id) return;
      try {
        const [electionRes, voteStatusRes] = await Promise.all([
          electionAPI.getElectionById(id),
          voteAPI.checkVoteStatus(id)
        ]);
        setElection(electionRes.data);
        setHasVoted(voteStatusRes.data.hasVoted);
        if (electionRes.data.categories?.length > 0) {
          setActiveCategory(electionRes.data.categories[0].id);
        }
      } catch (error) {
        console.error('Failed to fetch election details', error);
      } finally {
        setLoading(false);
      }
    };

    fetchElectionData();
  }, [id]);

  if (loading) return <LoadingSpinner fullPage />;
  if (!election) return <div className="p-8 text-center">Election not found.</div>;

  const status = getElectionStatus(election.start_time, election.end_time);
  const filteredCandidates = election.candidates?.filter(
    (c: any) => c.election_category_id === activeCategory
  ) || [];

  return (
    <PageWrapper>
      <Link to="/voter/elections" className="inline-flex items-center gap-2 text-sm font-bold text-gray-500 hover:text-indigo-600 mb-6 transition-colors">
        <ArrowLeft size={16} />
        Back to Elections
      </Link>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column - Info */}
        <div className="lg:col-span-2 space-y-8">
          <div className="card">
            <div className="flex justify-between items-start mb-6">
              <h1 className="text-3xl font-bold text-gray-900">{election.title}</h1>
              <ElectionStatusBadge start={election.start_time} end={election.end_time} />
            </div>
            
            <p className="text-gray-600 leading-relaxed mb-8">
              {election.description}
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 pt-6 border-t border-gray-100">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl">
                  <Calendar size={20} />
                </div>
                <div>
                  <p className="text-xs text-gray-500 font-medium uppercase tracking-wider">Start Date</p>
                  <p className="font-bold text-gray-900">{formatDateTime(election.start_time)}</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl">
                  <Clock size={20} />
                </div>
                <div>
                  <p className="text-xs text-gray-500 font-medium uppercase tracking-wider">End Date</p>
                  <p className="font-bold text-gray-900">{formatDateTime(election.end_time)}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Categories & Candidates */}
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-bold text-gray-900">Candidates</h2>
              <div className="flex gap-2">
                {election.categories?.map((cat: any) => (
                  <button
                    key={cat.id}
                    onClick={() => setActiveCategory(cat.id)}
                    className={`px-4 py-1.5 rounded-xl text-sm font-medium transition-all ${
                      activeCategory === cat.id
                        ? 'bg-indigo-600 text-white shadow-md'
                        : 'bg-white text-gray-500 hover:bg-gray-50 border border-gray-200'
                    }`}
                  >
                    {cat.category_name}
                  </button>
                ))}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {filteredCandidates.map((candidate: any) => (
                <CandidateCard key={candidate.id} candidate={candidate} />
              ))}
            </div>
          </div>
        </div>

        {/* Right Column - Actions */}
        <div className="space-y-6">
          <div className="card sticky top-24">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Participation</h3>
            
            {hasVoted ? (
              <div className="bg-green-50 border border-green-100 rounded-2xl p-6 text-center">
                <div className="w-12 h-12 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                  <CheckCircle2 size={24} />
                </div>
                <h4 className="font-bold text-green-900">Vote Cast Successfully</h4>
                <p className="text-sm text-green-700 mt-1">
                  You have already participated in this election. Thank you for voting!
                </p>
                <Link to="/voter/history" className="mt-6 btn btn-secondary w-full">
                  View Voting History
                </Link>
              </div>
            ) : status === 'ACTIVE' ? (
              <div className="space-y-4">
                <div className="p-4 bg-indigo-50 rounded-2xl">
                  <p className="text-sm text-indigo-900 leading-relaxed">
                    This election is currently <strong>Active</strong>. You can cast your vote for each category.
                  </p>
                </div>
                <Button 
                  className="w-full py-4 text-lg" 
                  icon={<VoteIcon size={20} />}
                  onClick={() => navigate(`/voter/vote/${id}`)}
                >
                  Proceed to Vote
                </Button>
              </div>
            ) : status === 'UPCOMING' ? (
              <div className="bg-blue-50 border border-blue-100 rounded-2xl p-6 text-center">
                <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Clock size={24} />
                </div>
                <h4 className="font-bold text-blue-900">Election Upcoming</h4>
                <p className="text-sm text-blue-700 mt-1">
                  Voting will begin on {formatDateTime(election.start_time)}.
                </p>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="bg-gray-50 border border-gray-100 rounded-2xl p-6 text-center">
                  <h4 className="font-bold text-gray-900">Election Ended</h4>
                  <p className="text-sm text-gray-500 mt-1">
                    Voting for this election is now closed.
                  </p>
                </div>
                {!election.results_locked && (
                  <Button 
                    variant="outline" 
                    className="w-full" 
                    icon={<BarChart3 size={18} />}
                    onClick={() => navigate(`/voter/results/${id}`)}
                  >
                    View Results
                  </Button>
                )}
              </div>
            )}

            <div className="mt-8 pt-6 border-t border-gray-100">
              <h4 className="text-sm font-bold text-gray-900 mb-3">Quick Stats</h4>
              <div className="space-y-3">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Total Categories</span>
                  <span className="font-bold text-gray-900">{election.categories?.length || 0}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Total Candidates</span>
                  <span className="font-bold text-gray-900">{election.candidates?.length || 0}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </PageWrapper>
  );
};

export default ElectionDetail;
