package com.drone.simulator.server;

import com.drone.simulator.concurrent.DisplayWorker;
import com.drone.simulator.concurrent.DroneWorker;
import com.drone.simulator.io.ConfigurationReader;
import com.drone.simulator.map.SimulatorMap;
import com.drone.simulator.model.Drone;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Gère un client connecté
public class ServerConnectionHandler implements Runnable {
    private final Socket socket;
    private static final double OBSTACLE_RATIO = 0.08;

    public ServerConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Lecture des dimensions
            String[] dims = in.readLine().split(" ");
            int width = Integer.parseInt(dims[0]);
            int height = Integer.parseInt(dims[1]);

            // Lecture du nombre de drones
            int numDrones = Integer.parseInt(in.readLine().trim());
            List<Drone> drones = new ArrayList<>();
            ConfigurationReader reader = new ConfigurationReader();

            for (int i = 0; i < numDrones; i++) {
                drones.add(reader.parseDroneData(i, in.readLine()));
            }

            // Démarrage de la simulation côté serveur
            SimulatorMap map = new SimulatorMap(width, height);
            map.addObstacles(drones, (int)(width * height * OBSTACLE_RATIO));
            
            executeSimulation(drones, map);

            // Renvoi des résultats au client
            out.println("SUCCESS");
            for (Drone drone : drones) {
                out.println(drone.getX() + " " + drone.getY() + " " + drone.getOrientation());
            }

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // Lance la simulation avec threads et attend la fin
    private void executeSimulation(List<Drone> drones, SimulatorMap map) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cond = lock.newCondition();
        CountDownLatch latch = new CountDownLatch(drones.size());

        DisplayWorker display = new DisplayWorker(map, drones, lock, cond);
        display.start();

        for (Drone drone : drones) {
            new DroneWorker(drone, map, lock, cond, latch).start();
        }

        latch.await();
        display.stopDisplay();
    }
}
