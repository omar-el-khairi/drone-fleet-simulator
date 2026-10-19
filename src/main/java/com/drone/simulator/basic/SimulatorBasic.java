package com.drone.simulator.basic;

import com.drone.simulator.io.ConfigurationReader;
import com.drone.simulator.io.ConfigurationReader.ConfigurationException;
import com.drone.simulator.map.SimulatorMap;
import com.drone.simulator.model.Drone;
import java.io.IOException;
import java.util.List;

// Version 1 : Les drones bougent un par un
public class SimulatorBasic {
    private SimulatorMap map;
    private List<Drone> drones;
    private static final int STEP_DELAY_MS = 300;
    private static final double OBSTACLE_RATIO = 0.08;

    public void initializeSimulation(String configFile) throws IOException, ConfigurationException {
        System.out.println("\n=== Initialisation du Simulateur Basique ===\n");

        ConfigurationReader reader = new ConfigurationReader();
        reader.readConfiguration(configFile);

        int width = reader.getMapWidth();
        int height = reader.getMapHeight();
        drones = reader.getDrones();

        System.out.println("Fichier : " + configFile);
        System.out.printf("Carte : %dx%d%n", width, height);
        System.out.println("Drones : " + drones.size());

        map = new SimulatorMap(width, height);
        map.addObstacles(drones, (int)(width * height * OBSTACLE_RATIO));

        for (Drone drone : drones) {
            System.out.printf("Drone %d : Départ %d %d %c | Cmds : %s%n",
                    drone.getId(), drone.getX(), drone.getY(), drone.getOrientation(), drone.getCommands());
        }
    }

    public void executeSimulation() {
        System.out.println("\n=== Démarrage (Séquentiel) ===\n");

        for (Drone drone : drones) {
            System.out.println("--------------------------------------------------");
            map.displayMap(drones);

            for (char cmd : drone.getCommands().toCharArray()) {
                String avant = drone.getPosition();
                drone.executeCommand(cmd, map);
                System.out.printf("  [CMD: %c] %s  ->  %s%n", cmd, avant, drone.getPosition());

                clearScreen();
                System.out.printf("=== Drone %d - Commande: %c ===%n", drone.getId(), cmd);
                map.displayMap(drones);
                sleep(STEP_DELAY_MS);
            }
            System.out.println();
        }
    }

    public void displayResults() {
        System.out.println("\n=== POSITIONS FINALES ===");
        for (Drone drone : drones) {
            System.out.printf("Drone %d : %d %d %c%n", drone.getId(), drone.getX(), drone.getY(), drone.getOrientation());
        }
        System.out.println("\nCarte finale :");
        map.displayMap(drones);
    }

    public void run(String configFile) {
        try {
            initializeSimulation(configFile);
            executeSimulation();
            displayResults();
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
