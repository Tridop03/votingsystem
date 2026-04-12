import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { adminAPI } from '../../../api/adminAPI';
import { electionAPI } from '../../../api/electionAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import Input from '../../../components/common/Input';
import Button from '../../../components/common/Button';
import { Plus, Trash2, ArrowLeft, Save } from 'lucide-react';
import { toast } from 'react-toastify';

const schema = yup.object().shape({
  title: yup.string().required('Title is required').min(5, 'Min 5 characters'),
  description: yup.string().required('Description is required').min(10, 'Min 10 characters'),
  start_time: yup.string().required('Start time is required'),
  end_time: yup.string().required('End time is required'),
  categories: yup.array().of(
    yup.object().shape({
      category_name: yup.string().required('Category name is required'),
    })
  ).min(1, 'At least one category is required'),
});

const ElectionForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(!!id);

  const { register, control, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
    defaultValues: {
      categories: [{ category_name: '' }]
    }
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: "categories"
  });

  useEffect(() => {
    if (id) {
      const fetchElection = async () => {
        try {
          const response = await electionAPI.getElectionById(id);
          // Format dates for input[type="datetime-local"]
          const data = response.data;
          data.start_time = new Date(data.start_time).toISOString().slice(0, 16);
          data.end_time = new Date(data.end_time).toISOString().slice(0, 16);
          reset(data);
        } catch (error) {
          toast.error('Failed to load election');
          navigate('/admin/elections');
        } finally {
          setFetching(false);
        }
      };
      fetchElection();
    }
  }, [id, reset, navigate]);

  const onSubmit = async (data: any) => {
    setLoading(true);
    try {
      if (id) {
        await adminAPI.updateElection(id, data);
        toast.success('Election updated successfully');
      } else {
        await adminAPI.createElection(data);
        toast.success('Election created successfully');
      }
      navigate('/admin/elections');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to save election');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageWrapper
      title={id ? 'Edit Election' : 'New Election'}
      subtitle={id ? 'Update election details and categories.' : 'Set up a new voting event.'}
    >
      <Button 
        variant="secondary" 
        size="sm" 
        icon={<ArrowLeft size={16} />} 
        onClick={() => navigate('/admin/elections')}
        className="mb-6"
      >
        Back to List
      </Button>

      <div className="max-w-4xl">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
          <div className="card space-y-6">
            <h3 className="text-lg font-bold text-gray-900 border-b pb-4">Basic Information</h3>
            <Input
              label="Election Title"
              placeholder="e.g. Presidential Election 2025"
              {...register('title')}
              error={errors.title?.message}
            />
            
            <div className="space-y-1.5">
              <label className="block text-sm font-medium text-gray-700">Description</label>
              <textarea
                rows={4}
                className={`input-field ${errors.description ? 'border-red-500' : ''}`}
                placeholder="Provide details about the election..."
                {...register('description')}
              />
              {errors.description && <p className="text-xs text-red-500 mt-1">{errors.description.message}</p>}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Input
                label="Start Date & Time"
                type="datetime-local"
                {...register('start_time')}
                error={errors.start_time?.message}
              />
              <Input
                label="End Date & Time"
                type="datetime-local"
                {...register('end_time')}
                error={errors.end_time?.message}
              />
            </div>
          </div>

          <div className="card space-y-6">
            <div className="flex items-center justify-between border-b pb-4">
              <h3 className="text-lg font-bold text-gray-900">Election Categories</h3>
              <Button 
                type="button" 
                variant="outline" 
                size="sm" 
                icon={<Plus size={16} />}
                onClick={() => append({ category_name: '' })}
              >
                Add Category
              </Button>
            </div>

            <div className="space-y-4">
              {fields.map((field, index) => (
                <div key={field.id} className="flex items-end gap-4">
                  <div className="flex-1">
                    <Input
                      label={`Category ${index + 1}`}
                      placeholder="e.g. President, Secretary, etc."
                      {...register(`categories.${index}.category_name` as const)}
                      error={errors.categories?.[index]?.category_name?.message}
                    />
                  </div>
                  {fields.length > 1 && (
                    <button
                      type="button"
                      onClick={() => remove(index)}
                      className="p-2.5 text-red-500 hover:bg-red-50 rounded-xl transition-all mb-[1px]"
                    >
                      <Trash2 size={20} />
                    </button>
                  )}
                </div>
              ))}
              {errors.categories?.root && (
                <p className="text-sm text-red-500">{errors.categories.root.message}</p>
              )}
            </div>
          </div>

          <div className="flex justify-end gap-4">
            <Button 
              type="button" 
              variant="secondary" 
              onClick={() => navigate('/admin/elections')}
            >
              Cancel
            </Button>
            <Button 
              type="submit" 
              loading={loading} 
              icon={<Save size={18} />}
              className="px-12"
            >
              {id ? 'Update Election' : 'Create Election'}
            </Button>
          </div>
        </form>
      </div>
    </PageWrapper>
  );
};

export default ElectionForm;
