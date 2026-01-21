import { useMemo, useState } from 'react';
import { toast } from 'sonner';
import { Plus, Check, Trash2, MoveRight } from 'lucide-react';
import { PortalModal } from './PortalModal';

interface Product {
  id: number;
  name: string;
  shelf: number;
  dateAdded: string;
  expiryDate: string;
  status: 'fresh' | 'expiring' | 'expired';
}

const mockProducts: Product[] = [
  { id: 1, name: 'Молоко 3.2%', shelf: 5, dateAdded: '2025-11-28', expiryDate: '2025-12-05', status: 'expiring' },
  { id: 2, name: 'Йогурт фруктовый', shelf: 12, dateAdded: '2025-11-29', expiryDate: '2025-12-10', status: 'fresh' },
  { id: 3, name: 'Сыр российский', shelf: 8, dateAdded: '2025-11-26', expiryDate: '2025-12-15', status: 'fresh' },
  { id: 4, name: 'Колбаса вареная', shelf: 3, dateAdded: '2025-11-20', expiryDate: '2025-11-30', status: 'expired' },
  { id: 5, name: 'Яйца (10 шт)', shelf: 15, dateAdded: '2025-11-27', expiryDate: '2025-12-12', status: 'fresh' },
  { id: 6, name: 'Масло сливочное', shelf: 7, dateAdded: '2025-11-28', expiryDate: '2025-12-20', status: 'fresh' },
  { id: 7, name: 'Творог 9%', shelf: 11, dateAdded: '2025-11-29', expiryDate: '2025-12-06', status: 'expiring' },
  { id: 8, name: 'Сметана 20%', shelf: 9, dateAdded: '2025-11-30', expiryDate: '2025-12-08', status: 'fresh' },
];

export function MyProductsPage() {
  const [products, setProducts] = useState<Product[]>(mockProducts);
  const [showAddForm, setShowAddForm] = useState(false);
  const [movingProductId, setMovingProductId] = useState<number | null>(null);
  const [moveToShelf, setMoveToShelf] = useState<string>('');
  const [newProduct, setNewProduct] = useState({
    name: '',
    shelf: '',
    dateAdded: '',
    expiryDate: '',
  });

  const todayISO = () => new Date().toISOString().slice(0, 10);

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

  const handleAddProduct = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newProduct.name.trim()) {
      toast.error('Введите название продукта');
      return;
    }
    if (!newProduct.shelf) {
      toast.error('Укажите номер полки');
      return;
    }
    if (!newProduct.dateAdded) {
      toast.error('Укажите дату добавления');
      return;
    }
    if (!newProduct.expiryDate) {
      toast.error('Укажите срок годности');
      return;
    }
    if (newProduct.expiryDate < newProduct.dateAdded) {
      toast.error('Срок годности не может быть раньше даты добавления');
      return;
    }
    const product: Product = {
      id: products.length + 1,
      name: newProduct.name,
      shelf: parseInt(newProduct.shelf),
      dateAdded: newProduct.dateAdded,
      expiryDate: newProduct.expiryDate,
      status: 'fresh',
    };
    setProducts([...products, product]);
    setNewProduct({ name: '', shelf: '', dateAdded: '', expiryDate: '' });
    setShowAddForm(false);
    toast.success(`Добавлено: ${product.name}`);
  };

  const closeAddProductModal = () => {
    setShowAddForm(false);
    setNewProduct({ name: '', shelf: '', dateAdded: '', expiryDate: '' });
  };

  const handleDeleteProduct = (id: number) => {
    setProducts(products.filter(p => p.id !== id));
  };

  const handleUseProduct = (id: number) => {
    const product = products.find((p) => p.id === id);
    if (!product) return;
    setProducts((prev) => prev.filter((p) => p.id !== id));
    toast.success(`Отмечено как использованное: ${product.name}`);
  };

  const openMoveDialog = (id: number) => {
    setMovingProductId(id);
    setMoveToShelf('');
  };

  const usedShelves = useMemo(() => new Set(products.map((p) => p.shelf)), [products]);

  const confirmMove = () => {
    if (movingProductId === null) return;
    const target = Number(moveToShelf);
    if (!target || target < 1) {
      toast.error('Введите корректный номер полки');
      return;
    }
    if (usedShelves.has(target)) {
      toast.error('Эта полка уже занята вашим продуктом');
      return;
    }
    setProducts((prev) =>
      prev.map((p) => (p.id === movingProductId ? { ...p, shelf: target } : p))
    );
    toast.success(`Перемещено на полку #${target}`);
    setMovingProductId(null);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-neutral-900 mb-2">Мои продукты</h1>
          <p className="text-neutral-600">Управление вашими продуктами в холодильнике</p>
        </div>
        
        <button
          onClick={() => {
            setNewProduct((prev) => ({
              ...prev,
              dateAdded: prev.dateAdded || todayISO(),
              expiryDate: prev.expiryDate || todayISO(),
            }));
            setShowAddForm(true);
          }}
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Добавить продукт
        </button>
      </div>

      {showAddForm && (
        <PortalModal
          title="Добавить продукт"
          subtitle="Заполните данные — продукт появится в списке сразу после сохранения."
          onClose={closeAddProductModal}
          maxWidthPx={640}
        >
          <form onSubmit={handleAddProduct} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm text-neutral-600">Название продукта</label>
              <input
                type="text"
                value={newProduct.name}
                onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Молоко 3.2%"
                autoFocus
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-neutral-600">Номер полки</label>
              <input
                type="number"
                value={newProduct.shelf}
                onChange={(e) => setNewProduct({ ...newProduct, shelf: e.target.value })}
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="1-60"
                min="1"
                max="60"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-neutral-600">Дата добавления</label>
              <input
                type="date"
                value={newProduct.dateAdded}
                onChange={(e) => setNewProduct({ ...newProduct, dateAdded: e.target.value })}
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-neutral-600">Срок годности</label>
              <input
                type="date"
                value={newProduct.expiryDate}
                onChange={(e) => setNewProduct({ ...newProduct, expiryDate: e.target.value })}
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="md:col-span-2 flex gap-3 pt-2">
              <button
                type="submit"
                className="flex-1 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
              >
                Добавить
              </button>
              <button
                type="button"
                onClick={closeAddProductModal}
                className="px-6 py-2 border border-neutral-200 rounded-lg text-neutral-600 hover:bg-neutral-50 transition-colors"
              >
                Отмена
              </button>
            </div>
          </form>
        </PortalModal>
      )}

      <div className="bg-white rounded-xl border border-neutral-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-neutral-50 border-b border-neutral-200">
            <tr>
              <th className="text-left px-6 py-3 text-sm text-neutral-600">Продукт</th>
              <th className="text-left px-6 py-3 text-sm text-neutral-600">Полка</th>
              <th className="text-left px-6 py-3 text-sm text-neutral-600">Дата добавления</th>
              <th className="text-left px-6 py-3 text-sm text-neutral-600">Срок годности</th>
              <th className="text-left px-6 py-3 text-sm text-neutral-600">Статус</th>
              <th className="text-left px-6 py-3 text-sm text-neutral-600">Действия</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id} className="border-b border-neutral-100 hover:bg-neutral-50">
                <td className="px-6 py-4 text-sm text-neutral-900">{product.name}</td>
                <td className="px-6 py-4 text-sm text-neutral-600">#{product.shelf}</td>
                <td className="px-6 py-4 text-sm text-neutral-600">{product.dateAdded}</td>
                <td className="px-6 py-4 text-sm text-neutral-600">{product.expiryDate}</td>
                <td className="px-6 py-4 text-sm">{getStatusBadge(product.status)}</td>
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    <button
                      title="Использовать"
                      onClick={() => handleUseProduct(product.id)}
                      className="p-2 rounded-lg text-green-600 hover:bg-green-50 transition-colors"
                    >
                      <Check className="w-4 h-4" />
                    </button>
                    <button
                      title="Переместить"
                      onClick={() => openMoveDialog(product.id)}
                      className="p-2 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors"
                    >
                      <MoveRight className="w-4 h-4" />
                    </button>
                    <button
                      title="Удалить"
                      onClick={() => handleDeleteProduct(product.id)}
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

      {products.length === 0 && (
        <div className="text-center py-12 bg-white rounded-xl border border-neutral-200">
          <p className="text-neutral-600">У вас пока нет продуктов</p>
          <button
            onClick={() => setShowAddForm(true)}
            className="mt-4 text-blue-600 hover:text-blue-700"
          >
            Добавить первый продукт
          </button>
        </div>
      )}

      {movingProductId !== null && (
        <PortalModal
          title="Переместить продукт"
          subtitle="Введите новый номер полки."
          onClose={() => setMovingProductId(null)}
          maxWidthPx={560}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            <label style={{ fontSize: 14, color: '#6b7280' }}>Новая полка</label>
            <input
              type="number"
              min={1}
              value={moveToShelf}
              onChange={(e) => setMoveToShelf(e.target.value)}
              placeholder="например, 12"
              style={{
                width: '100%',
                padding: '10px 12px',
                border: '1px solid #e5e7eb',
                borderRadius: 12,
                outline: 'none',
                fontSize: 16,
              }}
            />
            <div style={{ fontSize: 12, color: '#6b7280', lineHeight: 1.4 }}>
              Подсказка: сейчас занятые вами полки —{' '}
              {[...usedShelves].sort((a, b) => a - b).map((s) => `#${s}`).join(', ')}
            </div>
          </div>

          <div style={{ display: 'flex', gap: 12, marginTop: 18 }}>
            <button
              onClick={confirmMove}
              disabled={!moveToShelf}
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
                opacity: !moveToShelf ? 0.6 : 1,
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
