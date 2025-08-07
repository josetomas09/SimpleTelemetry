package org.josegrangetto.services;

public enum SupportedBaudRate {

    RATE_2400(2400),
    RATE_4800(4800),
    RATE_9600(9600),
    RATE_19200(19200),
    RATE_31250(31250),
    RATE_38400(38400),
    RATE_57600(57600),
    RATE_74880(74880),
    RATE_115200(115200),
    RATE_230400(230400),
    RATE_250000(250000);

    private final int speed;

    public int getSpeed() {
        return speed;
    }

    SupportedBaudRate(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return String.valueOf(speed);
    }

}
