package org.josegrangetto.test;

import org.josegrangetto.model.Telemetry;
import org.josegrangetto.controllers.CommController;
import org.josegrangetto.services.SupportedBaudRate;

import java.util.Scanner;

public class SerialTest {
    public static void main(String[] args) {

        CommController comm = new CommController();
        Scanner sc = new Scanner(System.in);

        System.out.printf("Puertos disponibles: ");
        comm.getAvailablePorts().forEach(System.out::println);

        System.out.println("\nIngrese el nombre del puerto a usar: ");
        String port = sc.nextLine();

        boolean isOpen = comm.openPort(port, SupportedBaudRate.RATE_115200.getSpeed(), 8, 1,0);

        if(isOpen){
            System.out.println("El puerto " + port + " Se ha abierto correctamente.");
            System.out.println("Esperando datos... (Ctrl+C to exit)");

            while(comm.isPortOpen()){
                try{
                    synchronized (comm){
                        Telemetry t = comm.data;
                        if(t != null){
                            System.out.println(t.toString());
                        }
                    }
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }

        }else{
            System.out.println("El puerto " + port + " No se ha podido abrir.");
        }
    }
}
