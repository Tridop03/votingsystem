import React from 'react';
import Badge from '../common/Badge';
import { getElectionStatus } from '../../utils/dateFormatter';

interface ElectionStatusBadgeProps {
  start: string | Date;
  end: string | Date;
}

const ElectionStatusBadge: React.FC<ElectionStatusBadgeProps> = ({ start, end }) => {
  const status = getElectionStatus(start, end);

  const configs = {
    ACTIVE: { variant: 'success' as const, label: 'Active' },
    UPCOMING: { variant: 'info' as const, label: 'Upcoming' },
    PAST: { variant: 'neutral' as const, label: 'Ended' },
  };

  const config = configs[status];

  return <Badge variant={config.variant}>{config.label}</Badge>;
};

export default ElectionStatusBadge;
