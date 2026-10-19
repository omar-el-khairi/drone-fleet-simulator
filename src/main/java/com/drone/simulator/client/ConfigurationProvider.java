package com.drone.simulator.client;

import com.drone.simulator.model.Drone;
import java.util.List;

// Définit les données nécessaires pour lancer la simulation
public interface ConfigurationProvider {
    int getMapWidth();
    int getMapHeight();
    List<Drone> getDrones();
}
