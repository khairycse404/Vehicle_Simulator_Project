#include <Servo.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

Servo speedServo;
LiquidCrystal_I2C lcd(0x27, 16, 2);

const int servoPin = 9;

const int stopButton = 11;
const int incOrDownButton = 10;
const int decOrUpButton = 6;
const int nextButton = 2;

int currentServoAngle = 0;
int targetServoAngle = 0;

String oldLine1 = "";
String oldLine2 = "";

unsigned long lastSend11 = 0;
unsigned long lastSend10 = 0;
unsigned long lastSend6 = 0;
unsigned long lastSend2 = 0;

const int holdDelay = 180;

void setup() {
  Serial.begin(9600);

  pinMode(stopButton, INPUT);
  pinMode(incOrDownButton, INPUT);
  pinMode(decOrUpButton, INPUT);
  pinMode(nextButton, INPUT);

  speedServo.attach(servoPin);
  speedServo.write(0);

  lcd.init();
  lcd.backlight();

  printLCD("Hardware Ready", "Waiting ROS...");
}

void loop() {
  readButtons();
  readSerialCommands();
  moveServoSmooth();
  delay(20);
}

void readButtons() {
  checkHoldButton(stopButton, 11, lastSend11);
  checkHoldButton(incOrDownButton, 10, lastSend10);
  checkHoldButton(decOrUpButton, 6, lastSend6);
  checkHoldButton(nextButton, 2, lastSend2);
}

void checkHoldButton(int pin, int buttonNumber, unsigned long &lastSendTime) {
  int state = digitalRead(pin);

  if (state == HIGH) {
    if (millis() - lastSendTime >= holdDelay) {
      Serial.print("BTN:");
      Serial.println(buttonNumber);
      lastSendTime = millis();
    }
  }
}

void readSerialCommands() {
  if (!Serial.available()) return;

  String command = Serial.readStringUntil('\n');
  command.trim();

  if (command.startsWith("LCD:")) {
    String msg = command.substring(4);

    int sep = msg.indexOf('|');
    if (sep == -1) return;

    String line1 = msg.substring(0, sep);
    String line2 = msg.substring(sep + 1);

    printLCD(line1, line2);
  }

  else if (command.startsWith("SERVO:")) {
    int angle = command.substring(6).toInt();

    if (angle < 0) angle = 0;
    if (angle > 180) angle = 180;

    targetServoAngle = angle;
  }
}

void printLCD(String line1, String line2) {
  line1 = fit16(line1);
  line2 = fit16(line2);

  if (line1 != oldLine1) {
    lcd.setCursor(0, 0);
    lcd.print(line1);
    oldLine1 = line1;
  }

  if (line2 != oldLine2) {
    lcd.setCursor(0, 1);
    lcd.print(line2);
    oldLine2 = line2;
  }
}

void moveServoSmooth() {
  if (currentServoAngle < targetServoAngle) {
    currentServoAngle++;
    speedServo.write(currentServoAngle);
  }
  else if (currentServoAngle > targetServoAngle) {
    currentServoAngle--;
    speedServo.write(currentServoAngle);
  }
}

String fit16(String text) {
  if (text.length() > 16) {
    return text.substring(0, 16);
  }

  while (text.length() < 16) {
    text += " ";
  }

  return text;
}