import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { adminAPI } from '../../../api/adminAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import CandidateForm from '../../../components/candidate/CandidateForm';
import { ArrowLeft } from 'lucide-react';
import Button from '../../../components/common/Button';
import { toast } from 'react-toastify';

const AdminCandidateForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<any[]>([]);
  const [initialData, setInitialData] = useState<any>(null);
  const [fetching, setFetching] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const electionsRes = await adminAPI.getAllElections();
        // Flatten categories from all elections
        const allCategories = electionsRes.data.flatMap((e: any) => 
          e.categories.map((c: any) => ({ ...c, election_title: e.title }))
        );
        setCategories(allCategories);

        if (id) {
          const candidatesRes = await adminAPI.getAllCandidates();
          const candidate = candidatesRes.data.find((c: any) => c.id === parseInt(id));
          if (candidate) {
            setInitialData(candidate);
          } else {
            toast.error('Candidate not found');
            navigate('/admin/candidates');
          }
        }
      } catch (error) {
        toast.error('Failed to load data');
      } finally {
        setFetching(false);
      }
    };

    fetchData();
  }, [id, navigate]);

  const handleSubmit = async (data: any) => {
    setLoading(true);
    try {
      if (id) {
        await adminAPI.updateCandidate(id, data);
        toast.success('Candidate updated successfully');
      } else {
        await adminAPI.createCandidate(data);
        toast.success('Candidate created successfully');
      }
      navigate('/admin/candidates');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to save candidate');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageWrapper
      title={id ? 'Edit Candidate' : 'Add Candidate'}
      subtitle={id ? 'Update candidate profile and assignment.' : 'Register a new candidate for an election.'}
    >
      <Button 
        variant="secondary" 
        size="sm" 
        icon={<ArrowLeft size={16} />} 
        onClick={() => navigate('/admin/candidates')}
        className="mb-6"
      >
        Back to List
      </Button>

      <div className="max-w-2xl">
        <div className="card">
          {!fetching && (
            <CandidateForm 
              initialData={initialData} 
              onSubmit={handleSubmit} 
              loading={loading}
              categories={categories}
            />
          )}
        </div>
      </div>
    </PageWrapper>
  );
};

export default AdminCandidateForm;
