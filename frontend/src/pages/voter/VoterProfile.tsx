import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuth } from '../../hooks/useAuth';
import { voterAPI } from '../../api/voterAPI';
import PageWrapper from '../../components/layout/PageWrapper';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import Avatar from '../../components/common/Avatar';
import { User, Mail, Phone, MapPin, CreditCard, Camera, Trash2, ShieldAlert } from 'lucide-react';
import { toast } from 'react-toastify';
import ConfirmDialog from '../../components/common/ConfirmDialog';

const schema = yup.object().shape({
  full_name: yup.string().required('Full name is required'),
  phone: yup.string().required('Phone number is required'),
  address: yup.string().required('Address is required'),
});

const VoterProfile: React.FC = () => {
  const { user, updateUser, logout } = useAuth();
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
    defaultValues: {
      full_name: user?.full_name,
      phone: user?.phone,
      address: user?.address,
    },
  });

  const onSubmit = async (data: any) => {
    setLoading(true);
    try {
      await voterAPI.updateProfile(data);
      updateUser(data);
      toast.success('Profile updated successfully');
    } catch (error) {
      toast.error('Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handlePhotoUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    try {
      const response = await voterAPI.uploadPhoto(file);
      updateUser({ profile_picture: response.data.photo_url });
      toast.success('Photo uploaded successfully');
    } catch (error) {
      toast.error('Failed to upload photo');
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteAccount = async () => {
    try {
      await voterAPI.deleteAccount();
      toast.success('Account deleted successfully');
      logout();
    } catch (error) {
      toast.error('Failed to delete account');
    }
  };

  return (
    <PageWrapper
      title="My Profile"
      subtitle="Manage your personal information and account settings."
    >
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column - Avatar & Identity */}
        <div className="lg:col-span-1 space-y-6">
          <div className="card text-center">
            <div className="relative inline-block mb-4">
              <Avatar src={user?.profile_picture} name={user?.full_name} size="xl" />
              <label className="absolute bottom-0 right-0 p-2 bg-indigo-600 text-white rounded-full cursor-pointer shadow-lg hover:bg-indigo-700 transition-colors">
                <Camera size={18} />
                <input type="file" className="hidden" accept="image/*" onChange={handlePhotoUpload} disabled={uploading} />
              </label>
            </div>
            <h3 className="text-xl font-bold text-gray-900">{user?.full_name}</h3>
            <p className="text-gray-500 text-sm">{user?.email}</p>
            
            <div className="mt-6 pt-6 border-t border-gray-100 flex justify-center gap-4">
              <div className="text-center">
                <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Status</p>
                <span className="text-sm font-bold text-green-600">{user?.status}</span>
              </div>
              <div className="w-px h-8 bg-gray-100"></div>
              <div className="text-center">
                <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Role</p>
                <span className="text-sm font-bold text-indigo-600">{user?.role}</span>
              </div>
            </div>
          </div>

          <div className="card bg-indigo-900 text-white">
            <h4 className="font-bold mb-4 flex items-center gap-2">
              <ShieldAlert size={20} className="text-indigo-300" />
              Identity Verification
            </h4>
            <div className="space-y-4">
              <div className="flex items-center gap-3">
                <CreditCard size={18} className="text-indigo-300" />
                <div>
                  <p className="text-[10px] text-indigo-300 font-bold uppercase tracking-wider">National ID</p>
                  <p className="text-sm font-medium">{user?.national_id}</p>
                </div>
              </div>
              <div className="p-3 bg-white/10 rounded-xl text-xs text-indigo-100 leading-relaxed">
                Your National ID is used for identity verification and cannot be changed online. Contact support if you need to update it.
              </div>
            </div>
          </div>
        </div>

        {/* Right Column - Edit Form */}
        <div className="lg:col-span-2 space-y-6">
          <div className="card">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Personal Information</h3>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Input
                  label="Full Name"
                  icon={<User size={18} />}
                  {...register('full_name')}
                  error={errors.full_name?.message}
                />
                <Input
                  label="Email Address"
                  type="email"
                  icon={<Mail size={18} />}
                  value={user?.email}
                  disabled
                  className="bg-gray-50"
                />
                <Input
                  label="Phone Number"
                  icon={<Phone size={18} />}
                  {...register('phone')}
                  error={errors.phone?.message}
                />
                <Input
                  label="Address"
                  icon={<MapPin size={18} />}
                  {...register('address')}
                  error={errors.address?.message}
                />
              </div>
              
              <div className="flex justify-end pt-4">
                <Button type="submit" loading={loading} className="px-8">
                  Save Changes
                </Button>
              </div>
            </form>
          </div>

          <div className="card border-red-100 bg-red-50/30">
            <h3 className="text-lg font-bold text-red-600 mb-2">Danger Zone</h3>
            <p className="text-sm text-gray-600 mb-6">
              Once you delete your account, there is no going back. Please be certain.
            </p>
            <Button 
              variant="danger" 
              icon={<Trash2 size={18} />}
              onClick={() => setShowDeleteConfirm(true)}
            >
              Delete My Account
            </Button>
          </div>
        </div>
      </div>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDeleteAccount}
        title="Delete Account"
        message="Are you sure you want to delete your account? This action is permanent and will remove all your data from our system."
        confirmText="Yes, Delete Account"
      />
    </PageWrapper>
  );
};

export default VoterProfile;
