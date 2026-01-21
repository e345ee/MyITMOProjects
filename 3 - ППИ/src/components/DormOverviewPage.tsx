import { useMemo, useState } from 'react';
import { toast } from 'sonner';
import { Refrigerator, Users, Settings, FileText } from 'lucide-react';
import { PortalModal } from './PortalModal';

const initialRefrigerators = [
  { id: 1, floor: 3, room: '301-305', capacity: 60, occupied: 48, status: 'normal' },
  { id: 2, floor: 3, room: '306-310', capacity: 60, occupied: 52, status: 'warning' },
  { id: 3, floor: 4, room: '401-405', capacity: 60, occupied: 35, status: 'normal' },
  { id: 4, floor: 4, room: '406-410', capacity: 60, occupied: 58, status: 'critical' },
  { id: 5, floor: 5, room: '501-505', capacity: 60, occupied: 42, status: 'normal' },
];

const initialMonitors = [
  { id: 1, name: 'Анна Смирнова', floor: 3, email: 'anna@example.com', students: 45 },
  { id: 2, name: 'Дмитрий Соколов', floor: 4, email: 'dmitry@example.com', students: 52 },
  { id: 3, name: 'Елена Волкова', floor: 5, email: 'elena@example.com', students: 38 },
];

const auditLog = [
  { id: 1, user: 'Иван Петров', action: 'Добавил продукт "Молоко"', time: '2025-12-02 10:30', type: 'add' },
  { id: 2, user: 'Староста (Анна)', action: 'Удалила просроченный продукт', time: '2025-12-02 09:15', type: 'delete' },
  { id: 3, user: 'Мария Козлова', action: 'Переместила продукт на полку #12', time: '2025-12-02 08:45', type: 'move' },
  { id: 4, user: 'Администратор', action: 'Изменил лимит холодильника #2', time: '2025-12-01 16:20', type: 'config' },
  { id: 5, user: 'Петр Иванов', action: 'Использовал продукт "Йогурт"', time: '2025-12-01 14:10', type: 'use' },
];

export function DormOverviewPage() {
  const [refrigerators, setRefrigerators] = useState(() => initialRefrigerators);
  const [monitors, setMonitors] = useState(() => initialMonitors);
  const [showAddFridge, setShowAddFridge] = useState(false);
  const [showAddMonitor, setShowAddMonitor] = useState(false);
  const [newFridge, setNewFridge] = useState({ floor: '', room: '', capacity: '60' });
  const [newMonitor, setNewMonitor] = useState({ name: '', floor: '', email: '', students: '' });

  const fridgeCount = refrigerators.length;
  const monitorCount = monitors.length;

  const usedFloors = useMemo(() => new Set(monitors.map((m) => m.floor)), [monitors]);

  const closeFridgeModal = () => {
    setShowAddFridge(false);
    setNewFridge({ floor: '', room: '', capacity: '60' });
  };

  const closeMonitorModal = () => {
    setShowAddMonitor(false);
    setNewMonitor({ name: '', floor: '', email: '', students: '' });
  };

  const handleAddFridge = (e: React.FormEvent) => {
    e.preventDefault();
    const floor = Number(newFridge.floor);
    const capacity = Number(newFridge.capacity);
    if (!floor || floor < 1) {
      toast.error('Укажите корректный этаж');
      return;
    }
    if (!newFridge.room.trim()) {
      toast.error('Укажите комнаты/секции');
      return;
    }
    if (!capacity || capacity < 1) {
      toast.error('Укажите корректную вместимость');
      return;
    }
    const nextId = Math.max(0, ...refrigerators.map((r) => r.id)) + 1;
    setRefrigerators((prev) => [
      ...prev,
      { id: nextId, floor, room: newFridge.room.trim(), capacity, occupied: 0, status: 'normal' },
    ]);
    toast.success(`Холодильник добавлен (этаж ${floor}, ${newFridge.room.trim()})`);
    closeFridgeModal();
  };

  const handleAddMonitor = (e: React.FormEvent) => {
    e.preventDefault();
    const floor = Number(newMonitor.floor);
    const students = Number(newMonitor.students || 0);
    if (!newMonitor.name.trim()) {
      toast.error('Укажите имя старосты');
      return;
    }
    if (!floor || floor < 1) {
      toast.error('Укажите корректный этаж');
      return;
    }
    if (usedFloors.has(floor)) {
      toast.error('На этом этаже уже назначен староста');
      return;
    }
    if (!newMonitor.email.trim()) {
      toast.error('Укажите email');
      return;
    }
    const nextId = Math.max(0, ...monitors.map((m) => m.id)) + 1;
    setMonitors((prev) => [
      ...prev,
      { id: nextId, name: newMonitor.name.trim(), floor, email: newMonitor.email.trim(), students: students || 0 },
    ]);
    toast.success(`Староста назначен: ${newMonitor.name.trim()} (этаж ${floor})`);
    closeMonitorModal();
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'normal':
        return 'bg-green-100 text-green-700';
      case 'warning':
        return 'bg-orange-100 text-orange-700';
      case 'critical':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-neutral-100 text-neutral-700';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'normal':
        return 'Нормально';
      case 'warning':
        return 'Почти заполнен';
      case 'critical':
        return 'Переполнен';
      default:
        return 'Неизвестно';
    }
  };

  const getActionIcon = (type: string) => {
    const colors: Record<string, string> = {
      add: 'bg-green-100 text-green-700',
      delete: 'bg-red-100 text-red-700',
      move: 'bg-blue-100 text-blue-700',
      config: 'bg-purple-100 text-purple-700',
      use: 'bg-orange-100 text-orange-700',
    };
    return colors[type] || 'bg-neutral-100 text-neutral-700';
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-neutral-900 mb-2">Обзор общежития</h1>
        <p className="text-neutral-600">Административная панель управления (только для администраторов)</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-center gap-3 mb-3">
            <div className="p-2 bg-blue-50 rounded-lg">
              <Refrigerator className="w-5 h-5 text-blue-600" />
            </div>
            <span className="text-sm text-neutral-600">Холодильников</span>
          </div>
          <p className="text-neutral-900">{fridgeCount}</p>
        </div>

        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-center gap-3 mb-3">
            <div className="p-2 bg-green-50 rounded-lg">
              <Users className="w-5 h-5 text-green-600" />
            </div>
            <span className="text-sm text-neutral-600">Студентов</span>
          </div>
          <p className="text-neutral-900">135</p>
        </div>

        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-center gap-3 mb-3">
            <div className="p-2 bg-purple-50 rounded-lg">
              <Settings className="w-5 h-5 text-purple-600" />
            </div>
            <span className="text-sm text-neutral-600">Старост</span>
          </div>
          <p className="text-neutral-900">{monitorCount}</p>
        </div>

        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="flex items-center gap-3 mb-3">
            <div className="p-2 bg-orange-50 rounded-lg">
              <FileText className="w-5 h-5 text-orange-600" />
            </div>
            <span className="text-sm text-neutral-600">Всего продуктов</span>
          </div>
          <p className="text-neutral-900">235</p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200">
        <div className="p-6 border-b border-neutral-200 flex items-center justify-between">
          <div>
            <h2 className="text-neutral-900">Обзор холодильников</h2>
            <p className="text-sm text-neutral-600 mt-1">Мониторинг заполненности по этажам</p>
          </div>
          <button
            onClick={() => setShowAddFridge(true)}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Добавить холодильник
          </button>
        </div>

        <div className="overflow-hidden">
          <table className="w-full">
            <thead className="bg-neutral-50 border-b border-neutral-200">
              <tr>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Этаж</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Комнаты</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Вместимость</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Занято</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Заполненность</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Статус</th>
              </tr>
            </thead>
            <tbody>
              {refrigerators.map((fridge) => {
                const percentage = Math.round((fridge.occupied / fridge.capacity) * 100);
                return (
                  <tr key={fridge.id} className="border-b border-neutral-100 hover:bg-neutral-50">
                    <td className="px-6 py-4 text-sm text-neutral-900">{fridge.floor} этаж</td>
                    <td className="px-6 py-4 text-sm text-neutral-600">{fridge.room}</td>
                    <td className="px-6 py-4 text-sm text-neutral-600">{fridge.capacity}</td>
                    <td className="px-6 py-4 text-sm text-neutral-900">{fridge.occupied}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="flex-1 h-2 bg-neutral-100 rounded-full overflow-hidden">
                          <div 
                            className={`h-full ${
                              percentage >= 95 ? 'bg-red-500' : 
                              percentage >= 85 ? 'bg-orange-500' : 
                              'bg-green-500'
                            }`}
                            style={{ width: `${percentage}%` }}
                          />
                        </div>
                        <span className="text-sm text-neutral-600 w-12">{percentage}%</span>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`text-xs px-2 py-1 rounded-full ${getStatusColor(fridge.status)}`}>
                        {getStatusLabel(fridge.status)}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200">
        <div className="p-6 border-b border-neutral-200 flex items-center justify-between">
          <div>
            <h2 className="text-neutral-900">Управление старостами</h2>
            <p className="text-sm text-neutral-600 mt-1">Список старост по этажам</p>
          </div>
          <button
            onClick={() => setShowAddMonitor(true)}
            className="px-4 py-2 border border-neutral-200 rounded-lg text-neutral-600 hover:bg-neutral-50 transition-colors"
          >
            Назначить старосту
          </button>
        </div>

        <div className="overflow-hidden">
          <table className="w-full">
            <thead className="bg-neutral-50 border-b border-neutral-200">
              <tr>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Имя</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Этаж</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Email</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Студентов</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Действия</th>
              </tr>
            </thead>
            <tbody>
              {monitors.map((monitor) => (
                <tr key={monitor.id} className="border-b border-neutral-100 hover:bg-neutral-50">
                  <td className="px-6 py-4 text-sm text-neutral-900">{monitor.name}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{monitor.floor} этаж</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{monitor.email}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{monitor.students}</td>
                  <td className="px-6 py-4">
                    <button className="text-sm text-blue-600 hover:text-blue-700">Редактировать</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200">
        <div className="p-6 border-b border-neutral-200">
          <h2 className="text-neutral-900">Аудит-лог</h2>
          <p className="text-sm text-neutral-600 mt-1">История действий пользователей</p>
        </div>

        <div className="p-6 space-y-4">
          {auditLog.map((log) => (
            <div key={log.id} className="flex items-start gap-4">
              <div className={`p-2 rounded-lg ${getActionIcon(log.type)}`}>
                <FileText className="w-4 h-4" />
              </div>
              <div className="flex-1">
                <p className="text-sm text-neutral-900">{log.action}</p>
                <div className="flex items-center gap-3 mt-1">
                  <span className="text-xs text-neutral-500">{log.user}</span>
                  <span className="text-xs text-neutral-400">•</span>
                  <span className="text-xs text-neutral-500">{log.time}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {showAddFridge && (
        <PortalModal
          title="Добавить холодильник"
          subtitle="Новый холодильник появится в таблице сразу после сохранения."
          onClose={closeFridgeModal}
          maxWidthPx={720}
        >
          <form onSubmit={handleAddFridge} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-2">
                <label className="text-sm text-neutral-600">Этаж</label>
                <input
                  type="number"
                  min={1}
                  value={newFridge.floor}
                  onChange={(e) => setNewFridge({ ...newFridge, floor: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="например, 6"
                  required
                />
              </div>

              <div className="space-y-2 md:col-span-2">
                <label className="text-sm text-neutral-600">Комнаты/секции</label>
                <input
                  type="text"
                  value={newFridge.room}
                  onChange={(e) => setNewFridge({ ...newFridge, room: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="например, 601-605"
                  required
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm text-neutral-600">Вместимость</label>
                <input
                  type="number"
                  min={1}
                  value={newFridge.capacity}
                  onChange={(e) => setNewFridge({ ...newFridge, capacity: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="60"
                  required
                />
              </div>

              <div className="md:col-span-3 flex gap-3 mt-2">
                <button
                  type="submit"
                  className="flex-1 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  Добавить
                </button>
                <button
                  type="button"
                  onClick={closeFridgeModal}
                  className="px-6 py-2 border border-neutral-200 rounded-lg text-neutral-600 hover:bg-neutral-50 transition-colors"
                >
                  Отмена
                </button>
              </div>
            </form>
        </PortalModal>
      )}

      {showAddMonitor && (
        <PortalModal
          title="Назначить старосту"
          subtitle="Староста будет привязан к этажу и появится в списке управления."
          onClose={closeMonitorModal}
          maxWidthPx={720}
        >
          <form onSubmit={handleAddMonitor} className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm text-neutral-600">Имя</label>
                <input
                  type="text"
                  value={newMonitor.name}
                  onChange={(e) => setNewMonitor({ ...newMonitor, name: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Анна Смирнова"
                  required
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm text-neutral-600">Этаж</label>
                <input
                  type="number"
                  min={1}
                  value={newMonitor.floor}
                  onChange={(e) => setNewMonitor({ ...newMonitor, floor: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="например, 6"
                  required
                />
                <p className="text-xs text-neutral-500">Занятые этажи: {[...usedFloors].sort((a, b) => a - b).join(', ') || '—'}</p>
              </div>

              <div className="space-y-2 md:col-span-2">
                <label className="text-sm text-neutral-600">Email</label>
                <input
                  type="email"
                  value={newMonitor.email}
                  onChange={(e) => setNewMonitor({ ...newMonitor, email: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="anna@example.com"
                  required
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm text-neutral-600">Студентов</label>
                <input
                  type="number"
                  min={0}
                  value={newMonitor.students}
                  onChange={(e) => setNewMonitor({ ...newMonitor, students: e.target.value })}
                  className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="45"
                />
              </div>

              <div className="md:col-span-2 flex gap-3 mt-2">
                <button
                  type="submit"
                  className="flex-1 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  Назначить
                </button>
                <button
                  type="button"
                  onClick={closeMonitorModal}
                  className="px-6 py-2 border border-neutral-200 rounded-lg text-neutral-600 hover:bg-neutral-50 transition-colors"
                >
                  Отмена
                </button>
              </div>
            </form>
        </PortalModal>
      )}
    </div>
  );
}
