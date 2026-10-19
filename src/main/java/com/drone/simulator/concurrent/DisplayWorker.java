package com.drone.simulator.concurrent;

import com.drone.simulator.map.SimulatorMap;
import com.drone.simulator.model.Drone;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Thread qui gère l'affichage en continu
public class DisplayWorker extends Thread {
    private final SimulatorMap map;
    private final List<Drone> drones;
    private final ReentrantLock displayLock;
    private final Condition displayCondition;
    private volatile boolean running = true;
    private static final long REFRESH_INTERVAL_MS = 150;

    public DisplayWorker(SimulatorMap map, List<Drone> drones, ReentrantLock displayLock, Condition displayCondition) {
        super("DisplayWorker");
        this.map = map;
        this.drones = drones;
        this.displayLock = displayLock;
        this.displayCondition = displayCondition;
        setDaemon(true); // S'arrête tout seul à la fin
    }

    @Override
    public void run() {
        while (running) {
            displayLock.lock();
            try {
                // On attend qu'un drone bouge ou que le temps max soit écoulé
                displayCondition.await(REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
                
                clearScreen();
                System.out.println("=== SIMULATEUR CONCURRENT ===");
                map.displayMap(drones);
                
                for (Drone drone : drones) {
                    System.out.println("Drone " + drone.getId() + " : " + drone.getPosition());
                }

            } catch (InterruptedException e) {
                break;
            } finally {
                displayLock.unlock();
            }
        }
    }

    public void stopDisplay() {
        running = false;
        displayLock.lock();
        try {
            displayCondition.signalAll();
        } finally {
            displayLock.unlock();
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
