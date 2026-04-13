import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useVoters } from '../../../hooks/useVoters';
import { adminAPI } from '../../../api/adminAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import Avatar from '../../../components/common/Avatar';
import SearchBar from '../../../components/common/SearchBar';
import Button from '../../../components/common/Button';
import ConfirmDialog from '../../../components/common/ConfirmDialog';
import { Eye, UserCheck, UserX, Key, Search } from 'lucide-react';
import { formatDate } from '../../../utils/dateFormatter';
import { toast } from 'react-toastify';

const ManageVoters: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const statusFilter = searchParams.get('status') || 'ALL';
  const { voters, loading, refetch } = useVoters(statusFilter);
  const [search, setSearch] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [confirmAction, setConfirmAction] = useState<{ id: number, type: 'APPROVE' | 'DEACTIVATE' | 'RESET' } | null>(null);

  const filteredVoters = voters.filter(v => 
    v.full_name.toLowerCase().includes(search.toLowerCase()) ||
    v.email.toLowerCase().includes(search.toLowerCase()) ||
    v.national_id.includes(search)
  );

  const handleAction = async () => {
    if (!confirmAction) return;
    setActionLoading(true);
    try {
      const { id, type } = confirmAction;
      if (type === 'APPROVE') await adminAPI.approveVoter(id);
      else if (type === 'DEACTIVATE') await adminAPI.deactivateVoter(id);
      else if (type === 'RESET') await adminAPI.resetVoterPassword(id);
      
      toast.success(`Action ${type.toLowerCase()} completed successfully`);
      refetch();
    } catch (error) {
      toast.error('Failed to perform action');
    } finally {
      setActionLoading(false);
      setConfirmAction(null);
    }
  };

  const columns = [
    {
      header: 'Voter',
      accessor: (item: any) => (
        <div className="flex items-center gap-3">
          <Avatar src={item.profile_picture} name={item.full_name} size="md" />
          <div>
            <p className="font-bold text-gray-900">{item.full_name}</p>
            <p className="text-xs text-gray-500">{item.email}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'National ID',
      accessor: 'national_id',
    },
    {
      header: 'Status',
      accessor: (item: any) => {
        const variants = {
          ACTIVE: 'success' as const,
          PENDING: 'warning' as const,
          DEACTIVATED: 'danger' as const,
        };
        return <Badge variant={variants[item.status as keyof typeof variants]}>{item.status}</Badge>;
      },
    },
    {
      header: 'Registered',
      accessor: (item: any) => formatDate(item.created_at),
    },
    {
      header: 'Actions',
      accessor: (item: any) => (
        <div className="flex items-center gap-2">
          <button 
            onClick={() => navigate(`/admin/voters/${item.id}`)}
            className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
            title="View Details"
          >
            <Eye size={18} />
          </button>
          {item.status === 'PENDING' && (
            <button 
              onClick={() => setConfirmAction({ id: item.id, type: 'APPROVE' })}
              className="p-2 text-gray-400 hover:text-green-600 hover:bg-green-50 rounded-lg transition-all"
              title="Approve"
            >
              <UserCheck size={18} />
            </button>
          )}
          {item.status === 'ACTIVE' && (
            <button 
              onClick={() => setConfirmAction({ id: item.id, type: 'DEACTIVATE' })}
              className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
              title="Deactivate"
            >
              <UserX size={18} />
            </button>
          )}
          <button 
            onClick={() => setConfirmAction({ id: item.id, type: 'RESET' })}
            className="p-2 text-gray-400 hover:text-yellow-600 hover:bg-yellow-50 rounded-lg transition-all"
            title="Reset Password"
          >
            <Key size={18} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <PageWrapper
      title="Manage Voters"
      subtitle="Review registrations and manage voter accounts."
    >
      <div className="flex flex-col md:flex-row gap-4 mb-8">
        <SearchBar 
          value={search} 
          onChange={setSearch} 
          placeholder="Search by name, email, or ID..." 
          className="flex-1"
        />
        
        <div className="flex items-center gap-2 bg-white p-1 rounded-xl border border-gray-200">
          {['ALL', 'PENDING', 'ACTIVE', 'DEACTIVATED'].map(status => (
            <button
              key={status}
              onClick={() => setSearchParams({ status })}
              className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-all ${
                statusFilter === status
                  ? 'bg-indigo-600 text-white shadow-md'
                  : 'text-gray-500 hover:bg-gray-50'
              }`}
            >
              {status}
            </button>
          ))}
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <Table 
          columns={columns} 
          data={filteredVoters} 
          loading={loading} 
          emptyMessage="No voters found."
        />
      </div>

      <ConfirmDialog
        isOpen={!!confirmAction}
        onClose={() => setConfirmAction(null)}
        onConfirm={handleAction}
        title={`${confirmAction?.type} Voter`}
        message={`Are you sure you want to ${confirmAction?.type.toLowerCase()} this voter?`}
        loading={actionLoading}
        variant={confirmAction?.type === 'DEACTIVATE' ? 'danger' : 'info'}
      />
    </PageWrapper>
  );
};

export default ManageVoters;
