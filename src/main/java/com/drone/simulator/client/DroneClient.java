package com.drone.simulator.client;

import com.drone.simulator.model.Drone;
import java.io.*;
import java.net.Socket;
import java.util.List;

// Client qui se connecte au serveur
public class DroneClient {
    private final String host;
    private final int port;

    public DroneClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(String configFile) {
        System.out.println("=== CLIENT CONNECTÉ SUR " + host + ":" + port + " ===");

        try {
            ConfigurationProvider config = new FileConfigurationProvider(configFile);
            
            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                 
                // Envoi de la taille de la carte
                out.println(config.getMapWidth() + " " + config.getMapHeight());
                
                // Envoi du nombre de drones
                List<Drone> drones = config.getDrones();
                out.println(drones.size());
                
                // Envoi des drones
                for (Drone drone : drones) {
                    out.println(drone.getX() + " " + drone.getY() + " " + drone.getOrientation() + " " + drone.getCommands());
                }

                System.out.println("Configuration envoyée. Attente des résultats...");

                // Réception du statut
                String status = in.readLine();
                if ("SUCCESS".equals(status)) {
                    System.out.println("\n=== POSITIONS FINALES REÇUES ===");
                    for (int i = 0; i < drones.size(); i++) {
                        System.out.println("Drone " + i + " : " + in.readLine());
                    }
                } else {
                    System.err.println("Erreur du serveur : " + status);
                }

            }
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
