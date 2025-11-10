#### **MPU6050** 
datasheet link:
- https://invensense.tdk.com/wp-content/uploads/2015/02/MPU-6000-Datasheet1.pdf

**I2C Operating Frequency:**
- All registers, Fast-mode 400 kHz
- All registers, Standard-mode 100 kHz

**INTERNAL CLOCK SOURCE:**
- Gyroscope Sample Rate, Fast DLPFCFG=0
- Gyroscope Sample Rate, Slow DLPFCFG=1,2,3,4,5, or 6

400 kHz for sampling and 1000kHz for i2c comunication.

#### **Kalman filter**
References links:
- https://github.com/TKJElectronics/KalmanFilter
- https://github.com/TKJElectronics/Example-Sketch-for-IMU-including-Kalman-filter
- https://blog.tkjelectronics.dk/2012/09/a-practical-approach-to-kalman-filter-and-how-to-implement-it/