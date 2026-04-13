import React from 'react';

const Footer: React.FC = () => {
  return (
    <footer className="bg-white border-t border-gray-100 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold">V</div>
            <span className="font-bold text-gray-900">SecureVote</span>
          </div>
          <p className="text-sm text-gray-500">
            © {new Date().getFullYear()} SecureVote Online Voting System. All rights reserved.
          </p>
          <div className="flex gap-6">
            <a href="#" className="text-sm text-gray-400 hover:text-indigo-600 transition-colors">Privacy Policy</a>
            <a href="#" className="text-sm text-gray-400 hover:text-indigo-600 transition-colors">Terms of Service</a>
            <a href="#" className="text-sm text-gray-400 hover:text-indigo-600 transition-colors">Contact Support</a>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
