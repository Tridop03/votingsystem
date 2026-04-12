import React from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Users, ArrowRight } from 'lucide-react';
import { formatDate } from '../../utils/dateFormatter';
import ElectionStatusBadge from './ElectionStatusBadge';
import ElectionTimer from './ElectionTimer';

interface ElectionCardProps {
  election: any;
}

const ElectionCard: React.FC<ElectionCardProps> = ({ election }) => {
  return (
    <div className="card group hover:shadow-xl transition-all duration-300 border border-transparent hover:border-indigo-100">
      <div className="flex justify-between items-start mb-4">
        <ElectionStatusBadge start={election.start_time} end={election.end_time} />
        <ElectionTimer endDate={election.end_time} />
      </div>
      
      <h3 className="text-xl font-bold text-gray-900 mb-2 group-hover:text-indigo-600 transition-colors">
        {election.title}
      </h3>
      <p className="text-gray-500 text-sm line-clamp-2 mb-6">
        {election.description}
      </p>
      
      <div className="space-y-3 mb-6">
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <Calendar size={16} className="text-indigo-500" />
          <span>Starts: {formatDate(election.start_time)}</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <Users size={16} className="text-indigo-500" />
          <span>{election.categories?.length || 0} Categories</span>
        </div>
      </div>
      
      <Link 
        to={`/voter/elections/${election.id}`}
        className="w-full btn btn-primary flex items-center justify-center gap-2"
      >
        View Details
        <ArrowRight size={18} />
      </Link>
    </div>
  );
};

export default ElectionCard;
