import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useElections } from '../../../hooks/useElections';
import { adminAPI } from '../../../api/adminAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import Button from '../../../components/common/Button';
import SearchBar from '../../../components/common/SearchBar';
import ConfirmDialog from '../../../components/common/ConfirmDialog';
import { Plus, Edit2, Trash2, Globe, Lock, ChartBarBig as BarChart3 } from 'lucide-react';
import { formatDateTime, getElectionStatus } from '../../../utils/dateFormatter';
import { toast } from 'react-toastify';

const ManageElections: React.FC = () => {
  const navigate = useNavigate();
  const { elections, loading, refetch } = useElections('all');
  const [search, setSearch] = useState('');
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  const filteredElections = elections.filter(e => 
    e.title.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async () => {
    if (!deleteId) return;
    setActionLoading(true);
    try {
      await adminAPI.deleteElection(deleteId);
      toast.success('Election deleted successfully');
      refetch();
    } catch (error) {
      toast.error('Failed to delete election');
    } finally {
      setActionLoading(false);
      setDeleteId(null);
    }
  };

  const handlePublish = async (id: number) => {
    try {
      await adminAPI.publishElection(id);
      toast.success('Election published successfully');
      refetch();
    } catch (error) {
      toast.error('Failed to publish election');
    }
  };

  const handleLockResults = async (id: number) => {
    try {
      await adminAPI.lockResults(id);
      toast.success('Results locked successfully');
      refetch();
    } catch (error) {
      toast.error('Failed to lock results');
    }
  };

  const columns = [
    {
      header: 'Title',
      accessor: (item: any) => (
        <div>
          <p className="font-bold text-gray-900">{item.title}</p>
          <p className="text-xs text-gray-500 line-clamp-1">{item.description}</p>
        </div>
      ),
    },
    {
      header: 'Duration',
      accessor: (item: any) => (
        <div className="text-xs space-y-1">
          <p><span className="text-gray-400">From:</span> {formatDateTime(item.start_time)}</p>
          <p><span className="text-gray-400">To:</span> {formatDateTime(item.end_time)}</p>
        </div>
      ),
    },
    {
      header: 'Status',
      accessor: (item: any) => {
        const status = getElectionStatus(item.start_time, item.end_time);
        const variants = {
          ACTIVE: 'success' as const,
          UPCOMING: 'info' as const,
          PAST: 'neutral' as const,
        };
        return <Badge variant={variants[status]}>{status}</Badge>;
      },
    },
    {
      header: 'Visibility',
      accessor: (item: any) => (
        <Badge variant={item.is_active ? 'success' : 'warning'}>
          {item.is_active ? 'Published' : 'Draft'}
        </Badge>
      ),
    },
    {
      header: 'Actions',
      accessor: (item: any) => (
        <div className="flex items-center gap-2">
          <button 
            onClick={() => navigate(`/admin/elections/${item.id}/edit`)}
            className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
            title="Edit"
          >
            <Edit2 size={18} />
          </button>
          {!item.is_active && (
            <button 
              onClick={() => handlePublish(item.id)}
              className="p-2 text-gray-400 hover:text-green-600 hover:bg-green-50 rounded-lg transition-all"
              title="Publish"
            >
              <Globe size={18} />
            </button>
          )}
          <button 
            onClick={() => navigate(`/admin/results/${item.id}`)}
            className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all"
            title="Results"
          >
            <BarChart3 size={18} />
          </button>
          <button 
            onClick={() => setDeleteId(item.id)}
            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
            title="Delete"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <PageWrapper
      title="Manage Elections"
      subtitle="Create, edit, and monitor all voting events."
      actions={
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => navigate('/admin/elections/new')}
        >
          New Election
        </Button>
      }
    >
      <div className="mb-6">
        <SearchBar 
          value={search} 
          onChange={setSearch} 
          placeholder="Search elections by title..." 
          className="max-w-md"
        />
      </div>

      <div className="card p-0 overflow-hidden">
        <Table 
          columns={columns} 
          data={filteredElections} 
          loading={loading} 
          emptyMessage="No elections found."
        />
      </div>

      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Election"
        message="Are you sure you want to delete this election? This will also remove all associated candidates and votes. This action cannot be undone."
        loading={actionLoading}
      />
    </PageWrapper>
  );
};

export default ManageElections;
