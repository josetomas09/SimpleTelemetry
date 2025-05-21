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
        telemetry.aY = buffer.getFloat(1);
        telemetry.aZ = buffer.getFloat(2);
        telemetry.roll = buffer.getFloat(3);
        telemetry.pitch = buffer.getFloat(4);
        telemetry.temp = buffer.getFloat(5);

        return telemetry;

    }

}
