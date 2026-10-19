package com.drone.simulator.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Version 3 : Serveur TCP qui écoute les connexions
public class DroneServer {
    public static final int DEFAULT_PORT = 5000;
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    public DroneServer() { this(DEFAULT_PORT); }
    public DroneServer(int port) { this.port = port; }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("=== SERVEUR DÉMARRÉ SUR LE PORT " + port + " ===");
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté.");
                
                // On crée un nouveau thread pour chaque client connecté
                new Thread(new ServerConnectionHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            if (running) System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur arrêt serveur : " + e.getMessage());
        }
    }
}
