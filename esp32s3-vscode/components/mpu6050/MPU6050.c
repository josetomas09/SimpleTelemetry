#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "driver/i2c_master.h"

#include "MPU6050.h"

#define TIMEOUT_MS 1000
#define DELAY_MS 1000

#define SDA_GPIO 7
#define SCL_GPIO 15




