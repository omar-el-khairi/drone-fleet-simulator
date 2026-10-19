package com.drone.simulator;

import com.drone.simulator.basic.SimulatorBasic;
import com.drone.simulator.client.DroneClient;
import com.drone.simulator.concurrent.SimulatorConcurrent;
import com.drone.simulator.server.DroneServer;
import java.util.Scanner;

// Point d'entrée du programme
public class Main {
    private static final String DEFAULT_CONFIG = "drones.txt";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        // Lancement direct via ligne de commande
        if (args.length > 0) {
            handleCliArgs(args);
            return;
        }

        // Lancement via le menu interactif
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== MENU SIMULATEUR DRONES ===");
            System.out.println("1. Mode Basique (un par un)");
            System.out.println("2. Mode Concurrent (tous en même temps)");
            System.out.println("3. Mode Serveur (attend les connexions)");
            System.out.println("4. Mode Client (envoie les données)");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    new SimulatorBasic().run(askFile(scanner));
                    break;
                case "2":
                    new SimulatorConcurrent().run(askFile(scanner));
                    break;
                case "3":
                    int port = askPort(scanner);
                    new DroneServer(port).startServer();
                    break;
                case "4":
                    System.out.print("Serveur [localhost] : ");
                    String host = scanner.nextLine().trim();
                    if (host.isEmpty()) host = "localhost";
                    new DroneClient(host, askPort(scanner)).run(askFile(scanner));
                    break;
                case "0":
                    exit = true;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide.");
            }
        }
        scanner.close();
    }

    private static void handleCliArgs(String[] args) {
        String mode = args[0].toLowerCase();
        if (mode.equals("--basic")) {
            new SimulatorBasic().run(args.length > 1 ? args[1] : DEFAULT_CONFIG);
        } else if (mode.equals("--concurrent")) {
            new SimulatorConcurrent().run(args.length > 1 ? args[1] : DEFAULT_CONFIG);
        } else if (mode.equals("--server")) {
            new DroneServer(args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT).startServer();
        } else if (mode.equals("--client")) {
            new DroneClient(args.length > 2 ? args[2] : "localhost", args.length > 3 ? Integer.parseInt(args[3]) : DEFAULT_PORT)
                .run(args.length > 1 ? args[1] : DEFAULT_CONFIG);
        }
    }

    private static String askFile(Scanner scanner) {
        System.out.print("Fichier [" + DEFAULT_CONFIG + "] : ");
        String file = scanner.nextLine().trim();
        return file.isEmpty() ? DEFAULT_CONFIG : file;
    }

    private static int askPort(Scanner scanner) {
        System.out.print("Port [" + DEFAULT_PORT + "] : ");
        String port = scanner.nextLine().trim();
        try {
            return port.isEmpty() ? DEFAULT_PORT : Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }
}
