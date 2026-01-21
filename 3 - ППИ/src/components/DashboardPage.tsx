import { AlertTriangle, Clock, TrendingUp, Package } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const chartData = [
  { day: 'Пн', products: 45 },
  { day: 'Вт', products: 52 },
  { day: 'Ср', products: 48 },
  { day: 'Чт', products: 61 },
  { day: 'Пт', products: 55 },
  { day: 'Сб', products: 42 },
  { day: 'Вс', products: 38 },
];

const allProducts = [
  { id: 1, name: 'Йогурт', owner: 'Петр Иванов', shelf: 3, expiryDate: '2025-11-30', status: 'expired' },
  { id: 2, name: 'Творог', owner: 'Алексей Новиков', shelf: 6, expiryDate: '2025-12-01', status: 'expired' },
  { id: 3, name: 'Молоко 3.2%', owner: 'Иван Петров', shelf: 5, expiryDate: '2025-12-05', status: 'expiring' },
  { id: 4, name: 'Фрукты', owner: 'Дмитрий Соколов', shelf: 7, expiryDate: '2025-12-03', status: 'expiring' },
  { id: 5, name: 'Творог 9%', owner: 'Вы', shelf: 11, expiryDate: '2025-12-06', status: 'expiring' },
];

export function DashboardPage() {
  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'fresh':
        return <span className="text-xs px-2 py-1 rounded-full bg-green-100 text-green-700">Свежий</span>;
      case 'expiring':
        return <span className="text-xs px-2 py-1 rounded-full bg-orange-100 text-orange-700">Истекает</span>;
      case 'expired':
        return <span className="text-xs px-2 py-1 rounded-full bg-red-100 text-red-700">Просрочен</span>;
      default:
        return null;
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-neutral-900 mb-2">Панель контроля</h1>
        <p className="text-neutral-600">Аналитика и мониторинг холодильника</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-start justify-between mb-4">
            <div className="p-3 bg-red-50 rounded-lg">
              <AlertTriangle className="w-6 h-6 text-red-600" />
            </div>
            <span className="text-xs px-2 py-1 rounded-full bg-red-100 text-red-700">Срочно</span>
          </div>
          <p className="text-neutral-900 mb-1">2</p>
          <p className="text-sm text-neutral-600">Просроченных</p>
        </div>

        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-start justify-between mb-4">
            <div className="p-3 bg-orange-50 rounded-lg">
              <Clock className="w-6 h-6 text-orange-600" />
            </div>
            <span className="text-xs px-2 py-1 rounded-full bg-orange-100 text-orange-700">Внимание</span>
          </div>
          <p className="text-neutral-900 mb-1">3</p>
          <p className="text-sm text-neutral-600">Истекает срок</p>
        </div>

        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-start justify-between mb-4">
            <div className="p-3 bg-blue-50 rounded-lg">
              <Package className="w-6 h-6 text-blue-600" />
            </div>
          </div>
          <p className="text-neutral-900 mb-1">15</p>
          <p className="text-sm text-neutral-600">Продукты &gt; 7 дней</p>
        </div>

        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-start justify-between mb-4">
            <div className="p-3 bg-green-50 rounded-lg">
              <TrendingUp className="w-6 h-6 text-green-600" />
            </div>
          </div>
          <p className="text-neutral-900 mb-1">80%</p>
          <p className="text-sm text-neutral-600">Заполненность</p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200 p-6">
        <h2 className="text-neutral-900 mb-6">График заполненности холодильника</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e5e5" />
            <XAxis dataKey="day" stroke="#737373" />
            <YAxis stroke="#737373" />
            <Tooltip 
              contentStyle={{ 
                backgroundColor: 'white', 
                border: '1px solid #e5e5e5',
                borderRadius: '8px'
              }}
            />
            <Bar dataKey="products" fill="#3b82f6" radius={[8, 8, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200">
        <div className="p-6 border-b border-neutral-200">
          <h2 className="text-neutral-900">Требуют внимания</h2>
          <p className="text-sm text-neutral-600 mt-1">Просроченные и истекающие продукты</p>
        </div>
        
        <div className="overflow-hidden">
          <table className="w-full">
            <thead className="bg-neutral-50 border-b border-neutral-200">
              <tr>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Продукт</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Владелец</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Полка</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Срок годности</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Статус</th>
              </tr>
            </thead>
            <tbody>
              {allProducts.map((product) => (
                <tr key={product.id} className="border-b border-neutral-100 hover:bg-neutral-50">
                  <td className="px-6 py-4 text-sm text-neutral-900">{product.name}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{product.owner}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">#{product.shelf}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{product.expiryDate}</td>
                  <td className="px-6 py-4 text-sm">{getStatusBadge(product.status)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}