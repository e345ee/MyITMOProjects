import { User, Mail, Shield, LogOut, Key } from 'lucide-react';
import type { UserRole } from '../App';

interface ProfilePageProps {
  userRole: UserRole;
  onRoleChange: (role: UserRole) => void;
}

export function ProfilePage({ userRole, onRoleChange }: ProfilePageProps) {
  const getRoleLabel = (role: UserRole) => {
    switch (role) {
      case 'student':
        return 'Студент';
      case 'monitor':
        return 'Староста';
      case 'admin':
        return 'Администратор';
      default:
        return 'Неизвестно';
    }
  };

  const getRoleBadgeColor = (role: UserRole) => {
    switch (role) {
      case 'student':
        return 'bg-blue-100 text-blue-700';
      case 'monitor':
        return 'bg-purple-100 text-purple-700';
      case 'admin':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-neutral-100 text-neutral-700';
    }
  };

  return (
    <div className="space-y-6 max-w-3xl">
      <div>
        <h1 className="text-neutral-900 mb-2">Профиль пользователя</h1>
        <p className="text-neutral-600">Управление вашим профилем и настройками</p>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200 p-8">
        <div className="flex items-start justify-between mb-8">
          <div className="flex items-center gap-4">
            <div className="w-20 h-20 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
              <User className="w-10 h-10 text-white" />
            </div>
            <div>
              <h2 className="text-neutral-900 mb-1">Иван Петров</h2>
              <span className={`text-xs px-3 py-1 rounded-full ${getRoleBadgeColor(userRole)}`}>
                {getRoleLabel(userRole)}
              </span>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <label className="text-sm text-neutral-600 flex items-center gap-2">
                <User className="w-4 h-4" />
                Имя
              </label>
              <input
                type="text"
                defaultValue="Иван Петров"
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-neutral-600 flex items-center gap-2">
                <Mail className="w-4 h-4" />
                Email
              </label>
              <input
                type="email"
                defaultValue="ivan.petrov@example.com"
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-neutral-600 flex items-center gap-2">
                <Shield className="w-4 h-4" />
                Роль
              </label>
              <select
                value={userRole}
                onChange={(e) => onRoleChange(e.target.value as UserRole)}
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              >
                <option value="student">Студент</option>
                <option value="monitor">Староста</option>
                <option value="admin">Администратор</option>
              </select>
              <p className="text-xs text-neutral-500 mt-1">
                Переключайте роль для просмотра разных возможностей системы
              </p>
            </div>

            <div className="space-y-2">
              <label className="text-sm text-neutral-600">Комната</label>
              <input
                type="text"
                defaultValue="301"
                className="w-full px-4 py-2 border border-neutral-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="pt-6 border-t border-neutral-200">
            <button className="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              Сохранить изменения
            </button>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200 p-8">
        <h3 className="text-neutral-900 mb-4">Безопасность</h3>
        
        <div className="space-y-4">
          <button className="w-full flex items-center justify-between p-4 border border-neutral-200 rounded-lg hover:bg-neutral-50 transition-colors">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-blue-50 rounded-lg">
                <Key className="w-5 h-5 text-blue-600" />
              </div>
              <div className="text-left">
                <p className="text-sm text-neutral-900">Сменить пароль</p>
                <p className="text-xs text-neutral-500">Обновите ваш пароль для безопасности</p>
              </div>
            </div>
            <span className="text-neutral-400">→</span>
          </button>

          <button className="w-full flex items-center justify-between p-4 border border-red-200 rounded-lg hover:bg-red-50 transition-colors text-red-600">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-red-50 rounded-lg">
                <LogOut className="w-5 h-5 text-red-600" />
              </div>
              <div className="text-left">
                <p className="text-sm">Выйти из аккаунта</p>
                <p className="text-xs text-red-500">Завершить текущую сессию</p>
              </div>
            </div>
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-neutral-200 p-8">
        <h3 className="text-neutral-900 mb-4">Статистика</h3>
        
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div>
            <p className="text-sm text-neutral-600 mb-1">Всего продуктов</p>
            <p className="text-neutral-900">8</p>
          </div>
          <div>
            <p className="text-sm text-neutral-600 mb-1">Активных</p>
            <p className="text-neutral-900">6</p>
          </div>
          <div>
            <p className="text-sm text-neutral-600 mb-1">Использовано</p>
            <p className="text-neutral-900">24</p>
          </div>
          <div>
            <p className="text-sm text-neutral-600 mb-1">Дней в системе</p>
            <p className="text-neutral-900">45</p>
          </div>
        </div>
      </div>
    </div>
  );
}
