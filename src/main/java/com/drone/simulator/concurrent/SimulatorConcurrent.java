package com.drone.simulator.concurrent;

import com.drone.simulator.io.ConfigurationReader;
import com.drone.simulator.io.ConfigurationReader.ConfigurationException;
import com.drone.simulator.map.SimulatorMap;
import com.drone.simulator.model.Drone;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Version 2 : Tous les drones bougent en même temps (Threads)
public class SimulatorConcurrent {
    private SimulatorMap map;
    private List<Drone> drones;
    private List<DroneWorker> droneWorkers;
    private DisplayWorker displayWorker;
    
    private ReentrantLock displayLock;
    private Condition displayCondition;
    private CountDownLatch completionLatch;

    private static final double OBSTACLE_RATIO = 0.08;

    public void initializeSimulation(String configFile) throws IOException, ConfigurationException {
        System.out.println("\n=== Initialisation Concurrent ===\n");

        ConfigurationReader reader = new ConfigurationReader();
        reader.readConfiguration(configFile);
        
        int width = reader.getMapWidth();
        int height = reader.getMapHeight();
        drones = reader.getDrones();

        map = new SimulatorMap(width, height);
        map.addObstacles(drones, (int)(width * height * OBSTACLE_RATIO));

        initializeThreads();
    }

    public void initializeThreads() {
        displayLock = new ReentrantLock();
        displayCondition = displayLock.newCondition();
        completionLatch = new CountDownLatch(drones.size());
        droneWorkers = new ArrayList<>();

        for (Drone drone : drones) {
            droneWorkers.add(new DroneWorker(drone, map, displayLock, displayCondition, completionLatch));
        }
        displayWorker = new DisplayWorker(map, drones, displayLock, displayCondition);
    }

    public void startSimulation() {
        System.out.println("\n=== Démarrage Threads ===");
        displayWorker.start();
        for (DroneWorker worker : droneWorkers) {
            worker.start();
        }
        waitForCompletion();
    }

    public void waitForCompletion() {
        try {
            completionLatch.await(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        displayWorker.stopDisplay();
    }

    public void displayResults() {
        System.out.println("\n=== POSITIONS FINALES (CONCURRENT) ===");
        for (Drone drone : drones) {
            System.out.printf("Drone %d : %d %d %c%n", drone.getId(), drone.getX(), drone.getY(), drone.getOrientation());
        }
        map.displayMap(drones);
    }

    public void run(String configFile) {
        try {
            initializeSimulation(configFile);
            Thread.sleep(1500);
            startSimulation();
            displayResults();
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }
}
