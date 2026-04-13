import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, ArrowRight, CircleCheckBig as CheckCircle2, CircleAlert as AlertCircle } from 'lucide-react';
import { electionAPI } from '../../api/electionAPI';
import { voteAPI } from '../../api/voteAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import CandidateCard from '../../components/candidate/CandidateCard';
import Button from '../../components/common/Button';
import { toast } from 'react-toastify';

const VotingPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [election, setElection] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [selections, setSelections] = useState<Record<number, number>>({});
  const [currentStep, setCurrentStep] = useState(0);

  useEffect(() => {
    const fetchElection = async () => {
      if (!id) return;
      try {
        const response = await electionAPI.getElectionById(id);
        const voteStatus = await voteAPI.checkVoteStatus(id);
        
        if (voteStatus.data.hasVoted) {
          toast.info('You have already voted in this election');
          navigate(`/voter/elections/${id}`);
          return;
        }
        
        setElection(response.data);
      } catch (error) {
        toast.error('Failed to load election');
        navigate('/voter/elections');
      } finally {
        setLoading(false);
      }
    };

    fetchElection();
  }, [id, navigate]);

  if (loading) return <LoadingSpinner fullPage />;
  if (!election) return null;

  const categories = election.categories || [];
  const currentCategory = categories[currentStep];
  const candidates = election.candidates?.filter(
    (c: any) => c.election_category_id === currentCategory?.id
  ) || [];

  const handleSelect = (candidateId: number) => {
    setSelections(prev => ({
      ...prev,
      [currentCategory.id]: candidateId
    }));
  };

  const handleNext = () => {
    if (!selections[currentCategory.id]) {
      toast.warning('Please select a candidate to continue');
      return;
    }
    if (currentStep < categories.length - 1) {
      setCurrentStep(prev => prev + 1);
      window.scrollTo(0, 0);
    } else {
      // Go to confirmation
      navigate(`/voter/vote/${id}/confirm`, { state: { selections, election } });
    }
  };

  const handleBack = () => {
    if (currentStep > 0) {
      setCurrentStep(prev => prev - 1);
    } else {
      navigate(`/voter/elections/${id}`);
    }
  };

  return (
    <PageWrapper>
      <div className="max-w-4xl mx-auto">
        {/* Progress Header */}
        <div className="mb-12">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-2xl font-bold text-gray-900">{election.title}</h1>
            <span className="text-sm font-bold text-indigo-600 bg-indigo-50 px-3 py-1 rounded-full">
              Category {currentStep + 1} of {categories.length}
            </span>
          </div>
          
          <div className="flex gap-2">
            {categories.map((_: any, idx: number) => (
              <div 
                key={idx}
                className={`h-2 flex-1 rounded-full transition-all duration-500 ${
                  idx <= currentStep ? 'bg-indigo-600' : 'bg-gray-200'
                }`}
              />
            ))}
          </div>
        </div>

        {/* Current Category Info */}
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-2">{currentCategory?.category_name}</h2>
          <p className="text-gray-500">Select one candidate from the list below to cast your vote.</p>
        </div>

        {/* Candidates Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
          {candidates.map((candidate: any) => (
            <CandidateCard
              key={candidate.id}
              candidate={candidate}
              isSelected={selections[currentCategory.id] === candidate.id}
              onSelect={() => handleSelect(candidate.id)}
              showVoteButton
            />
          ))}
        </div>

        {/* Navigation Actions */}
        <div className="flex items-center justify-between pt-8 border-t border-gray-100">
          <Button variant="secondary" onClick={handleBack} icon={<ArrowLeft size={18} />}>
            {currentStep === 0 ? 'Cancel' : 'Previous Category'}
          </Button>
          
          <Button 
            onClick={handleNext} 
            className="px-8"
            icon={currentStep === categories.length - 1 ? <CheckCircle2 size={18} /> : <ArrowRight size={18} />}
          >
            {currentStep === categories.length - 1 ? 'Review Vote' : 'Next Category'}
          </Button>
        </div>

        {/* Warning Footer */}
        <div className="mt-12 p-4 bg-yellow-50 rounded-2xl flex gap-3 border border-yellow-100">
          <AlertCircle className="text-yellow-600 shrink-0" size={20} />
          <p className="text-sm text-yellow-800">
            Your vote is anonymous and secure. Once you confirm your final selection on the next page, it cannot be changed.
          </p>
        </div>
      </div>
    </PageWrapper>
  );
};

export default VotingPage;
