import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Vote, 
  Users, 
  SquareUser, 
  ChartBarBig as BarChart3, 
  History, 
  Megaphone, 
  ShieldCheck 
} from 'lucide-react';

const Sidebar: React.FC = () => {
  const menuItems = [
    { name: 'Dashboard', path: '/admin/dashboard', icon: LayoutDashboard },
    { name: 'Elections', path: '/admin/elections', icon: Vote },
    { name: 'Candidates', path: '/admin/candidates', icon: SquareUser },
    { name: 'Voters', path: '/admin/voters', icon: Users },
    { name: 'Audit Logs', path: '/admin/audit-logs', icon: ShieldCheck },
    { name: 'Announcements', path: '/admin/announcements', icon: Megaphone },
  ];

  return (
    <aside className="hidden lg:flex flex-col w-64 bg-white border-r border-gray-100 h-[calc(100vh-4rem)] sticky top-16">
      <div className="flex-1 py-6 px-4 space-y-1">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                isActive
                  ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-100'
                  : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900'
              }`
            }
          >
            <item.icon size={20} />
            {item.name}
          </NavLink>
        ))}
      </div>
      
      <div className="p-4 border-t border-gray-50">
        <div className="bg-indigo-50 rounded-2xl p-4">
          <p className="text-xs font-semibold text-indigo-600 uppercase tracking-wider mb-1">System Status</p>
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
            <span className="text-sm text-indigo-900 font-medium">All systems operational</span>
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
