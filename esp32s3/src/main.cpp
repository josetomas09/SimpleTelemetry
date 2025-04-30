#include <Arduino.h>
#include "Adafruit_NeoPixel.h"

#define PIN 48
#define NUMPIXELS 1

Adafruit_NeoPixel pixels(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);
#define DELAY 500

void setup() {
    Serial.begin(115200);
    pixels.begin();
    pixels.show();
}

void loop() {
    if (Serial.available()) {
        byte inByte = Serial.read();

        switch (inByte) {
            case 1:
                pixels.setPixelColor(0, pixels.Color(255, 0, 0));  // Red
                break;
            case 2:
                pixels.setPixelColor(0, pixels.Color(0, 255, 0));  // Green
                break;
            case 3:
                pixels.setPixelColor(0, pixels.Color(0, 0, 255));  // Blue
                break;
            case 4:
                pixels.setPixelColor(0, pixels.Color(0, 0, 0));  // Off / black
                break;
        }
        pixels.show();
        delay(500);
    }
}