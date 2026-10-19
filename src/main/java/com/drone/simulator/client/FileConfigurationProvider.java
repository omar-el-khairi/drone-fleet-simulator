package com.drone.simulator.client;

import com.drone.simulator.io.ConfigurationReader;
import com.drone.simulator.model.Drone;
import java.util.List;

// Fournit la configuration depuis un fichier texte
public class FileConfigurationProvider implements ConfigurationProvider {
    private final int mapWidth;
    private final int mapHeight;
    private final List<Drone> drones;

    public FileConfigurationProvider(String filename) throws Exception {
        ConfigurationReader reader = new ConfigurationReader();
        reader.readConfiguration(filename);
        this.mapWidth = reader.getMapWidth();
        this.mapHeight = reader.getMapHeight();
        this.drones = reader.getDrones();
    }

    @Override public int getMapWidth() { return mapWidth; }
    @Override public int getMapHeight() { return mapHeight; }
    @Override public List<Drone> getDrones() { return drones; }
}
