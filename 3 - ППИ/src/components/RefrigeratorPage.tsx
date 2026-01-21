import { useEffect, useMemo, useRef, useState } from 'react';
import { toast } from 'sonner';
import { User, Calendar, Clock, Check, Trash2, MoveRight } from 'lucide-react';
import { PortalModal } from './PortalModal';

type ViewMode = 'visual' | 'list';

interface Shelf {
  id: number;
  owner: string;
  product: string;
  dateAdded: string;
  expiryDate: string;
  status: 'fresh' | 'expiring' | 'expired';
}

const mockShelves: Shelf[] = [
  { id: 1, owner: 'Иван Петров', product: 'Молоко', dateAdded: '2025-11-28', expiryDate: '2025-12-05', status: 'expiring' },
  { id: 2, owner: 'Анна Смирнова', product: 'Сыр', dateAdded: '2025-11-25', expiryDate: '2025-12-10', status: 'fresh' },
  { id: 3, owner: 'Петр Иванов', product: 'Йогурт', dateAdded: '2025-11-20', expiryDate: '2025-11-30', status: 'expired' },
  { id: 4, owner: 'Мария Козлова', product: 'Колбаса', dateAdded: '2025-11-29', expiryDate: '2025-12-08', status: 'fresh' },
  { id: 5, owner: 'Дмитрий Соколов', product: 'Фрукты', dateAdded: '2025-11-27', expiryDate: '2025-12-03', status: 'expiring' },
  { id: 6, owner: '', product: '', dateAdded: '', expiryDate: '', status: 'fresh' },
  { id: 7, owner: 'Елена Волкова', product: 'Курица', dateAdded: '2025-11-26', expiryDate: '2025-12-06', status: 'fresh' },
  { id: 8, owner: '', product: '', dateAdded: '', expiryDate: '', status: 'fresh' },
  { id: 9, owner: 'Сергей Лебедев', product: 'Овощи', dateAdded: '2025-11-28', expiryDate: '2025-12-04', status: 'expiring' },
  { id: 10, owner: '', product: '', dateAdded: '', expiryDate: '', status: 'fresh' },
  { id: 11, owner: 'Ольга Морозова', product: 'Масло', dateAdded: '2025-11-24', expiryDate: '2025-12-15', status: 'fresh' },
  { id: 12, owner: '', product: '', dateAdded: '', expiryDate: '', status: 'fresh' },
];

export function RefrigeratorPage({
  highlightShelfId,
}: {
  highlightShelfId?: number | null;
}) {
  const [viewMode, setViewMode] = useState<ViewMode>('visual');
  const [shelves, setShelves] = useState<Shelf[]>(mockShelves);
  const [movingShelfId, setMovingShelfId] = useState<number | null>(null);
  const [moveToShelfId, setMoveToShelfId] = useState<string>('');

  const highlightRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!highlightShelfId) return;
    // В прототипе проще показать визуальную схему и подсветить нужную полку.
    setViewMode('visual');
    // Дадим React нарисовать DOM.
    const t = window.setTimeout(() => {
      highlightRef.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 0);
    return () => window.clearTimeout(t);
  }, [highlightShelfId]);

  const freeShelves = useMemo(
    () => shelves.filter((s) => !s.owner).map((s) => s.id),
    [shelves]
  );

  const clearShelf = (id: number) => {
    setShelves((prev) =>
      prev.map((s) =>
        s.id === id
          ? { ...s, owner: '', product: '', dateAdded: '', expiryDate: '', status: 'fresh' }
          : s
      )
    );
  };

  const handleUse = (id: number) => {
    const shelf = shelves.find((s) => s.id === id);
    if (!shelf || !shelf.owner) return;
    clearShelf(id);
    toast.success(`Отмечено как использованное: ${shelf.product} (полка #${id})`);
  };

  const handleDelete = (id: number) => {
    const shelf = shelves.find((s) => s.id === id);
    if (!shelf || !shelf.owner) return;
    const ok = window.confirm(`Удалить продукт "${shelf.product}" с полки #${id}?`);
    if (!ok) return;
    clearShelf(id);
    toast.success(`Удалено: ${shelf.product} (полка #${id})`);
  };

  const openMoveDialog = (id: number) => {
    const shelf = shelves.find((s) => s.id === id);
    if (!shelf || !shelf.owner) return;
    setMovingShelfId(id);
    setMoveToShelfId('');
  };

  const confirmMove = () => {
    if (movingShelfId === null) return;
    const target = Number(moveToShelfId);
    if (!target || !freeShelves.includes(target)) {
      toast.error('Выберите свободную полку');
      return;
    }
    const from = shelves.find((s) => s.id === movingShelfId);
    if (!from || !from.owner) {
      setMovingShelfId(null);
      return;
    }
    setShelves((prev) =>
      prev.map((s) => {
        if (s.id === target) return { ...s, ...from, id: target };
        if (s.id === movingShelfId) return { ...s, owner: '', product: '', dateAdded: '', expiryDate: '', status: 'fresh' };
        return s;
      })
    );
    toast.success(`Перемещено: ${from.product} → полка #${target}`);
    setMovingShelfId(null);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'fresh':
        return 'border-green-200 bg-green-50';
      case 'expiring':
        return 'border-orange-200 bg-orange-50';
      case 'expired':
        return 'border-red-200 bg-red-50';
      default:
        return 'border-neutral-200 bg-white';
    }
  };

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
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-neutral-900 mb-2">Холодильник</h1>
          <p className="text-neutral-600">Управление полками и продуктами</p>
        </div>
        
        <div className="flex gap-2 bg-neutral-100 p-1 rounded-lg">
          <button
            onClick={() => setViewMode('visual')}
            className={`px-4 py-2 rounded-lg text-sm transition-colors ${
              viewMode === 'visual'
                ? 'bg-white text-neutral-900 shadow-sm'
                : 'text-neutral-600 hover:text-neutral-900'
            }`}
          >
            Визуальная схема
          </button>
          <button
            onClick={() => setViewMode('list')}
            className={`px-4 py-2 rounded-lg text-sm transition-colors ${
              viewMode === 'list'
                ? 'bg-white text-neutral-900 shadow-sm'
                : 'text-neutral-600 hover:text-neutral-900'
            }`}
          >
            Список
          </button>
        </div>
      </div>

      {viewMode === 'visual' ? (
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {shelves.map((shelf) => (
            <div
              key={shelf.id}
              ref={highlightShelfId === shelf.id ? (el) => (highlightRef.current = el) : undefined}
              className={`border rounded-xl p-4 transition-all hover:shadow-md ${
                shelf.owner ? getStatusColor(shelf.status) : 'border-dashed border-neutral-300 bg-neutral-50'
              } ${highlightShelfId === shelf.id ? 'ring-2 ring-blue-500 ring-offset-2' : ''}`}
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-sm text-neutral-500">Полка #{shelf.id}</span>
                {shelf.owner && getStatusBadge(shelf.status)}
              </div>

              {shelf.owner ? (
                <div className="space-y-3">
                  <div>
                    <p className="text-neutral-900 mb-1">{shelf.product}</p>
                  </div>

                  <div className="space-y-2 text-sm">
                    <div className="flex items-center gap-2 text-neutral-600">
                      <User className="w-4 h-4" />
                      <span>{shelf.owner}</span>
                    </div>
                    <div className="flex items-center gap-2 text-neutral-600">
                      <Calendar className="w-4 h-4" />
                      <span>{shelf.dateAdded}</span>
                    </div>
                    <div className="flex items-center gap-2 text-neutral-600">
                      <Clock className="w-4 h-4" />
                      <span>{shelf.expiryDate}</span>
                    </div>
                  </div>

                  <div className="flex gap-2 pt-2 border-t border-neutral-200">
                    <button
                      onClick={() => handleUse(shelf.id)}
                      title="Использовать"
                      className="flex-1 p-2 rounded-lg bg-green-100 text-green-700 hover:bg-green-200 transition-colors"
                    >
                      <Check className="w-4 h-4 mx-auto" />
                    </button>
                    <button
                      onClick={() => openMoveDialog(shelf.id)}
                      title="Переместить"
                      className="flex-1 p-2 rounded-lg bg-blue-100 text-blue-700 hover:bg-blue-200 transition-colors"
                    >
                      <MoveRight className="w-4 h-4 mx-auto" />
                    </button>
                    <button
                      onClick={() => handleDelete(shelf.id)}
                      title="Удалить"
                      className="flex-1 p-2 rounded-lg bg-red-100 text-red-700 hover:bg-red-200 transition-colors"
                    >
                      <Trash2 className="w-4 h-4 mx-auto" />
                    </button>
                  </div>
                </div>
              ) : (
                <div className="text-center py-8">
                  <p className="text-sm text-neutral-400">Свободна</p>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-neutral-200 overflow-hidden">
          <table className="w-full">
            <thead className="bg-neutral-50 border-b border-neutral-200">
              <tr>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Полка</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Продукт</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Владелец</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Дата добавления</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Срок годности</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Статус</th>
                <th className="text-left px-6 py-3 text-sm text-neutral-600">Действия</th>
              </tr>
            </thead>
            <tbody>
              {shelves.filter(shelf => shelf.owner).map((shelf) => (
                <tr key={shelf.id} className="border-b border-neutral-100 hover:bg-neutral-50">
                  <td className="px-6 py-4 text-sm text-neutral-900">#{shelf.id}</td>
                  <td className="px-6 py-4 text-sm text-neutral-900">{shelf.product}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{shelf.owner}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{shelf.dateAdded}</td>
                  <td className="px-6 py-4 text-sm text-neutral-600">{shelf.expiryDate}</td>
                  <td className="px-6 py-4 text-sm">{getStatusBadge(shelf.status)}</td>
                  <td className="px-6 py-4">
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleUse(shelf.id)}
                        title="Использовать"
                        className="p-2 rounded-lg text-green-600 hover:bg-green-50"
                      >
                        <Check className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => openMoveDialog(shelf.id)}
                        title="Переместить"
                        className="p-2 rounded-lg text-blue-600 hover:bg-blue-50"
                      >
                        <MoveRight className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(shelf.id)}
                        title="Удалить"
                        className="p-2 rounded-lg text-red-600 hover:bg-red-50"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {movingShelfId !== null && (
        <PortalModal
          title="Переместить продукт"
          subtitle="Выберите свободную полку для перемещения."
          onClose={() => setMovingShelfId(null)}
          maxWidthPx={560}
        >
          {freeShelves.length === 0 ? (
            <div
              style={{
                fontSize: 14,
                color: '#6b7280',
                background: '#f9fafb',
                border: '1px solid #e5e7eb',
                borderRadius: 12,
                padding: 14,
              }}
            >
              Нет свободных полок 😬
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <label style={{ fontSize: 14, color: '#6b7280' }}>Свободная полка</label>
              <select
                value={moveToShelfId}
                onChange={(e) => setMoveToShelfId(e.target.value)}
                style={{
                  width: '100%',
                  padding: '10px 12px',
                  border: '1px solid #e5e7eb',
                  borderRadius: 12,
                  outline: 'none',
                  fontSize: 16,
                }}
              >
                <option value="">Выберите…</option>
                {freeShelves.map((id) => (
                  <option key={id} value={id}>
                    #{id}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div style={{ display: 'flex', gap: 12, marginTop: 18 }}>
            <button
              onClick={confirmMove}
              disabled={freeShelves.length === 0 || !moveToShelfId}
              style={{
                flex: 1,
                background: '#2563eb',
                color: '#fff',
                border: 'none',
                borderRadius: 12,
                padding: '12px 16px',
                fontSize: 16,
                fontWeight: 600,
                cursor: 'pointer',
                opacity: freeShelves.length === 0 || !moveToShelfId ? 0.6 : 1,
              }}
            >
              Переместить
            </button>
            <button
              onClick={() => setMovingShelfId(null)}
              style={{
                background: '#fff',
                color: '#374151',
                border: '1px solid #e5e7eb',
                borderRadius: 12,
                padding: '12px 16px',
                fontSize: 16,
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              Отмена
            </button>
          </div>
        </PortalModal>
      )}
    </div>
  );
}
