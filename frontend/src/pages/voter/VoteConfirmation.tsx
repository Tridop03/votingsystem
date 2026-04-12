import React, { useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { CircleCheckBig as CheckCircle2, TriangleAlert as AlertTriangle, ArrowLeft, ShieldCheck } from 'lucide-react';
import { voteAPI } from '../../api/voteAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import Button from '../../components/common/Button';
import Avatar from '../../components/common/Avatar';
import { toast } from 'react-toastify';
import { motion, AnimatePresence } from 'motion/react';

const VoteConfirmation: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const location = useLocation();
  const navigate = useNavigate();
  const { selections, election } = location.state || {};
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  if (!selections || !election) {
    navigate(`/voter/elections/${id}`);
    return null;
  }

  const handleConfirm = async () => {
    setLoading(true);
    try {
      // Format votes for API
      const votes = Object.entries(selections).map(([categoryId, candidateId]) => ({
        election_id: parseInt(id!),
        election_category_id: parseInt(categoryId),
        candidate_id: candidateId
      }));

      await voteAPI.castVote({ votes });
      setSuccess(true);
      
      setTimeout(() => {
        navigate('/voter/dashboard');
      }, 3000);
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to cast vote');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageWrapper>
      <div className="max-w-2xl mx-auto">
        <AnimatePresence mode="wait">
          {!success ? (
            <motion.div
              key="review"
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 1.05 }}
              className="space-y-8"
            >
              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-indigo-100 text-indigo-600 rounded-2xl mb-4">
                  <ShieldCheck size={32} />
                </div>
                <h1 className="text-3xl font-bold text-gray-900">Review Your Vote</h1>
                <p className="text-gray-500 mt-2">Please confirm your selections before submitting.</p>
              </div>

              <div className="card space-y-6">
                <h3 className="font-bold text-gray-900 border-b pb-4">{election.title}</h3>
                
                <div className="space-y-4">
                  {election.categories.map((cat: any) => {
                    const candidateId = selections[cat.id];
                    const candidate = election.candidates.find((c: any) => c.id === candidateId);
                    
                    return (
                      <div key={cat.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                        <div>
                          <p className="text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                            {cat.category_name}
                          </p>
                          <p className="font-bold text-gray-900">{candidate?.full_name}</p>
                          <p className="text-xs text-gray-500">{candidate?.party}</p>
                        </div>
                        <Avatar src={candidate?.photo_url} name={candidate?.full_name} size="md" />
                      </div>
                    );
                  })}
                </div>

                <div className="p-4 bg-red-50 rounded-xl flex gap-3 border border-red-100">
                  <AlertTriangle className="text-red-600 shrink-0" size={20} />
                  <p className="text-xs text-red-800 leading-relaxed">
                    By clicking confirm, you agree that your selections are final. You will not be able to change your vote or vote again in this election.
                  </p>
                </div>

                <div className="flex gap-4 pt-4">
                  <Button 
                    variant="secondary" 
                    className="flex-1" 
                    onClick={() => navigate(-1)}
                    disabled={loading}
                  >
                    Go Back
                  </Button>
                  <Button 
                    className="flex-1" 
                    onClick={handleConfirm}
                    loading={loading}
                  >
                    Confirm Vote
                  </Button>
                </div>
              </div>
            </motion.div>
          ) : (
            <motion.div
              key="success"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="text-center py-20 space-y-6"
            >
              <div className="relative inline-block">
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ type: 'spring', damping: 12, stiffness: 200 }}
                  className="w-24 h-24 bg-green-500 text-white rounded-full flex items-center justify-center shadow-2xl shadow-green-200"
                >
                  <CheckCircle2 size={48} />
                </motion.div>
                <motion.div
                  animate={{ scale: [1, 1.5, 1], opacity: [0.5, 0, 0.5] }}
                  transition={{ repeat: Infinity, duration: 2 }}
                  className="absolute inset-0 bg-green-500 rounded-full -z-10"
                />
              </div>
              
              <div>
                <h2 className="text-3xl font-bold text-gray-900">Vote Cast Successfully!</h2>
                <p className="text-gray-500 mt-2">Your voice has been heard. Thank you for participating.</p>
              </div>

              <p className="text-sm text-gray-400 animate-pulse">
                Redirecting to dashboard in 3 seconds...
              </p>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </PageWrapper>
  );
};

export default VoteConfirmation;
