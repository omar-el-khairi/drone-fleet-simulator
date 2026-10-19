package com.drone.simulator.model;

import com.drone.simulator.map.SimulatorMap;

// Représente un drone avec ses coordonnées et sa liste de commandes
public class Drone {
    private int id;
    private int x;
    private int y;
    private char orientation;
    private String commands;

    public Drone(int id, int x, int y, char orientation, String commands) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.commands = commands;
    }

    // Exécute une seule commande
    public void executeCommand(char cmd, SimulatorMap map) {
        switch (Character.toUpperCase(cmd)) {
            case 'L': turnLeft(); break;
            case 'R': turnRight(); break;
            case 'M': moveForward(map); break;
            case 'B': moveBackward(map); break;
        }
    }

    public void turnLeft() {
        switch (orientation) {
            case 'N': orientation = 'W'; break;
            case 'W': orientation = 'S'; break;
            case 'S': orientation = 'E'; break;
            case 'E': orientation = 'N'; break;
        }
    }

    public void turnRight() {
        switch (orientation) {
            case 'N': orientation = 'E'; break;
            case 'E': orientation = 'S'; break;
            case 'S': orientation = 'W'; break;
            case 'W': orientation = 'N'; break;
        }
    }

    public void moveForward(SimulatorMap map) {
        int newX = x;
        int newY = y;

        switch (orientation) {
            case 'N': newY++; break;
            case 'S': newY--; break;
            case 'E': newX++; break;
            case 'W': newX--; break;
        }

        // Gère la sortie de carte
        newX = map.wrapCoordinate(newX, map.getWidth());
        newY = map.wrapCoordinate(newY, map.getHeight());

        // On avance seulement si la case est libre
        if (!map.isBlocked(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    public void moveBackward(SimulatorMap map) {
        int newX = x;
        int newY = y;

        switch (orientation) {
            case 'N': newY--; break;
            case 'S': newY++; break;
            case 'E': newX--; break;
            case 'W': newX++; break;
        }

        newX = map.wrapCoordinate(newX, map.getWidth());
        newY = map.wrapCoordinate(newY, map.getHeight());

        if (!map.isBlocked(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    // Retourne le symbole du drone pour l'affichage
    public char getDisplayChar() {
        switch (orientation) {
            case 'N': return '^';
            case 'S': return 'v';
            case 'E': return '>';
            case 'W': return '<';
            default:  return '?';
        }
    }

    public String getPosition() {
        return x + " " + y + " " + orientation;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public char getOrientation() { return orientation; }
    public void setOrientation(char orientation) { this.orientation = orientation; }
    public String getCommands() { return commands; }
    public void setCommands(String commands) { this.commands = commands; }

    @Override
    public String toString() {
        return "Drone " + id + " [" + x + ", " + y + ", " + orientation + "] cmds=" + commands;
    }
}
