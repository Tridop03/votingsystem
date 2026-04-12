import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import Input from '../common/Input';
import Button from '../common/Button';

const schema = yup.object().shape({
  full_name: yup.string().required('Full name is required'),
  party: yup.string().required('Party name is required'),
  bio: yup.string().required('Bio is required').min(20, 'Bio must be at least 20 characters'),
  election_category_id: yup.string().required('Election category is required'),
});

interface CandidateFormProps {
  initialData?: any;
  onSubmit: (data: any) => void;
  loading?: boolean;
  categories: any[];
}

const CandidateForm: React.FC<CandidateFormProps> = ({ initialData, onSubmit, loading, categories }) => {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <Input
        label="Full Name"
        placeholder="Enter candidate's full name"
        {...register('full_name')}
        error={errors.full_name?.message as string}
      />
      
      <Input
        label="Political Party"
        placeholder="Enter party name"
        {...register('party')}
        error={errors.party?.message as string}
      />
      
      <div className="space-y-1.5">
        <label className="block text-sm font-medium text-gray-700">Bio</label>
        <textarea
          rows={4}
          className={`input-field ${errors.bio ? 'border-red-500' : ''}`}
          placeholder="Enter candidate biography..."
          {...register('bio')}
        />
        {errors.bio && <p className="text-xs text-red-500 mt-1">{errors.bio.message as string}</p>}
      </div>
      
      <div className="space-y-1.5">
        <label className="block text-sm font-medium text-gray-700">Election Category</label>
        <select
          className={`input-field ${errors.election_category_id ? 'border-red-500' : ''}`}
          {...register('election_category_id')}
        >
          <option value="">Select a category</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.category_name}
            </option>
          ))}
        </select>
        {errors.election_category_id && (
          <p className="text-xs text-red-500 mt-1">{errors.election_category_id.message as string}</p>
        )}
      </div>
      
      <Button type="submit" className="w-full" loading={loading}>
        {initialData ? 'Update Candidate' : 'Create Candidate'}
      </Button>
    </form>
  );
};

export default CandidateForm;
