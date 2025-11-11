#ifndef MPU6050_h
#define MPU6050_h

#include <stdint.h>




/* 
        ========================================================
            1. Address and Register Definitions for MPU6050
        ========================================================
*/

// I2C address and register config
#define MPU6050_ADDR        0x68 // I2C address of the MPU6050
#define MPU6050_WHO_AM_I    0x75 // WHO_AM_I register
#define PWR_MGMT_1_REG      0x6B // Power management register 1
#define DLPF_CONFIG_REG     0x1A // Digital Low Pass Filter configuration register
#define GYRO_CONFIG_REG     0x1B // Gyroscope configuration register
#define ACCEL_CONFIG_REG    0x1C // Accelerometer configuration register

// Readings registers
#define ACCEL_XOUT_H_REG    0x3B // Accelerometer X-axis high byte
#define TEMP_OUT_H_REG      0x41 // Temperature high byte
#define GYRO_XOUT_H_REG     0x43 // Gyroscope X-axis high byte


/* 
        ========================================================
                            2. Data Structures
        ========================================================
*/

// Kalman filter structure (1D state: Angle & Bias)
typedef struct {
    double Q_angle;      // Process noise variance (Angle)
    double Q_bias;       // Process noise variance (Gyroscope Bias)
    double R_measure;    // Measurement noise variance (Accelerometer)
    double angle;        // Estimated angle (X state)
    double bias;         // Estimated gyroscope bias (Y state)
    double P[2][2];      // Error covariance matrix

} Kalman_t;


// MPU6050 data structure & lectures, results
typedef struct {
    // Raw data - 16 bits
    int16_t Accel_X_RAW;
    int16_t Accel_Y_RAW;
    int16_t Accel_Z_RAW;
    int16_t Gyro_X_RAW;
    int16_t Gyro_Y_RAW;
    int16_t Gyro_Z_RAW;
    int16_t Temp_RAW;

    // Scaled data - in 'g' and 'deg/s'
    double Ax;  // Acceleration in g
    double Ay;
    double Az;
    double Gx;  // Gyroscope in deg/s
    double Gy;
    double Gz;

    // Temperature in °C
    double Temperature;

    // Estimated angles by Kalman filter
    double KalmanAngleX; // Roll
    double KalmanAngleY; // Pitch

}MPU6050_t;


/* 
    ========================================================
            3. Function Prototypes (Public Interface)
    ========================================================
*/

// Init MPU6050
uint8_t MPU6050_Init();

// Read raw data and convert
void MPU6050_Read_All(MPU6050_t *DataStruct);       // TODO: Read 14 bytes raw data and then convert to 'g' and 'deg/s'

// I2C utility functions
esp_err_t MPU6050_write_reg(uint8_t reg_addr, uint8_t data);
esp_err_t MPU6050_read_reg(uint8_t reg_addr, uint8_t *buffer, uint8_t len);

// Kalman filter functions
void Kalman_Init(Kalman_t *Kalman);
double Kalman_getAngle(Kalman_t *Kalman, double newAngle, double newRate, double dt);

#endif /* MPU6050_h */

/*
    Possible Future Additions:
    - Add Union for config registers like:
        - Gyroscope Sample                -->   DLPFCFG=1,2,3,4,5, or 6
        - Gyroscope Full Scale Range      -->   FS_SEL=0,1,2, or 3
        - Accelerometer Full Scale Range  -->   AFS_SEL=0,1,2 or 3
*/