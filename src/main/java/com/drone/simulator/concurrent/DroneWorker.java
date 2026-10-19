package com.drone.simulator.concurrent;

import com.drone.simulator.map.SimulatorMap;
import com.drone.simulator.model.Drone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Thread qui contrôle un seul drone
public class DroneWorker extends Thread {
    private final Drone drone;
    private final SimulatorMap map;
    private final ReentrantLock displayLock;
    private final Condition displayCondition;
    private final CountDownLatch completionLatch;

    public DroneWorker(Drone drone, SimulatorMap map, ReentrantLock displayLock, Condition displayCondition, CountDownLatch completionLatch) {
        super("Drone-" + drone.getId());
        this.drone = drone;
        this.map = map;
        this.displayLock = displayLock;
        this.displayCondition = displayCondition;
        this.completionLatch = completionLatch;
    }

    @Override
    public void run() {
        for (char cmd : drone.getCommands().toCharArray()) {
            
            // On bloque la carte pour bouger sans collision avec d'autres threads
            synchronized (map) {
                drone.executeCommand(cmd, map);
            }

            // On prévient l'affichage qu'on a bougé
            displayLock.lock();
            try {
                displayCondition.signalAll();
            } finally {
                displayLock.unlock();
            }

            try { Thread.sleep(50); } catch (InterruptedException e) { break; }
        }

        completionLatch.countDown();
    }
}
