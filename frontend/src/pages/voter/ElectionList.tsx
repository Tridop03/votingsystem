import React, { useState } from 'react';
import { useElections } from '../../hooks/useElections';
import PageWrapper from '../../components/layout/PageWrapper';
import ElectionCard from '../../components/election/ElectionCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import SearchBar from '../../components/common/SearchBar';
import { Filter } from 'lucide-react';

const ElectionList: React.FC = () => {
  const [filter, setFilter] = useState<'all' | 'active' | 'upcoming' | 'past'>('all');
  const [search, setSearch] = useState('');
  const { elections, loading } = useElections(filter);

  const filteredElections = elections.filter(e => 
    e.title.toLowerCase().includes(search.toLowerCase()) ||
    e.description.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <PageWrapper
      title="Elections"
      subtitle="Browse and participate in available elections."
    >
      <div className="flex flex-col md:flex-row gap-4 mb-8">
        <SearchBar 
          value={search} 
          onChange={setSearch} 
          placeholder="Search elections..." 
          className="flex-1"
        />
        
        <div className="flex items-center gap-2 bg-white p-1 rounded-xl border border-gray-200">
          {[
            { id: 'all', label: 'All' },
            { id: 'active', label: 'Active' },
            { id: 'upcoming', label: 'Upcoming' },
            { id: 'past', label: 'Past' },
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setFilter(tab.id as any)}
              className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-all ${
                filter === tab.id
                  ? 'bg-indigo-600 text-white shadow-md'
                  : 'text-gray-500 hover:bg-gray-50'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : filteredElections.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredElections.map(election => (
            <ElectionCard key={election.id} election={election} />
          ))}
        </div>
      ) : (
        <div className="card py-20 text-center">
          <p className="text-gray-500">No elections found matching your criteria.</p>
        </div>
      )}
    </PageWrapper>
  );
};

export default ElectionList;
