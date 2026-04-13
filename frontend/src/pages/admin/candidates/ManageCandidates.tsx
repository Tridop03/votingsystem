import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCandidates } from '../../../hooks/useCandidates';
import { adminAPI } from '../../../api/adminAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import Table from '../../../components/common/Table';
import Avatar from '../../../components/common/Avatar';
import Button from '../../../components/common/Button';
import SearchBar from '../../../components/common/SearchBar';
import ConfirmDialog from '../../../components/common/ConfirmDialog';
import { Plus, Edit2, Trash2, SquareUser } from 'lucide-react';
import { toast } from 'react-toastify';

const ManageCandidates: React.FC = () => {
  const navigate = useNavigate();
  const { candidates, loading, refetch } = useCandidates();
  const [search, setSearch] = useState('');
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  const filteredCandidates = candidates.filter(c => 
    c.full_name.toLowerCase().includes(search.toLowerCase()) ||
    c.party.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async () => {
    if (!deleteId) return;
    setActionLoading(true);
    try {
      await adminAPI.deleteCandidate(deleteId);
      toast.success('Candidate removed successfully');
      refetch();
    } catch (error) {
      toast.error('Failed to remove candidate');
    } finally {
      setActionLoading(false);
      setDeleteId(null);
    }
  };

  const columns = [
    {
      header: 'Candidate',
      accessor: (item: any) => (
        <div className="flex items-center gap-3">
          <Avatar src={item.photo_url} name={item.full_name} size="md" />
          <div>
            <p className="font-bold text-gray-900">{item.full_name}</p>
            <p className="text-xs text-gray-500">{item.party}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Election',
      accessor: 'election_title',
    },
    {
      header: 'Category',
      accessor: 'category_name',
    },
    {
      header: 'Actions',
      accessor: (item: any) => (
        <div className="flex items-center gap-2">
          <button 
            onClick={() => navigate(`/admin/candidates/${item.id}/edit`)}
            className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
          >
            <Edit2 size={18} />
          </button>
          <button 
            onClick={() => setDeleteId(item.id)}
            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <PageWrapper
      title="Manage Candidates"
      subtitle="Add and manage candidates for various election categories."
      actions={
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => navigate('/admin/candidates/new')}
        >
          Add Candidate
        </Button>
      }
    >
      <div className="mb-6">
        <SearchBar 
          value={search} 
          onChange={setSearch} 
          placeholder="Search candidates by name or party..." 
          className="max-w-md"
        />
      </div>

      <div className="card p-0 overflow-hidden">
        <Table 
          columns={columns} 
          data={filteredCandidates} 
          loading={loading} 
          emptyMessage="No candidates found."
        />
      </div>

      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Remove Candidate"
        message="Are you sure you want to remove this candidate? This will also remove any votes cast for them. This action cannot be undone."
        loading={actionLoading}
      />
    </PageWrapper>
  );
};

export default ManageCandidates;
