import React from 'react';
import Avatar from '../common/Avatar';
import Button from '../common/Button';
import { Check } from 'lucide-react';

interface CandidateCardProps {
  candidate: any;
  isSelected?: boolean;
  onSelect?: () => void;
  showVoteButton?: boolean;
}

const CandidateCard: React.FC<CandidateCardProps> = ({ 
  candidate, 
  isSelected = false, 
  onSelect,
  showVoteButton = false 
}) => {
  return (
    <div 
      onClick={onSelect}
      className={`card cursor-pointer transition-all duration-300 border-2 ${
        isSelected 
          ? 'border-indigo-600 bg-indigo-50/30' 
          : 'border-transparent hover:border-indigo-100'
      }`}
    >
      <div className="flex items-center gap-4 mb-4">
        <Avatar src={candidate.photo_url} name={candidate.full_name} size="lg" />
        <div>
          <h4 className="text-lg font-bold text-gray-900">{candidate.full_name}</h4>
          <p className="text-sm font-medium text-indigo-600">{candidate.party}</p>
        </div>
        {isSelected && (
          <div className="ml-auto w-8 h-8 bg-indigo-600 text-white rounded-full flex items-center justify-center shadow-lg">
            <Check size={18} />
          </div>
        )}
      </div>
      
      <p className="text-gray-500 text-sm line-clamp-3 mb-4">
        {candidate.bio}
      </p>
      
      {showVoteButton && (
        <Button 
          variant={isSelected ? 'primary' : 'outline'} 
          className="w-full"
          onClick={(e) => {
            e.stopPropagation();
            onSelect?.();
          }}
        >
          {isSelected ? 'Selected' : 'Select Candidate'}
        </Button>
      )}
    </div>
  );
};

export default CandidateCard;
