package org.JoseGrangetto;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner input = new Scanner(System.in);
        SerialPort sp = SerialPort.getCommPort("/dev/ttyUSB0");
        sp.setComPortParameters(115200, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

        if (!sp.openPort()) {
            System.out.println("Could not open port");
            return;
        }
        int blink = 4;
        while (true) {
            System.out.println("Select an option: ");
            System.out.println("1- Color Red");
            System.out.println("2- Color Green");
            System.out.println("3- Color Blue");
            System.out.println("4- OFF");
            System.out.println("5- EXIT");
            System.out.print("Option: ");
            blink = input.nextInt();
            if (blink == 5) {
                sp.getOutputStream().write(4);
                break;
            };
            sp.getOutputStream().write(blink);
            sp.getOutputStream().flush();
        }

    }
}