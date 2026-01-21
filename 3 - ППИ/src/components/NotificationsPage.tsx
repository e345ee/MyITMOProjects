import { useMemo, useState } from 'react';
import { toast } from 'sonner';
import { AlertTriangle, Trash2, Clock, CheckCircle, Bell, ArrowRight } from 'lucide-react';

interface Notification {
  id: number;
  type: 'expiring' | 'deleted' | 'expired' | 'success';
  title: string;
  message: string;
  time: string;
  read: boolean;
  shelfId?: number;
}

const initialNotifications: Notification[] = [
  {
    id: 1,
    type: 'expiring',
    title: 'Срок годности истекает',
    message: 'Ваше молоко (полка #5) истекает через 3 дня',
    time: '2 часа назад',
    read: false,
  },
  {
    id: 2,
    type: 'expired',
    title: 'Продукт просрочен',
    message: 'Ваша колбаса (полка #3) просрочена. Пожалуйста, удалите её',
    time: '5 часов назад',
    read: false,
  },
  {
    id: 3,
    type: 'deleted',
    title: 'Продукт удалён администратором',
    message: 'Ваш йогурт был удалён старостой из-за истекшего срока годности',
    time: '1 день назад',
    read: false,
  },
  {
    id: 4,
    type: 'success',
    title: 'Продукт успешно добавлен',
    message: 'Творог добавлен на полку #11',
    time: '1 день назад',
    read: true,
  },
  {
    id: 5,
    type: 'expiring',
    title: 'Скоро истечёт срок',
    message: 'Творог 9% (полка #11) истекает через 4 дня',
    time: '2 дня назад',
    read: true,
  },
  {
    id: 6,
    type: 'success',
    title: 'Продукт использован',
    message: 'Вы отметили как использованное: Сметана 20%',
    time: '3 дня назад',
    read: true,
  },
];

export function NotificationsPage({
  onOpenShelf,
}: {
  onOpenShelf?: (shelfId: number) => void;
}) {
  const [notifications, setNotifications] = useState<Notification[]>(() => {
    // Автоматически вытащим номер полки из текста (если есть)
    return initialNotifications.map((n) => {
      const match = n.message.match(/#(\d+)/);
      const shelfId = match ? Number(match[1]) : undefined;
      return { ...n, shelfId };
    });
  });

  const unreadCount = useMemo(() => notifications.filter((n) => !n.read).length, [notifications]);

  const markAsRead = (id: number) => {
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
  };

  const markAllAsRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    toast.success('Все уведомления отмечены как прочитанные');
  };

  const openProduct = (notification: Notification) => {
    if (!notification.shelfId || !onOpenShelf) return;
    markAsRead(notification.id);
    onOpenShelf(notification.shelfId);
  };
  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'expiring':
        return <Clock className="w-5 h-5" />;
      case 'expired':
        return <AlertTriangle className="w-5 h-5" />;
      case 'deleted':
        return <Trash2 className="w-5 h-5" />;
      case 'success':
        return <CheckCircle className="w-5 h-5" />;
      default:
        return <Bell className="w-5 h-5" />;
    }
  };

  const getNotificationColor = (type: string) => {
    switch (type) {
      case 'expiring':
        return 'bg-orange-50 text-orange-600 border-orange-200';
      case 'expired':
        return 'bg-red-50 text-red-600 border-red-200';
      case 'deleted':
        return 'bg-purple-50 text-purple-600 border-purple-200';
      case 'success':
        return 'bg-green-50 text-green-600 border-green-200';
      default:
        return 'bg-blue-50 text-blue-600 border-blue-200';
    }
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-neutral-900 mb-2">Уведомления</h1>
          <p className="text-neutral-600">
            {unreadCount > 0 ? `У вас ${unreadCount} непрочитанных уведомлений` : 'Все уведомления прочитаны'}
          </p>
        </div>
        
        <button
          onClick={markAllAsRead}
          className="px-4 py-2 text-sm text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
        >
          Отметить все как прочитанные
        </button>
      </div>

      <div className="space-y-3">
        {notifications.map((notification) => (
          <div
            key={notification.id}
            className={`bg-white rounded-xl border transition-all ${
              notification.read 
                ? 'border-neutral-200' 
                : 'border-blue-200 shadow-sm'
            }`}
          >
            <div className="p-6">
              <div className="flex items-start gap-4">
                <div className={`p-3 rounded-lg border ${getNotificationColor(notification.type)}`}>
                  {getNotificationIcon(notification.type)}
                </div>
                
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4 mb-2">
                    <h3 className="text-neutral-900">{notification.title}</h3>
                    {!notification.read && (
                      <span className="w-2 h-2 bg-blue-500 rounded-full flex-shrink-0 mt-2"></span>
                    )}
                  </div>
                  <p className="text-sm text-neutral-600 mb-3">{notification.message}</p>
                  <div className="flex items-center gap-4">
                    <span className="text-xs text-neutral-500">{notification.time}</span>
                    {!notification.read && (
                      <button
                        onClick={() => markAsRead(notification.id)}
                        className="text-xs text-blue-600 hover:text-blue-700"
                      >
                        Отметить как прочитанное
                      </button>
                    )}
                    {!!notification.shelfId && !!onOpenShelf && (
                      <button
                        onClick={() => openProduct(notification)}
                        className="ml-auto inline-flex items-center gap-2 text-xs text-neutral-700 hover:text-neutral-900"
                        title="Перейти к продукту"
                      >
                        Перейти к продукту
                        <ArrowRight className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {notifications.length === 0 && (
        <div className="text-center py-12 bg-white rounded-xl border border-neutral-200">
          <Bell className="w-12 h-12 text-neutral-300 mx-auto mb-4" />
          <p className="text-neutral-600">У вас пока нет уведомлений</p>
        </div>
      )}
    </div>
  );
}
