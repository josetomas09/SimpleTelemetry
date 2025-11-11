#### **MPU6050** 
datasheet link:
- https://invensense.tdk.com/wp-content/uploads/2015/02/MPU-6000-Datasheet1.pdf

**I2C Operating Frequency:**
- All registers, Fast-mode 400 kHz
- All registers, Standard-mode 100 kHz

**INTERNAL CLOCK SOURCE:**
- Gyroscope Sample Rate, Fast DLPFCFG=0
- Gyroscope Sample Rate, Slow DLPFCFG=1,2,3,4,5, or 6

100 kHz for sampling and 400kHz for i2c comunication.

#### **Kalman filter**
References links:
- https://github.com/TKJElectronics/KalmanFilter
- https://github.com/TKJElectronics/Example-Sketch-for-IMU-including-Kalman-filter
- https://blog.tkjelectronics.dk/2012/09/a-practical-approach-to-kalman-filter-and-how-to-implement-it/





$$\text{Angle}_{kalman}(k) = \text{Angle}_{kalman}(k - 1) + T_s \cdot \text{Rate}(k)$$

$$\mathbf{K}_k = \frac{\mathbf{P}_{k|k-1} \mathbf{H}_k^{\textsf{T}}}{\mathbf{H}_k \mathbf{P}_{k|k-1} \mathbf{H}_k^{\textsf{T}} + \mathbf{R}_k}$$

$$\mathbf{K}_k = \frac{\mathbf{P}_{\text{predicción}}}{\mathbf{P}_{\text{predicción}} + \mathbf{R}_{\text{medida}}}$$

$$\text{rate} = \text{newRate} - \text{Kalman} \to \text{bias} \text{} $$

$$\text{Kalman} \to \text{angle} = \text{Kalman} \to \text{angle} + dt \cdot \text{rate} \text{} $$