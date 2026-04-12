import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../api/adminAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import Table from '../../components/common/Table';
import Avatar from '../../components/common/Avatar';
import Button from '../../components/common/Button';
import SearchBar from '../../components/common/SearchBar';
import { formatDateTime } from '../../utils/dateFormatter';
import { ShieldCheck, Filter } from 'lucide-react';

const AuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const response = await adminAPI.getAuditLogs();
        setLogs(response.data);
      } catch (error) {
        console.error('Failed to fetch audit logs', error);
      } finally {
        setLoading(false);
      }
    };

    fetchLogs();
  }, []);

  const filteredLogs = logs.filter(log => 
    log.user_name.toLowerCase().includes(search.toLowerCase()) ||
    log.action.toLowerCase().includes(search.toLowerCase()) ||
    log.details.toLowerCase().includes(search.toLowerCase())
  );

  const columns = [
    {
      header: 'User',
      accessor: (item: any) => (
        <div className="flex items-center gap-3">
          <Avatar name={item.user_name} size="sm" />
          <div>
            <p className="font-bold text-gray-900">{item.user_name}</p>
            <p className="text-[10px] text-gray-400 font-mono">ID: {item.user_id}</p>
          </div>
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
      header: 'Details',
      accessor: (item: any) => (
        <p className="text-sm text-gray-600 max-w-xs truncate" title={item.details}>
          {item.details}
        </p>
      ),
    },
    {
      header: 'Timestamp',
      accessor: (item: any) => (
        <div className="text-xs text-gray-500">
          {formatDateTime(item.timestamp)}
        </div>
      ),
    },
  ];

  return (
    <PageWrapper
      title="Audit Logs"
      subtitle="Track all administrative actions and system events."
    >
      <div className="mb-6 flex gap-4">
        <SearchBar 
          value={search} 
          onChange={setSearch} 
          placeholder="Search logs by user, action, or details..." 
          className="max-w-md flex-1"
        />
        <Button variant="outline" icon={<Filter size={18} />}>
          Filters
        </Button>
      </div>

      <div className="card p-0 overflow-hidden">
        <Table 
          columns={columns} 
          data={filteredLogs} 
          loading={loading} 
          emptyMessage="No audit logs found."
        />
      </div>
    </PageWrapper>
  );
};

export default AuditLogs;
