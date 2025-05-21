package org.josegrangetto.services;

import org.josegrangetto.model.Telemetry;
import org.josegrangetto.utils.SerialDecoder;

import java.util.ArrayList;
import java.util.List;

public class CommHandler {

    private com.fazecast.jSerialComm.SerialPort selPort;

    public Telemetry data = new Telemetry();


    public List<String> getAvailablePorts() {
        com.fazecast.jSerialComm.SerialPort[] ports = com.fazecast.jSerialComm.SerialPort.getCommPorts();
        List<String> portNames = new ArrayList<>();
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
                boolean result = selPort.openPort();
                if (result) {
                    return true;
                }

            }
        }
        return false;
    }

    public Telemetry readData(byte[] rawData) throws Exception {
        if (selPort.isOpen() && selPort != null) {
            return SerialDecoder.fromByteToFloat(rawData);
        }
        return null;
    }

    public boolean sendData(int data) {
        // TODO hacer
        return false;
    }

    public boolean isPortOpen() {
        return selPort != null && selPort.isOpen();
    }

    public void closePort() {
        if (selPort != null && selPort.isOpen()) {
            selPort.closePort();
        }
    }
}

