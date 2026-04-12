import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { adminAPI } from '../../../api/adminAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Avatar from '../../../components/common/Avatar';
import Badge from '../../../components/common/Badge';
import Table from '../../../components/common/Table';
import { ArrowLeft, Mail, Phone, MapPin, CreditCard, Activity, ShieldCheck } from 'lucide-react';
import { formatDateTime } from '../../../utils/dateFormatter';

const VoterDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [voter, setVoter] = useState<any>(null);
  const [activity, setActivity] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchVoterData = async () => {
      if (!id) return;
      try {
        const [voterRes, activityRes] = await Promise.all([
          adminAPI.getVoterById(id),
          adminAPI.getVoterActivity(id)
        ]);
        setVoter(voterRes.data);
        setActivity(activityRes.data);
      } catch (error) {
        console.error('Failed to fetch voter details', error);
      } finally {
        setLoading(false);
      }
    };

    fetchVoterData();
  }, [id]);

  if (loading) return <LoadingSpinner fullPage />;
  if (!voter) return <div className="p-8 text-center">Voter not found.</div>;

  const activityColumns = [
    {
      header: 'Action',
      accessor: (item: any) => (
        <span className="font-mono text-xs font-bold text-indigo-600 bg-indigo-50 px-2 py-1 rounded">
          {item.action}
        </span>
      ),
    },
    {
      header: 'Details',
      accessor: 'details',
    },
    {
      header: 'Timestamp',
      accessor: (item: any) => formatDateTime(item.timestamp),
    },
  ];

  return (
    <PageWrapper>
      <Link to="/admin/voters" className="inline-flex items-center gap-2 text-sm font-bold text-gray-500 hover:text-indigo-600 mb-6 transition-colors">
        <ArrowLeft size={16} />
        Back to Voters
      </Link>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Profile Card */}
        <div className="lg:col-span-1 space-y-6">
          <div className="card text-center">
            <Avatar src={voter.profile_picture} name={voter.full_name} size="xl" className="mb-4" />
            <h2 className="text-2xl font-bold text-gray-900">{voter.full_name}</h2>
            <p className="text-gray-500 mb-6">{voter.email}</p>
            
            <div className="flex justify-center gap-2 mb-8">
              <Badge variant={voter.status === 'ACTIVE' ? 'success' : voter.status === 'PENDING' ? 'warning' : 'danger'}>
                {voter.status}
              </Badge>
              {voter.email_verified && <Badge variant="info">Verified</Badge>}
            </div>

            <div className="space-y-4 text-left pt-6 border-t border-gray-100">
              <div className="flex items-center gap-3 text-sm">
                <Mail size={16} className="text-gray-400" />
                <span className="text-gray-600">{voter.email}</span>
              </div>
              <div className="flex items-center gap-3 text-sm">
                <Phone size={16} className="text-gray-400" />
                <span className="text-gray-600">{voter.phone}</span>
              </div>
              <div className="flex items-center gap-3 text-sm">
                <MapPin size={16} className="text-gray-400" />
                <span className="text-gray-600">{voter.address}</span>
              </div>
              <div className="flex items-center gap-3 text-sm">
                <CreditCard size={16} className="text-gray-400" />
                <span className="text-gray-600">ID: {voter.national_id}</span>
              </div>
            </div>
          </div>

          <div className="card bg-gray-900 text-white">
            <h4 className="font-bold mb-4 flex items-center gap-2">
              <ShieldCheck size={20} className="text-indigo-400" />
              Account Info
            </h4>
            <div className="space-y-3 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-400">Registered on</span>
                <span>{formatDateTime(voter.created_at)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">User ID</span>
                <span className="font-mono text-xs">#{voter.id}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Activity Log */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center gap-2 mb-2">
            <Activity size={20} className="text-indigo-600" />
            <h3 className="text-xl font-bold text-gray-900">Voter Activity Log</h3>
          </div>
          <div className="card p-0 overflow-hidden">
            <Table 
              columns={activityColumns} 
              data={activity} 
              loading={loading} 
              emptyMessage="No activity recorded for this voter."
            />
          </div>
        </div>
      </div>
    </PageWrapper>
  );
};

export default VoterDetail;
