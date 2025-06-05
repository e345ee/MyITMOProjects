#include <Servo.h>

// Пины для ультразвукового датчика HC-SR04
const int trigPin = 9;
const int echoPin = 10;

// Пин для управления сервоприводом
const int servoPin = 6;

// Константы для расчета
const float g = 9.81;             // Ускорение свободного падения (м/с^2)
const float rho = 1.225;          // Плотность воздуха (кг/м^3)
const float Cd = 1.05;            // Коэффициент аэродинамического сопротивления
const float A = 0.02 * 0.01;      // Площадь поперечного сечения кубика (м^2)
const float m = 0.00158;          // Масса снаряда (кг)
const float k = 93.1;             // Коэффициент упругости (Н/м)
const float x = 0.1;              // Натяжение резинки (м)
const float d_r = 0.095;          // Расстояние от оси до крепления резинки (м)
const float L_r = 0.25;           // Длина рычага (м)

// Переменные для сервопривода
Servo servo;

// Функция для измерения дальности с HC-SR04
float measureDistance() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  long duration = pulseIn(echoPin, HIGH);
  float distance = duration * 0.034 / 2; // Перевод в см
  return distance / 100;                 // Перевод в метры
}

// Функция для вычисления начальной скорости
float calculateInitialVelocity() {
  return sqrt((k * x * x) / m);
}

// Функция для нахождения угла вылета
float calculateLaunchAngle(float R) {
  float v0_squared = (k * x * x) / m;
  float k_d = (rho * Cd * A) / (2 * m);
  float sin_2theta = (R * g) / v0_squared * exp(k_d * R);

  if (sin_2theta > 1 || sin_2theta < -1) {
    return -1; // Угол не может быть вычислен
  }

  return 0.5 * asin(sin_2theta); // Угол в радианах
}

// Функция для вычисления угла блокиратора
float calculateBlockerAngle(float theta) {
  return asin((d_r / L_r) * sin(theta)) * 180 / PI; // Угол в градусах
}

void setup() {
  // Настройка пинов
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  // Инициализация сервопривода
  servo.attach(servoPin);
  servo.write(0); // Начальное положение

  // Инициализация Serial для отладки
  Serial.begin(9600);
}

void loop() {
  // Измерение дальности
  float R = measureDistance();
  Serial.print("Измеренная дальность: ");
  Serial.print(R);
  Serial.println(" м");

  // Вычисление углов
  float theta = calculateLaunchAngle(R);
  if (theta < 0) {
    Serial.println("Невозможно рассчитать угол!");
    delay(1000);
    return;
  }

  float phi = calculateBlockerAngle(theta);

  // Управление сервоприводом
  Serial.print("Угол блокиратора: ");
  Serial.print(phi);
  Serial.println("°");

  servo.write(phi); // Поворот сервопривода на рассчитанный угол

  delay(1000); // Задержка перед следующим измерением
}
