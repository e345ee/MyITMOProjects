import { Plus, AlertTriangle, Bell } from 'lucide-react';

interface HomePageProps {
  onNavigate: (page: string) => void;
}

export function HomePage({ onNavigate }: HomePageProps) {
  const quickActions = [
    {
      title: 'Добавить продукт',
      description: 'Разместите свой продукт в холодильнике',
      icon: Plus,
      color: 'bg-blue-50 text-blue-600 border-blue-200',
      action: () => onNavigate('my-products'),
    },
    {
      title: 'Посмотреть просроченные',
      description: 'Проверьте продукты с истекшим сроком',
      icon: AlertTriangle,
      color: 'bg-orange-50 text-orange-600 border-orange-200',
      action: () => onNavigate('dashboard'),
    },
    {
      title: 'Последние уведомления',
      description: 'Просмотрите важные обновления',
      icon: Bell,
      color: 'bg-green-50 text-green-600 border-green-200',
      action: () => onNavigate('notifications'),
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-neutral-900 mb-2">Добро пожаловать</h1>
        <p className="text-neutral-600">Управляйте своими продуктами в общежитии</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {quickActions.map((action) => {
          const Icon = action.icon;
          return (
            <button
              key={action.title}
              onClick={action.action}
              className={`${action.color} border rounded-xl p-6 text-left transition-all hover:scale-105 hover:shadow-lg`}
            >
              <div className="flex items-start justify-between mb-4">
                <div className={`p-3 rounded-lg ${action.color}`}>
                  <Icon className="w-6 h-6" />
                </div>
              </div>
              <h3 className="text-neutral-900 mb-2">{action.title}</h3>
              <p className="text-sm text-neutral-600">{action.description}</p>
            </button>
          );
        })}
      </div>

      <div className="bg-white rounded-xl border border-neutral-200 p-6">
        <h2 className="text-neutral-900 mb-4">Обзор холодильника</h2>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="space-y-2">
            <p className="text-sm text-neutral-600">Всего продуктов</p>
            <p className="text-neutral-900">48</p>
          </div>
          <div className="space-y-2">
            <p className="text-sm text-neutral-600">Мои продукты</p>
            <p className="text-neutral-900">8</p>
          </div>
          <div className="space-y-2">
            <p className="text-sm text-neutral-600">Свободных полок</p>
            <p className="text-neutral-900">12 / 60</p>
          </div>
          <div className="space-y-2">
            <p className="text-sm text-neutral-600">Истекает сегодня</p>
            <p className="text-orange-600">3</p>
          </div>
        </div>
      </div>

      <button
        onClick={() => onNavigate('refrigerator')}
        className="w-full bg-blue-600 text-white rounded-lg px-6 py-3 hover:bg-blue-700 transition-colors"
      >
        Перейти к холодильнику
      </button>
    </div>
  );
}
