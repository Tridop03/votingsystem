import { format, formatDistanceToNow, isAfter, isBefore } from 'date-fns';

export const formatDate = (date: string | Date) => {
  if (!date) return '';
  return format(new Date(date), 'MMM dd, yyyy');
};

export const formatDateTime = (date: string | Date) => {
  if (!date) return '';
  return format(new Date(date), 'MMM dd, yyyy p');
};

export const formatCountdown = (endDate: string | Date) => {
  if (!endDate) return '';
  return formatDistanceToNow(new Date(endDate), { addSuffix: true });
};

export const isElectionActive = (start: string | Date, end: string | Date) => {
  const now = new Date();
  const startDate = new Date(start);
  const endDate = new Date(end);
  return isAfter(now, startDate) && isBefore(now, endDate);
};

export const getElectionStatus = (start: string | Date, end: string | Date) => {
  const now = new Date();
  const startDate = new Date(start);
  const endDate = new Date(end);

  if (isBefore(now, startDate)) return 'UPCOMING';
  if (isAfter(now, endDate)) return 'PAST';
  return 'ACTIVE';
};
