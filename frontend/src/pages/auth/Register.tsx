import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { User, Mail, Lock, Phone, CreditCard, MapPin, ArrowRight } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';

const schema = yup.object().shape({
  full_name: yup.string().required('Full name is required').min(3, 'Min 3 characters'),
  email: yup.string().required('Email is required').email('Invalid email format'),
  national_id: yup.string().required('National ID is required').min(6, 'Min 6 characters'),
  phone: yup.string().required('Phone number is required'),
  address: yup.string().required('Address is required'),
  password: yup.string()
    .required('Password is required')
    .min(8, 'Min 8 characters')
    .matches(/[A-Z]/, 'Must contain uppercase')
    .matches(/[0-9]/, 'Must contain number')
    .matches(/[^a-zA-Z0-9]/, 'Must contain special character'),
  confirm_password: yup.string()
    .oneOf([yup.ref('password')], 'Passwords must match')
    .required('Confirm password is required'),
});

const Register: React.FC = () => {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: any) => {
    setLoading(true);
    try {
      const { confirm_password, ...submitData } = data;
      await registerUser(submitData);
      navigate('/login');
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4 py-12">
      <div className="w-full max-w-2xl">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-indigo-600 rounded-2xl text-white font-bold text-3xl shadow-xl shadow-indigo-200 mb-4">
            V
          </div>
          <h1 className="text-3xl font-bold text-gray-900">Create Account</h1>
          <p className="text-gray-500 mt-2">Join the secure digital voting platform</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Input
                label="Full Name"
                placeholder="John Doe"
                icon={<User size={18} />}
                {...register('full_name')}
                error={errors.full_name?.message}
              />
              <Input
                label="Email Address"
                type="email"
                placeholder="john@example.com"
                icon={<Mail size={18} />}
                {...register('email')}
                error={errors.email?.message}
              />
              <Input
                label="National ID"
                placeholder="ID Number"
                icon={<CreditCard size={18} />}
                {...register('national_id')}
                error={errors.national_id?.message}
              />
              <Input
                label="Phone Number"
                placeholder="+1 234 567 890"
                icon={<Phone size={18} />}
                {...register('phone')}
                error={errors.phone?.message}
              />
              <div className="md:col-span-2">
                <Input
                  label="Address"
                  placeholder="Street, City, Country"
                  icon={<MapPin size={18} />}
                  {...register('address')}
                  error={errors.address?.message}
                />
              </div>
              <Input
                label="Password"
                type="password"
                placeholder="••••••••"
                icon={<Lock size={18} />}
                {...register('password')}
                error={errors.password?.message}
              />
              <Input
                label="Confirm Password"
                type="password"
                placeholder="••••••••"
                icon={<Lock size={18} />}
                {...register('confirm_password')}
                error={errors.confirm_password?.message}
              />
            </div>

            <Button type="submit" className="w-full" loading={loading} icon={<ArrowRight size={18} />}>
              Register Account
            </Button>
          </form>

          <div className="mt-8 pt-6 border-t border-gray-100 text-center">
            <p className="text-gray-600 text-sm">
              Already have an account?{' '}
              <Link to="/login" className="font-bold text-indigo-600 hover:text-indigo-500">
                Sign in instead
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
