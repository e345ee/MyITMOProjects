import { useMemo, useState } from 'react';
import { toast } from 'sonner';
import { Search, Filter, Trash2, Bell, Download, Check, MoveRight } from 'lucide-react';
import { PortalModal } from './PortalModal';

interface Product {
  id: number;
  name: string;
  shelf: number;
  dateAdded: string;
  expiryDate: string;
  status: 'fresh' | 'expiring' | 'expired';
  owner: string;
}

const mockAllProducts: Product[] = [
  { id: 1, name: 'Молоко 3.2%', shelf: 5, dateAdded: '2025-11-28', expiryDate: '2025-12-05', status: 'expiring', owner: 'Иван Петров' },
  { id: 2, name: 'Сыр российский', shelf: 8, dateAdded: '2025-11-25', expiryDate: '2025-12-10', status: 'fresh', owner: 'Анна Смирнова' },
  { id: 3, name: 'Йогурт', shelf: 3, dateAdded: '2025-11-20', expiryDate: '2025-11-30', status: 'expired', owner: 'Петр Иванов' },
  { id: 4, name: 'Колбаса', shelf: 12, dateAdded: '2025-11-29', expiryDate: '2025-12-08', status: 'fresh', owner: 'Мария Козлова' },
  { id: 5, name: 'Фрукты', shelf: 7, dateAdded: '2025-11-27', expiryDate: '2025-12-03', status: 'expiring', owner: 'Дмитрий Соколов' },
  { id: 6, name: 'Курица', shelf: 15, dateAdded: '2025-11-26', expiryDate: '2025-12-06', status: 'fresh', owner: 'Елена Волкова' },
  { id: 7, name: 'Овощи', shelf: 9, dateAdded: '2025-11-28', expiryDate: '2025-12-04', status: 'expiring', owner: 'Сергей Лебедев' },
  { id: 8, name: 'Масло', shelf: 11, dateAdded: '2025-11-24', expiryDate: '2025-12-15', status: 'fresh', owner: 'Ольга Морозова' },
  { id: 9, name: 'Творог', shelf: 6, dateAdded: '2025-11-22', expiryDate: '2025-12-01', status: 'expired', owner: 'Алексей Новиков' },
  { id: 10, name: 'Сметана', shelf: 14, dateAdded: '2025-11-30', expiryDate: '2025-12-09', status: 'fresh', owner: 'Наталья Попова' },
];

export function AllProductsPage() {
  const [products, setProducts] = useState<Product[]>(mockAllProducts);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | Product['status']>('all');

  // bulk selection
  const [selectedProducts, setSelectedProducts] = useState<number[]>([]);

  // move modal
  const [movingProductId, setMovingProductId] = useState<number | null>(null);
  const [moveToShelfId, setMoveToShelfId] = useState<string>('');

  const filteredProducts = useMemo(() => {
    const q = searchTerm.trim().toLowerCase();
    return products.filter((p) => {
      const matchesSearch =
        q.length === 0 ||
        p.name.toLowerCase().includes(q) ||
        p.owner.toLowerCase().includes(q);
      const matchesStatus = statusFilter === 'all' || p.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [products, searchTerm, statusFilter]);

  const freeShelves = useMemo(() => {
    const TOTAL_SHELVES = 20;
    const used = new Set(products.map((p) => p.shelf));
    const free: number[] = [];
    for (let i = 1; i <= TOTAL_SHELVES; i++) {
      if (!used.has(i)) free.push(i);
    }
    return free;
  }, [products]);

  const toggleSelectProduct = (id: number) => {
    setSelectedProducts((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
  };

  const toggleSelectAll = () => {
    setSelectedProducts((prev) => (prev.length === filteredProducts.length ? [] : filteredProducts.map((p) => p.id)));
  };

  const handleBulkDelete = () => {
    if (selectedProducts.length === 0) return;
    if (!confirm(`Удалить выбранные продукты (${selectedProducts.length})?`)) return;
    setProducts((prev) => prev.filter((p) => !selectedProducts.includes(p.id)));
    setSelectedProducts([]);
    toast.success('Выбранные продукты удалены');
  };

  const handleBulkNotify = () => {
    if (selectedProducts.length === 0) return;
    toast.success(`Уведомления отправлены владельцам (${selectedProducts.length})`);
  };

  const handleNotify = (id: number) => {
    const p = products.find((x) => x.id === id);
    if (!p) return;
    toast.success(`Уведомление отправлено: ${p.owner}`);
  };

  const handleDelete = (id: number) => {
    const p = products.find((x) => x.id === id);
    if (!p) return;
    if (!confirm(`Удалить продукт "${p.name}"?`)) return;
    setProducts((prev) => prev.filter((x) => x.id !== id));
    setSelectedProducts((prev) => prev.filter((x) => x !== id));
    toast.success('Удалено');
  };

  const handleMarkUsed = (id: number) => {
    const p = products.find((x) => x.id === id);
    if (!p) return;
    setProducts((prev) => prev.filter((x) => x.id !== id));
    setSelectedProducts((prev) => prev.filter((x) => x !== id));
    toast.success('Отмечено как использовано');
  };

  const openMoveDialog = (id: number) => {
    setMovingProductId(id);
    setMoveToShelfId('');
  };

  const confirmMove = () => {
    if (movingProductId === null) return;
    const target = Number(moveToShelfId);
    if (!target || !freeShelves.includes(target)) {
      toast.error('Выберите свободную полку');
      return;
    }
    setProducts((prev) => prev.map((p) => (p.id === movingProductId ? { ...p, shelf: target } : p)));
    toast.success('Перемещено');
    setMovingProductId(null);
  };

  const getStatusBadge = (status: Product['status']) => {
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
        <h1 className="text-neutral-900 mb-2">Все продукты</h1>
        <p className="text-neutral-600">Управление всеми продуктами в холодильнике (доступ старосты/администратора)</p>
      </div>

      <div className="flex flex-col md:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-neutral-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Поиск по названию или владельцу..."
            className="w-full pl-10 pr-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="flex gap-2">
          <div className="relative">
            <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-neutral-400" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as any)}
              className="pl-10 pr-8 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white appearance-none"
            >
              <option value="all">Все статусы</option>
              <option value="fresh">Свежие</option>
              <option value="expiring">Истекает срок</option>
              <option value="expired">Просроченные</option>
            </select>
          </div>

          <button
            onClick={() => toast.message('Экспорт пока не реализован в прототипе')}
            className="flex items-center gap-2 px-4 py-2 border border-neutral-200 rounded-lg text-neutral-600 hover:bg-neutral-50"
          >
            <Download className="w-5 h-5" />
            Экспорт
          </button>
        </div>
      </div>

      {selectedProducts.length > 0 && (
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3 bg-blue-50 border border-blue-200 rounded-lg px-6 py-3">
          <span className="text-sm text-blue-900">Выбрано продуктов: {selectedProducts.length}</span>
          <div className="flex gap-2">
            <button
              onClick={handleBulkNotify}
              className="px-4 py-2 rounded-lg border border-blue-200 text-blue-900 hover:bg-blue-100"
            >
              Уведомить владельцев
            </button>
            <button
              onClick={handleBulkDelete}
              className="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700"
            >
              Удалить выбранные
            </button>
          </div>
        </div>
      )}

      <div className="bg-white rounded-xl border border-neutral-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-neutral-50">
            <tr>
              <th className="p-4 text-left">
                <input
                  type="checkbox"
                  checked={filteredProducts.length > 0 && selectedProducts.length === filteredProducts.length}
                  onChange={toggleSelectAll}
                  className="w-4 h-4"
                  aria-label="Выбрать все"
                />
              </th>
              <th className="p-4 text-left text-sm font-medium text-neutral-600">Продукт</th>
              <th className="p-4 text-left text-sm font-medium text-neutral-600">Владелец</th>
              <th className="p-4 text-left text-sm font-medium text-neutral-600">Полка</th>
              <th className="p-4 text-left text-sm font-medium text-neutral-600">Срок годности</th>
              <th className="p-4 text-left text-sm font-medium text-neutral-600">Статус</th>
              <th className="p-4 text-left text-sm font-medium text-neutral-600">Действия</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.map((product) => (
              <tr key={product.id} className="border-t border-neutral-200 hover:bg-neutral-50">
                <td className="p-4">
                  <input
                    type="checkbox"
                    checked={selectedProducts.includes(product.id)}
                    onChange={() => toggleSelectProduct(product.id)}
                    className="w-4 h-4"
                    aria-label={`Выбрать ${product.name}`}
                  />
                </td>
                <td className="p-4">
                  <div className="font-medium text-neutral-900">{product.name}</div>
                  <div className="text-sm text-neutral-600">Добавлен: {product.dateAdded}</div>
                </td>
                <td className="p-4 text-neutral-900">{product.owner}</td>
                <td className="p-4 text-neutral-900">#{product.shelf}</td>
                <td className="p-4 text-neutral-900">{product.expiryDate}</td>
                <td className="p-4">{getStatusBadge(product.status)}</td>
                <td className="p-4">
                  <div className="flex gap-1">
                    <button
                      onClick={() => handleMarkUsed(product.id)}
                      title="Использовано"
                      className="p-2 rounded-lg text-green-600 hover:bg-green-50 transition-colors"
                    >
                      <Check className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => openMoveDialog(product.id)}
                      title="Переместить"
                      className="p-2 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors"
                    >
                      <MoveRight className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleNotify(product.id)}
                      title="Уведомить"
                      className="p-2 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors"
                    >
                      <Bell className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(product.id)}
                      title="Удалить"
                      className="p-2 rounded-lg text-red-600 hover:bg-red-50 transition-colors"
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

      {filteredProducts.length === 0 && (
        <div className="text-center py-12 bg-white rounded-xl border border-neutral-200">
          <p className="text-neutral-600">Продукты не найдены</p>
        </div>
      )}

      {movingProductId !== null && (
        <PortalModal
          title="Переместить продукт"
          subtitle="Выберите свободную полку для перемещения."
          onClose={() => setMovingProductId(null)}
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
              onClick={() => setMovingProductId(null)}
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
