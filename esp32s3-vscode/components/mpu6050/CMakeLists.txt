idf_component_register(SRCS "MPU6050.c"
                       INCLUDE_DIRS "."
                       REQUIRES driver freertos
                       esp_driver_i2c)