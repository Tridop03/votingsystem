import React, { useState, useEffect } from 'react';
import { 
  Users, 
  Vote, 
  UserCheck, 
  Activity, 
  ArrowRight,
  ShieldCheck,
  TrendingUp
} from 'lucide-react';
import { adminAPI } from '../../api/adminAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import StatsCard from '../../components/charts/StatsCard';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Avatar from '../../components/common/Avatar';
import { formatDateTime } from '../../utils/dateFormatter';
import { Link } from 'react-router-dom';

const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState({
    totalVoters: 0,
    pendingVoters: 0,
    activeElections: 0,
    totalVotes: 0
  });
  const [pendingVoters, setPendingVoters] = useState<any[]>([]);
  const [auditLogs, setAuditLogs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [votersRes, pendingRes, electionsRes, logsRes] = await Promise.all([
          adminAPI.getAllVoters(),
          adminAPI.getPendingVoters(),
          adminAPI.getAllElections(),
          adminAPI.getAuditLogs()
        ]);

        setStats({
          totalVoters: votersRes.data.length,
          pendingVoters: pendingRes.data.length,
          activeElections: electionsRes.data.filter((e: any) => e.is_active).length,
          totalVotes: 1250 // Mock
        });

        setPendingVoters(pendingRes.data.slice(0, 5));
        setAuditLogs(logsRes.data.slice(0, 5));
      } catch (error) {
        console.error('Failed to fetch admin dashboard data', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const logColumns = [
    {
      header: 'User',
      accessor: (item: any) => (
        <div className="flex items-center gap-2">
          <Avatar name={item.user_name} size="sm" />
          <span className="font-medium">{item.user_name}</span>
        </div>
      ),
    },
    {
      header: 'Action',
      accessor: (item: any) => (
        <span className="font-mono text-xs font-bold text-indigo-600 bg-indigo-50 px-2 py-1 rounded">
          {item.action}
        </span>
      ),
    },
    {
      header: 'Timestamp',
      accessor: (item: any) => formatDateTime(item.timestamp),
    },
  ];

  return (
    <PageWrapper
      title="Admin Dashboard"
      subtitle="Overview of system activity and management."
    >
      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatsCard 
          title="Total Voters" 
          value={stats.totalVoters} 
          icon={Users} 
          color="indigo" 
          trend={{ value: 12, isUp: true }}
        />
        <StatsCard 
          title="Pending Approvals" 
          value={stats.pendingVoters} 
          icon={UserCheck} 
          color="yellow" 
        />
        <StatsCard 
          title="Active Elections" 
          value={stats.activeElections} 
          icon={Vote} 
          color="green" 
        />
        <StatsCard 
          title="Total Votes Cast" 
          value={stats.totalVotes} 
          icon={TrendingUp} 
          color="blue" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Pending Approvals */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-gray-900">Pending Approvals</h2>
            <Link to="/admin/voters?status=PENDING" className="text-sm font-bold text-indigo-600 hover:text-indigo-500 flex items-center gap-1">
              View All <ArrowRight size={16} />
            </Link>
          </div>
          
          <div className="card p-0 overflow-hidden">
            {pendingVoters.length > 0 ? (
              <div className="divide-y divide-gray-50">
                {pendingVoters.map(voter => (
                  <div key={voter.id} className="p-4 flex items-center justify-between hover:bg-gray-50 transition-colors">
                    <div className="flex items-center gap-3">
                      <Avatar name={voter.full_name} size="md" />
                      <div>
                        <p className="font-bold text-gray-900">{voter.full_name}</p>
                        <p className="text-xs text-gray-500">{voter.email}</p>
                      </div>
                    </div>
                    <Link to={`/admin/voters/${voter.id}`} className="p-2 text-indigo-600 hover:bg-indigo-50 rounded-xl transition-all">
                      <ArrowRight size={20} />
                    </Link>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-12 text-center text-gray-500">
                No pending approvals.
              </div>
            )}
          </div>
        </div>

        {/* Recent Audit Logs */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-gray-900">Recent Activity</h2>
            <Link to="/admin/audit-logs" className="text-sm font-bold text-indigo-600 hover:text-indigo-500 flex items-center gap-1">
              View All <ArrowRight size={16} />
            </Link>
          </div>
          
          <div className="card p-0 overflow-hidden">
            <Table 
              columns={logColumns} 
              data={auditLogs} 
              loading={loading} 
              emptyMessage="No recent activity logs."
            />
          </div>
        </div>
      </div>
    </PageWrapper>
  );
};

export default AdminDashboard;
