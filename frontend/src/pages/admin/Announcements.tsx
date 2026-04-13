import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../api/adminAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import Table from '../../components/common/Table';
import Button from '../../components/common/Button';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { Megaphone, Plus, Edit2, Trash2, Send } from 'lucide-react';
import { formatDateTime } from '../../utils/dateFormatter';
import { toast } from 'react-toastify';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';

const schema = yup.object().shape({
  title: yup.string().required('Title is required').min(5, 'Min 5 characters'),
  message: yup.string().required('Message is required').min(10, 'Min 10 characters'),
});

const Announcements: React.FC = () => {
  const [announcements, setAnnouncements] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingAnn, setEditingAnn] = useState<any>(null);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(schema)
  });

  const fetchAnnouncements = async () => {
    setLoading(true);
    try {
      const response = await adminAPI.getAnnouncements();
      setAnnouncements(response.data);
    } catch (error) {
      console.error('Failed to fetch announcements', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnnouncements();
  }, []);

  const onSubmit = async (data: any) => {
    setActionLoading(true);
    try {
      if (editingAnn) {
        await adminAPI.updateAnnouncement(editingAnn.id, data);
        toast.success('Announcement updated');
      } else {
        await adminAPI.createAnnouncement(data);
        toast.success('Announcement published');
      }
      setModalOpen(false);
      reset();
      setEditingAnn(null);
      fetchAnnouncements();
    } catch (error) {
      toast.error('Failed to save announcement');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    setActionLoading(true);
    try {
      await adminAPI.deleteAnnouncement(deleteId);
      toast.success('Announcement deleted');
      fetchAnnouncements();
    } catch (error) {
      toast.error('Failed to delete announcement');
    } finally {
      setActionLoading(false);
      setDeleteId(null);
    }
  };

  const columns = [
    {
      header: 'Announcement',
      accessor: (item: any) => (
        <div>
          <p className="font-bold text-gray-900">{item.title}</p>
          <p className="text-xs text-gray-500 line-clamp-1">{item.message}</p>
        </div>
      ),
    },
    {
      header: 'Published On',
      accessor: (item: any) => formatDateTime(item.created_at),
    },
    {
      header: 'Actions',
      accessor: (item: any) => (
        <div className="flex items-center gap-2">
          <button 
            onClick={() => {
              setEditingAnn(item);
              reset(item);
              setModalOpen(true);
            }}
            className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
          >
            <Edit2 size={18} />
          </button>
          <button 
            onClick={() => setDeleteId(item.id)}
            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <PageWrapper
      title="Announcements"
      subtitle="Broadcast important updates to all voters."
      actions={
        <Button 
          icon={<Plus size={18} />} 
          onClick={() => {
            setEditingAnn(null);
            reset({ title: '', message: '' });
            setModalOpen(true);
          }}
        >
          New Announcement
        </Button>
      }
    >
      <div className="card p-0 overflow-hidden">
        <Table 
          columns={columns} 
          data={announcements} 
          loading={loading} 
          emptyMessage="No announcements published yet."
        />
      </div>

      <Modal 
        isOpen={modalOpen} 
        onClose={() => setModalOpen(false)} 
        title={editingAnn ? 'Edit Announcement' : 'New Announcement'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <Input
            label="Title"
            placeholder="e.g. Election Day Reminder"
            {...register('title')}
            error={errors.title?.message}
          />
          <div className="space-y-1.5">
            <label className="block text-sm font-medium text-gray-700">Message</label>
            <textarea
              rows={5}
              className={`input-field ${errors.message ? 'border-red-500' : ''}`}
              placeholder="Enter your message to voters..."
              {...register('message')}
            />
            {errors.message && <p className="text-xs text-red-500 mt-1">{errors.message.message}</p>}
          </div>
          <Button type="submit" className="w-full" loading={actionLoading} icon={<Send size={18} />}>
            {editingAnn ? 'Update Announcement' : 'Publish Announcement'}
          </Button>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Announcement"
        message="Are you sure you want to delete this announcement? It will be removed from all voter dashboards."
        loading={actionLoading}
      />
    </PageWrapper>
  );
};

export default Announcements;
