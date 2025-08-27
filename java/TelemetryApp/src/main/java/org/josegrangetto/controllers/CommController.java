package org.josegrangetto.controllers;

import com.fazecast.jSerialComm.*;
import org.josegrangetto.model.Telemetry;
import org.josegrangetto.utils.SerialDecoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CommController {

    private SerialPort selPort;
    public Telemetry data = new Telemetry();


    public ArrayList<String> getAvailablePorts() {
        com.fazecast.jSerialComm.SerialPort[] ports = com.fazecast.jSerialComm.SerialPort.getCommPorts();
        ArrayList<String> portNames = new ArrayList<>();
        for (com.fazecast.jSerialComm.SerialPort port : ports) {
            portNames.add(port.getSystemPortName());
        }
        return portNames;
    }

    public boolean openPort(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        for (com.fazecast.jSerialComm.SerialPort port : com.fazecast.jSerialComm.SerialPort.getCommPorts()) {
            if (port.getSystemPortName().equals(portName)) {
                selPort = port;
                selPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
                if (selPort.openPort()) {
                    selPort.addDataListener(listener);
                    return true;
                }

            }
        }
        return false;
    }

    private SerialPortMessageListener listener = new SerialPortMessageListener() {
        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return new byte[]{(byte) 0x03};
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            byte[] delimitedMessage = event.getReceivedData();
            try {
                if (delimitedMessage.length != 25 || delimitedMessage[24] != (byte) 0x03) {
                    System.out.println("Invalid data or missing delimiter");
                    return;
                }

                byte[] payload = new byte[24];
                System.arraycopy(delimitedMessage, 0, payload, 0, 24);

                Telemetry newData = SerialDecoder.fromByteToFloat(payload);
                newData.reformatAll();

                synchronized (CommController.this) {
                    data = newData;
                    System.out.println("New Data: " + data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void sendData(byte dataToSend) {
        byte delimiter = (byte) 0x03;
        selPort.writeBytes(new byte[]{dataToSend}, 1);
        selPort.writeBytes(new byte[]{delimiter}, 1);
        System.out.println("Sent command: " + String.format("0x%02X", dataToSend));
    }

    public void closePort() {
        if (selPort != null && selPort.isOpen()) {
            selPort.closePort();
            System.out.println("Puerto cerrado correctamente.");
        }
    }
}

