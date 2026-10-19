package com.drone.simulator.io;

import com.drone.simulator.model.Drone;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Lit le fichier drones.txt
public class ConfigurationReader {
    private int mapWidth;
    private int mapHeight;
    private List<Drone> drones;

    public ConfigurationReader() {
        this.drones = new ArrayList<>();
    }

    public void readConfiguration(String filename) throws IOException, ConfigurationException {
        drones.clear();
        
        // Resolve the actual file path
        Path filePath = resolveFilePath(filename);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8))) {

            String dimensionLine = readNextNonEmptyLine(reader);
            if (dimensionLine == null) throw new ConfigurationException("Fichier vide.");
            parseMapDimensions(dimensionLine);

            String secondLine = readNextNonEmptyLine(reader);
            if (secondLine == null) throw new ConfigurationException("Pas de drones.");

            int numDrones = -1;
            String firstDroneLine = null;

            String[] parts = secondLine.trim().split("\\s+");
            if (parts.length == 1) {
                try {
                    numDrones = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    throw new ConfigurationException("Nombre de drones invalide.");
                }
            } else {
                firstDroneLine = secondLine;
            }

            int droneIndex = 0;
            if (firstDroneLine != null) {
                drones.add(parseDroneData(droneIndex++, firstDroneLine));
            }

            String line;
            while ((line = readNextNonEmptyLine(reader)) != null) {
                if (numDrones != -1 && droneIndex >= numDrones) break;
                drones.add(parseDroneData(droneIndex++, line));
            }
        }
    }

    private void parseMapDimensions(String line) throws ConfigurationException {
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 2) throw new ConfigurationException("Dimensions invalides.");
        try {
            mapWidth  = Integer.parseInt(parts[0]);
            mapHeight = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Dimensions non numériques.");
        }
    }

    public Drone parseDroneData(int index, String line) throws ConfigurationException {
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 4) throw new ConfigurationException("Données drone invalides.");

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            char orientation = Character.toUpperCase(parts[2].charAt(0));
            String commands = parts[3].toUpperCase();

            if (orientation != 'N' && orientation != 'S' && orientation != 'E' && orientation != 'W') {
                throw new ConfigurationException("Orientation invalide.");
            }
            return new Drone(index, x, y, orientation, commands);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Coordonnées non numériques.");
        }
    }

    private String readNextNonEmptyLine(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("//") && !line.startsWith("#")) {
                return line;
            }
        }
        return null;
    }

    private Path resolveFilePath(String filename) throws ConfigurationException {
        // List of locations to search for the file
        Path[] searchLocations = {
            Paths.get(filename),                                    // Current directory
            Paths.get("drone-simulator").resolve(filename),         // drone-simulator subdir
            Paths.get(".").resolve(filename),                       // Explicit current dir
        };

        for (Path location : searchLocations) {
            if (location.toFile().exists() && location.toFile().isFile()) {
                return location;
            }
        }

        // If not found anywhere, throw an error with helpful information
        throw new ConfigurationException(filename + " (Le fichier spécifié est introuvable. " +
                "Cherché dans: répertoire courant, drone-simulator/)");
    }

    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public List<Drone> getDrones() { return new ArrayList<>(drones); }

    public static class ConfigurationException extends Exception {
        public ConfigurationException(String message) { super(message); }
    }
}
