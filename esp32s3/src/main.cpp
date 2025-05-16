#include <Arduino.h>
#include <Wire.h>
#include <Adafruit_NeoPixel.h>

#define LED_BUILDIN 48
#define SDA_PIN 7
#define SCL_PIN 15

float rateRoll, ratePitch, rateYaw,
        accX, accY, accZ, tempC;
float rateCalibrationRoll, rateCalibrationPitch, rateCalibrationYaw,
        rateCalibrationAccelX, rateCalibrationAccelY, rateCalibrationAccelZ,
        angleRoll, anglePitch;
float kalmanAngleRoll = 0,
        kalmanUncertaintyAngleRoll = 2 * 2;
float kalmanAnglePitch = 0,
        kalmanUncertaintyAnglePitch = 2 * 2;
float kalman1DOutput[] = {0, 0};
int rateCalibrationNumber;

void gyro_signals(void) {
    // DLPF Config
    Wire.beginTransmission(0x68);
    Wire.write(0x1A);
    Wire.write(0x04); // DLPF_CFG = 4
    Wire.endTransmission();

    // Gyro Config
    Wire.beginTransmission(0x68);
    Wire.write(0x1B);
    Wire.write(0x8); //FL_SEL = 1
    Wire.endTransmission();

    // Accel Config
    Wire.beginTransmission(0x68);
    Wire.write(0x1C);
    Wire.write(0x00); //AFL_SEL=0
    Wire.endTransmission();

    // Gyro Measurements
    Wire.beginTransmission(0x68);
    Wire.write(0x43);
    Wire.endTransmission();
    Wire.requestFrom(0x68, 6);

    const int16_t gyroX = Wire.read() << 8 | Wire.read();
    const int16_t gyroY = Wire.read() << 8 | Wire.read();
    const int16_t gyroZ = Wire.read() << 8 | Wire.read();

    rateRoll = (float) gyroX / 65.5;
    ratePitch = (float) gyroY / 65.5;
    rateYaw = (float) gyroZ / 65.5;

    // Accel Measurements
    Wire.beginTransmission(0x68);
    Wire.write(0x3B);
    Wire.endTransmission();
    Wire.requestFrom(0x68, 6);

    const int16_t accelXLSB = Wire.read() << 8 | Wire.read();
    const int16_t accelYLSB = Wire.read() << 8 | Wire.read();
    const int16_t accelZLSB = Wire.read() << 8 | Wire.read();

    accX = (float) accelXLSB / 16384;
    accY = (float) accelYLSB / 16384;
    accZ = (float) accelZLSB / 16384;

    // Angle Pitch and Roll in degrees
    angleRoll = atan(accY / sqrt(accX * accX + accZ * accZ)) * 1 / (3.142 / 180);
    anglePitch = atan(accX / sqrt(accY * accY + accZ * accZ)) * 1 / (3.142 / 180);

    // Temp Measurements
    Wire.beginTransmission(0x68);
    Wire.write(0x41);
    Wire.endTransmission();
    Wire.requestFrom(0x68, 2);

    const int16_t tempOUT = Wire.read() << 8 | Wire.read();

    // Temp in degree
    tempC = (tempOUT / 340.0) + 36.53;
}

void kalman1D(float kalmanState, float kalmanUncertainty, float kalmanInput, float kalmanMeasurement) {
    kalmanState = kalmanState + kalmanInput * 0.004;
    kalmanUncertainty = kalmanUncertainty + 0.004 * 0.004 * 4 * 4;
    float kalmanGain = kalmanUncertainty * 1 / (1 * kalmanUncertainty + 3 * 3);
    kalmanState = kalmanState + kalmanGain * (kalmanMeasurement - kalmanState);
    kalmanUncertainty = (1 - kalmanGain) * kalmanUncertainty;

    kalman1DOutput[0] = kalmanState;
    kalman1DOutput[1] = kalmanUncertainty;
}

void setup() {
    Serial.begin(115200);
    neopixelWrite(LED_BUILTIN, 0, 20, 0);

    Wire.begin(SDA_PIN, SCL_PIN);
    Wire.setClock(400000);
    delay(250);

    Wire.beginTransmission(0x68);
    Wire.write(0x6B);
    Wire.write(0x00);
    Wire.endTransmission();
    delay(250);
    neopixelWrite(LED_BUILTIN, 0, 0, 0);


    // Calibration

    for (rateCalibrationNumber = 0; rateCalibrationNumber < 4000; rateCalibrationNumber++) {
        neopixelWrite(LED_BUILTIN, 0, 0, 20);
        gyro_signals();
        rateCalibrationRoll += rateRoll;
        rateCalibrationPitch += ratePitch;
        rateCalibrationYaw += rateYaw;
        rateCalibrationAccelX += accX;
        rateCalibrationAccelY += accY;
        rateCalibrationAccelZ += accZ;
        delay(5);
    }

    rateCalibrationRoll /= 4000;
    rateCalibrationPitch /= 4000;
    rateCalibrationYaw /= 4000;
    rateCalibrationAccelX /= 4000;
    rateCalibrationAccelY /= 4000;
    rateCalibrationAccelZ /= 4000;
    neopixelWrite(LED_BUILTIN, 0, 0, 0);
}

void loop() {
    gyro_signals();

    rateRoll -= rateCalibrationRoll;
    ratePitch -= rateCalibrationPitch;
    rateYaw -= rateCalibrationYaw;
    accX -= rateCalibrationAccelX;
    accY -= rateCalibrationAccelY;
    accZ -= rateCalibrationAccelZ;

    kalman1D(kalmanAngleRoll, kalmanUncertaintyAngleRoll, rateRoll, angleRoll);
    kalmanAngleRoll = kalman1DOutput[0];
    kalmanUncertaintyAngleRoll = kalman1DOutput[1];
    kalman1D(kalmanAnglePitch, kalmanUncertaintyAnglePitch, ratePitch, anglePitch);
    kalmanAnglePitch = kalman1DOutput[1];
    kalmanUncertaintyAnglePitch = kalman1DOutput[1];

    Serial.println("Roll rate (X) [°/s]= " + String(rateRoll)
                   + " | Pitch Rate (Y) [°/s]= " + String(ratePitch)
                   + " | Yaw Rate (Z) [°/s]= " + String(rateYaw)
                   + " | AccelX [G] = " + String(accX)
                   + " | AccelY [G] = " + String(accY)
                   + " | AccelZ [G] = " + String(accZ)
                   + " | Temp C [C]= " + String(tempC)
                   + " | Roll angle [°] = " + String(angleRoll)
                   + " | Pitch angle [°] = " + String(anglePitch)
    );

    delay(50);
}
