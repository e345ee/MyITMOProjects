#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

// Инициализация PCA9685
Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();

// Константы для MG996R (0.5–2.5 мс)
#define MG996R_MIN 125 // 0.5 мс = 125
#define MG996R_MAX 625 // 2.5 мс = 625

// Константы для MG90S (1.0–2.0 мс)
#define MG90S_MIN 250 // 1.0 мс = 250
#define MG90S_MAX 500 // 2.0 мс = 500

// Каналы на PCA9685
#define SG90_CHANNEL 0 // MG90S
#define MG996R_CHANNEL 1 // MG996R
#define SERVO3_CHANNEL 2 // MG996R

#define SG90_MIN 150       
#define SG90_MAX 600       
#define MG996R_MIN 125     
#define MG996R_MAX 625

void setup() {
  pwm.begin();
  pwm.setPWMFreq(50); 
  pwm.setPWM(SERVO3_CHANNEL, 0, angleToPulse(45, MG996R_MIN, MG996R_MAX));  
  pwm.setPWM(MG996R_CHANNEL, 0, angleToPulse(150, MG996R_MIN, MG996R_MAX));  
  pwm.setPWM(SG90_CHANNEL, 0, angleToPulse(90, SG90_MIN, SG90_MAX)); 
  
}

//Программу никто не сохранил, если найду, исправлю

  void loop() {
  
  pwm.setPWM(SG90_CHANNEL, 0, angleToPulse(90, SG90_MIN, SG90_MAX));  
  delay(3000);

  pwm.setPWM(MG996R_CHANNEL, 0, angleToPulse(150, MG996R_MIN, MG996R_MAX));  
  delay(2000);

  pwm.setPWM(SG90_CHANNEL, 0, angleToPulse(0, SG90_MIN, SG90_MAX));  
  delay(3000);


  pwm.setPWM(MG996R_CHANNEL, 0, angleToPulse(0, MG996R_MIN, MG996R_MAX)); 
  delay(1000); 
  
}

int angleToPulse(int angle, int minPulse, int maxPulse) {
  return map(angle, 0, 180, minPulse, maxPulse);
}





