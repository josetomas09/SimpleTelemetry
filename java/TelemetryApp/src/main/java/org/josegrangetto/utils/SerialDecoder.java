package org.josegrangetto.utils;

import org.josegrangetto.model.Telemetry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SerialDecoder {

    public static Telemetry fromByteToFloat(byte[] rawData) {
        if (rawData.length != 24) throw new IllegalArgumentException("Invalid length of raw data");
        ByteBuffer buffer = ByteBuffer.wrap(rawData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        Telemetry telemetry = new Telemetry();
        telemetry.aX = buffer.getFloat(0);
        telemetry.aY = buffer.getFloat(4);
        telemetry.aZ = buffer.getFloat(8);
        telemetry.roll = buffer.getFloat(12);
        telemetry.pitch = buffer.getFloat(16);
        telemetry.temp = buffer.getFloat(20);

        return telemetry;

    }

}
