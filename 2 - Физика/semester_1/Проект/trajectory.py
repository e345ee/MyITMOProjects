 import numpy as np
 import matplotlib.pyplot as plt
 from scipy.optimize import fsolve
 import math

 # Константы
 g = 9.81 # Ускорение свободного падения (м/с^2)
 rho = 1.225 # Плотность воздуха (кг/м^3)
 Cd = 1.05 # Коэффициент аэродинамического сопротивления для кубика
 A = 0.02 * 0.01 # Площадь поперечного сечения кубика (м^2)
 m = 0.00158 # Масса снаряда (кг)
 k = 93.1 # Коэффициент упругости (Н/м)
 x = 0.1 # Натяжение резинки (м)
 d_r = 0.095 # Расстояние от оси до точки крепления резинки (м)
 L_r = 0.25 # Длина рычага (м)

 # Начальная скорость, вычисляемая из энергии натяжения резинки
 v0 = np.sqrt(k * x**2 / m)

 # Функция силы сопротивления воздуха
 def drag_force(v):
 return 0.5 * rho * Cd * A * v**2

 # Симуляция движения и возврат траектории
 def simulate_motion_with_trajectory(v0, theta, dt=0.001, max_time=10):
 vx = v0 * np.cos(theta)
 vy = v0 * np.sin(theta)
 x, y = 0, 0

 x_positions, y_positions = [x], [y]

 while y >= 0:
 v = np.sqrt(vx**2 + vy**2)
 Fd = drag_force(v)

 ax = -Fd * (vx / v) / m
 ay = -g - (Fd * (vy / v) / m)

 vx += ax * dt
 vy += ay * dt

 x += vx * dt
 y += vy * dt

 x_positions.append(x)
 y_positions.append(y)

 if x > 100 or y < -100:
 break

 return x_positions, y_positions

 # Поиск угла вылета для заданной дальности
 def find_theta_for_range(R, v0, tolerance=0.001):
 def difference(theta):
 x, _ = simulate_motion_with_trajectory(v0, theta)
 return x[-1] - R

 theta = fsolve(difference, np.radians(45))[0]
 return theta

 # Построение траектории для заданной дальности
 def plot_trajectory_for_range(R):
 theta = find_theta_for_range(R, v0)
 x_positions, y_positions = simulate_motion_with_trajectory(v0, theta)

 plt.figure(figsize=(10, 6))
 plt.plot(x_positions, y_positions, label=f"Траектория для R={R} м")
 plt.xlabel("Горизонтальное расстояние (м)", fontsize=12)
 plt.ylabel("Вертикальное расстояние (м)", fontsize=12)
 plt.title("Движение снаряда с учетом сопротивления воздуха", fontsize=14)
 plt.grid(True)
 plt.legend()
 plt.show()

 return theta

 # Вычисление угла блокиратора
 def calculate_phi(R):
 theta = find_theta_for_range(R, v0)
 phi = math.asin((d_r / L_r) * math.sin(theta))
 return math.degrees(phi)

# Пример использования
R = float(input("Введите дальность: ")) # Дальность в метрах
 theta = plot_trajectory_for_range(R)
 phi = calculate_phi(R)
 print(f"Угол блокиратора (phi) для R={R} м: {phi:.2f}°")
 print(f"Угол вылета (theta) для R={R} м: {np.degrees(theta):.2f}°")