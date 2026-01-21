import { useState } from 'react';
import { Toaster } from 'sonner';
import { HomePage } from './components/HomePage';
import { RefrigeratorPage } from './components/RefrigeratorPage';
import { MyProductsPage } from './components/MyProductsPage';
import { AllProductsPage } from './components/AllProductsPage';
import { DashboardPage } from './components/DashboardPage';
import { DormOverviewPage } from './components/DormOverviewPage';
import { ProfilePage } from './components/ProfilePage';
import { NotificationsPage } from './components/NotificationsPage';
import { Navigation } from './components/Navigation';

export type UserRole = 'student' | 'monitor' | 'admin';

export default function App() {
  const [currentPage, setCurrentPage] = useState('home');
  const [userRole, setUserRole] = useState<UserRole>('student');
  const [highlightShelfId, setHighlightShelfId] = useState<number | null>(null);

  const openShelfFromNotifications = (shelfId: number) => {
    setHighlightShelfId(shelfId);
    setCurrentPage('refrigerator');
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'home':
        return <HomePage onNavigate={setCurrentPage} />;
      case 'refrigerator':
        return <RefrigeratorPage highlightShelfId={highlightShelfId} />;
      case 'my-products':
        return <MyProductsPage />;
      case 'all-products':
        return <AllProductsPage />;
      case 'dashboard':
        return <DashboardPage />;
      case 'dorm-overview':
        return <DormOverviewPage />;
      case 'profile':
        return <ProfilePage userRole={userRole} onRoleChange={setUserRole} />;
      case 'notifications':
        return <NotificationsPage onOpenShelf={openShelfFromNotifications} />;
      default:
        return <HomePage onNavigate={setCurrentPage} />;
    }
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      <Toaster richColors position="top-right" />
      <Navigation 
        currentPage={currentPage} 
        onNavigate={setCurrentPage}
        userRole={userRole}
      />
      <main className="max-w-7xl mx-auto px-4 py-8">
        {renderPage()}
      </main>
    </div>
  );
}
