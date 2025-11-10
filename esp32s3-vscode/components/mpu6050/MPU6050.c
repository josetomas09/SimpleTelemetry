#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "driver/i2c_master.h"

#include "MPU6050.h"

#define TIMEOUT_MS 1000
#define DELAY_MS 1000
#define CLOCK_SPEED_HZ 400000  // 400kHz standard I2C speed

static const i2c_port_num_t i2c_port = -1;          // -1 for auto-select I2C port
static const gpio_num_t i2c_sda_pin = 7;            // GPIO number for SDA
static const gpio_num_t i2c_scl_pin = 15;           // GPIO number for SCL
static const uint8_t i2c_glitch_ignore_cnt = 7;     // Glitch filter count

static const uint16_t mpu6050_addr = MPU6050_ADDR;
static const uint32_t mpu6050_scl_speed_hz = CLOCK_SPEED_HZ;
static const uint32_t sleep_time_ms = DELAY_MS;

static i2c_master_bus_handle_t bus_handle;
static i2c_master_dev_handle_t mpu6050_dev_handle;

esp_err_t err;

uint8_t MPU6050_Init(){

    /* 
    ========================================================
            1. I2C Bus Configuration
    ========================================================
    */

    i2c_master_bus_config_t bus_config = {
        .i2c_port = i2c_port,
        .sda_io_num = i2c_sda_pin,
        .scl_io_num = i2c_scl_pin,
        .clk_source = I2C_CLK_SRC_DEFAULT,
        .glitch_ignore_cnt = i2c_glitch_ignore_cnt,
        .flags.enable_internal_pullup = 1,
    };


    // Intialize I2C master bus
    err = i2c_new_master_bus(&bus_config, &bus_handle);
    if (err != ESP_OK) {
        ESP_LOGE("MPU6050", "I2C bus initialization failed: %d", err);
        abort();
    }
    // Another way -> ESP_ERROR_CHECK(i2c_new_master_bus(&bus_config, &bus_handle));


    // MPU6050 device configuration -> Slave device config?...
    i2c_device_config_t dev_config = {
        .dev_addr_length = I2C_ADDR_BIT_7,
        .device_address = mpu6050_addr,
        .scl_speed_hz = mpu6050_scl_speed_hz,
    };

    // Add MPU6050 device to I2C bus
    err = i2c_master_bus_add_device(bus_handle, &dev_config, &mpu6050_dev_handle);
    if (err != ESP_OK) {
        ESP_LOGE("MPU6050", "Failed to add MPU6050 device to I2C bus: %d", err);
        abort();
    }

    // Wake up MPU6050 by writing 0 to PWR_MGMT_1_REG
    err = MPU6050_write_reg(PWR_MGMT_1_REG, 0x00);
    if (err != ESP_OK) {
        ESP_LOGE("MPU6050", "Failed to wake up MPU6050: %d", err);
        abort();
    }
    vTaskDelay(pdMS_TO_TICKS(sleep_time_ms)); // Delay for sensor to stabilize
    
    ESP_LOGE("MPU6050", "MPU6050 initialized successfully");

    uint8_t who_am_i;
    if (MPU6050_read_reg(MPU6050_WHO_AM_I, &who_am_i, 1) != ESP_OK) {
        ESP_LOGE("MPU6050", "Failed to read WHO_AM_I register");
        abort();
    }else if (who_am_i != MPU6050_ADDR) {
        ESP_LOGE("MPU6050", "WHO_AM_I mismatch: expected 0x%02X, got 0x%02X", MPU6050_ADDR, who_am_i);
        abort();
    } else {
        ESP_LOGI("MPU6050", "WHO_AM_I register verified: 0x%02X", who_am_i);
    }
    
    return 0;
    
}

esp_err_t MPU6050_write_reg(uint8_t reg_addr, uint8_t data){
    uint8_t write_payload[] = {
        reg_addr,  // First byte: Register address (RA)
        data       // Second byte: The data to write (DATA)
    };

    err = i2c_master_transmit(
        mpu6050_dev_handle,
        write_payload,
        sizeof(write_payload),
        TIMEOUT_MS
    );

    return err;
};

esp_err_t MPU6050_read_reg(uint8_t reg_addr, uint8_t *buffer, uint8_t len){
    
    err = i2c_master_transmit_receive(
        mpu6050_dev_handle,
        &reg_addr,          // Pointer to Register Address (RA)
        sizeof(reg_addr),
        buffer,
        len,
        TIMEOUT_MS

    );

    return err;
};

