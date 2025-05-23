#include <Arduino.h>
#include <Wire.h>
#include <math.h>

#define SDA_PIN 7
#define SCL_PIN 15

float gRoll, gPitch, gYaw, aX, aY, aZ, temp;
float gCalRoll, gCalPitch, gCalYaw, aCalX, aCalY, aCalZ, angRoll, angPitch;
float kRoll = 0,
    kRollUnc = 2 * 2,
    kPitch = 0,
    kPitchUnc = 2 * 2;

float data[6] = {aX, aY, aZ, kRoll, kPitch, temp};

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

    gRoll = (float) gyroX / 65.5;
    gPitch = (float) gyroY / 65.5;
    gYaw = (float) gyroZ / 65.5;

    // Accel Measurements
    Wire.beginTransmission(0x68);
    Wire.write(0x3B);
    Wire.endTransmission();
    Wire.requestFrom(0x68, 6);

    const int16_t accelXLSB = Wire.read() << 8 | Wire.read();
    const int16_t accelYLSB = Wire.read() << 8 | Wire.read();
    const int16_t accelZLSB = Wire.read() << 8 | Wire.read();

    aX = (float) accelXLSB / 16384 - 0.02;
    aY = (float) accelYLSB / 16384 + 0.03;
    aZ = (float) accelZLSB / 16384 - 0.06;

    // Angle Pitch and Roll in degrees
    angRoll = atan(aY / sqrt(aX * aX + aZ * aZ)) * 1 / (180 / PI);
    angPitch = atan(aX / sqrt(aY * aY + aZ * aZ)) * 1 / (180 / PI);

    // Temp Measurements
    Wire.beginTransmission(0x68);
    Wire.write(0x41);
    Wire.endTransmission();
    Wire.requestFrom(0x68, 2);

    const int16_t tempOUT = Wire.read() << 8 | Wire.read();

    // Temp in degree
    temp = (tempOUT / 340.0) + 36.53;
}

void kalman1D(float &kalmanState, float &kalmanUncertainty, float kalmanInput, float kalmanMeasurement) {
    kalmanState = kalmanState + kalmanInput * 0.004;
    kalmanUncertainty = kalmanUncertainty + 0.004 * 0.004 * 4 * 4;
    float kalmanGain = kalmanUncertainty / (kalmanUncertainty + 9);
    kalmanState = kalmanState + kalmanGain * (kalmanMeasurement - kalmanState);
    kalmanUncertainty = (1 - kalmanGain) * kalmanUncertainty;

}

void setup() {
    Serial.begin(115200);

    Wire.begin(SDA_PIN, SCL_PIN);
    Wire.setClock(400000);
    delay(250);

    Wire.beginTransmission(0x68);
    Wire.write(0x6B);
    Wire.write(0x00);
    Wire.endTransmission();
    delay(250);



    // Calibration

    for (int i = 0; i < 4000; i++) {
        gyro_signals();
        gCalRoll += gRoll;
        gCalPitch += gPitch;
        gCalYaw += gYaw;

        aCalX += aX;
        aCalY += aY;
        aCalZ += (aZ - 1.0);
        delay(1);
    }
    gCalRoll /= 4000;
    gCalPitch /= 4000;
    gCalYaw /= 4000;
    aCalX /= 4000;
    aCalY /= 4000;
    aCalZ /= 4000;

}

void loop() {
    gyro_signals();

    gRoll -= gCalRoll;
    gPitch -= gCalPitch;
    gYaw -= gCalYaw;
    aX -= aCalX;
    aY -= aCalY;
    aZ -= aCalZ;

    kalman1D(kRoll, kRollUnc, gRoll, angRoll);
    kalman1D(kPitch, kPitchUnc, gPitch, angPitch);


    Serial.write((byte*)data, sizeof(data));

    delay(50);
}
