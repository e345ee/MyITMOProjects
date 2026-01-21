import { Refrigerator, Package, LayoutDashboard, Building2, User, Bell } from 'lucide-react';
import type { UserRole } from '../App';

interface NavigationProps {
  currentPage: string;
  onNavigate: (page: string) => void;
  userRole: UserRole;
}

export function Navigation({ currentPage, onNavigate, userRole }: NavigationProps) {
  const navItems = [
    { id: 'home', label: 'Холодильник', icon: Refrigerator },
    { id: 'my-products', label: 'Продукты', icon: Package },
    { id: 'dashboard', label: 'Панель контроля', icon: LayoutDashboard },
    ...(userRole === 'admin' ? [{ id: 'dorm-overview', label: 'Обзор общежития', icon: Building2 }] : []),
    { id: 'profile', label: 'Профиль', icon: User },
    { id: 'notifications', label: 'Уведомления', icon: Bell, badge: 3 },
  ];

  return (
    <nav className="bg-white border-b border-neutral-200">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <div 
            className="flex items-center gap-2 cursor-pointer"
            onClick={() => onNavigate('home')}
          >
            <Refrigerator className="w-6 h-6 text-blue-600" />
            <span className="text-neutral-900">Система бронирования</span>
          </div>
          
          <div className="flex items-center gap-1">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = currentPage === item.id || (currentPage === 'home' && item.id === 'home') || (currentPage === 'refrigerator' && item.id === 'home') || (currentPage === 'all-products' && item.id === 'my-products');
              
              return (
                <button
                  key={item.id}
                  onClick={() => onNavigate(item.id)}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-colors relative ${
                    isActive
                      ? 'bg-neutral-100 text-neutral-900'
                      : 'text-neutral-600 hover:bg-neutral-50 hover:text-neutral-900'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  <span className="text-sm">{item.label}</span>
                  {item.badge && (
                    <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                      {item.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </nav>
  );
}
