#ifndef MPU6050_h
#define MPU6050_h

#include <stdint.h>

/* 
=======================================================
    1. Address and Register Definitions for MPU6050
=======================================================
*/

// I2C address and register config
#define MPU6050_ADDR        0x68 // I2C address of the MPU6050
#define MPU6050_WHO_AM_I    0x75 // WHO_AM_I register
#define PWR_MGMT_1_REG      0x6B // Power management register 1
#define GYRO_CONFIG_REG     0x1B // Gyroscope configuration register
#define ACCEL_CONFIG_REG    0x1C // Accelerometer configuration register

// Readings registers
#define ACCEL_XOUT_H        0x3B // Accelerometer X-axis high byte


// Sensitivity scale factors
#define GYRO_SENS_250DPS   131.0
#define ACCEL_SENS_2G      16384.0


#endif /* MPU6050_h */