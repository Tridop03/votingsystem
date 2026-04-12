import React, { useState, useEffect } from 'react';
import { voterAPI } from '../../api/voterAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import { formatDateTime } from '../../utils/dateFormatter';
import { Vote, Calendar, User } from 'lucide-react';

const VotingHistory: React.FC = () => {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await voterAPI.getVotingHistory();
        setHistory(response.data);
      } catch (error) {
        console.error('Failed to fetch voting history', error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, []);

  const columns = [
    {
      header: 'Election',
      accessor: (item: any) => (
        <div className="flex items-center gap-3">
          <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
            <Vote size={18} />
          </div>
          <span className="font-bold text-gray-900">{item.election_title}</span>
        </div>
      ),
    },
    {
      header: 'Category',
      accessor: 'category_name',
    },
    {
      header: 'Candidate Voted',
      accessor: (item: any) => (
        <div className="flex items-center gap-2">
          <User size={16} className="text-gray-400" />
          <span>{item.candidate_name}</span>
        </div>
      ),
    },
    {
      header: 'Date & Time',
      accessor: (item: any) => (
        <div className="flex items-center gap-2 text-gray-500">
          <Calendar size={16} />
          <span>{formatDateTime(item.voted_at)}</span>
        </div>
      ),
    },
    {
      header: 'Status',
      accessor: () => <Badge variant="success">Verified</Badge>,
    },
  ];

  return (
    <PageWrapper
      title="Voting History"
      subtitle="A complete record of your participation in past elections."
    >
      <div className="card overflow-hidden">
        <Table 
          columns={columns} 
          data={history} 
          loading={loading} 
          emptyMessage="You haven't cast any votes yet."
        />
      </div>

      <div className="mt-8 p-6 bg-indigo-50 rounded-2xl border border-indigo-100">
        <h4 className="font-bold text-indigo-900 mb-2">Security Note</h4>
        <p className="text-sm text-indigo-700 leading-relaxed">
          Your voting history shows which elections you participated in and when. For security and anonymity, your specific candidate choices are encrypted and stored separately from your identity in the main ledger.
        </p>
      </div>
    </PageWrapper>
  );
};

export default VotingHistory;
