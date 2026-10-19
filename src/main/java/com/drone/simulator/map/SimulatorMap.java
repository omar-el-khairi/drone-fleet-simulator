package com.drone.simulator.map;

import com.drone.simulator.model.Drone;
import java.util.List;
import java.util.Random;

// Gère la grille et l'affichage
public class SimulatorMap {
    public static final char OBSTACLE = '#';
    public static final char EMPTY = '.';

    private final int width;
    private final int height;
    private final char[][] grid;

    public SimulatorMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        initGrid();
    }

    private void initGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY;
            }
        }
    }

    // Ajoute des obstacles aléatoirement sans écraser les drones
    public void addObstacles(List<Drone> drones, int obstacleCount) {
        Random random = new Random();
        int placed = 0;
        int maxAttempts = obstacleCount * 10;
        int attempts = 0;

        while (placed < obstacleCount && attempts < maxAttempts) {
            int ox = random.nextInt(width);
            int oy = random.nextInt(height);
            attempts++;

            if (grid[oy][ox] == OBSTACLE) continue;

            boolean isDronePosition = false;
            for (Drone drone : drones) {
                if (drone.getX() == ox && drone.getY() == oy) {
                    isDronePosition = true;
                    break;
                }
            }

            if (!isDronePosition) {
                grid[oy][ox] = OBSTACLE;
                placed++;
            }
        }
    }

    public void displayMap(List<Drone> drones) {
        char[][] display = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                display[y][x] = grid[y][x];
            }
        }

        for (Drone drone : drones) {
            int dx = drone.getX();
            int dy = drone.getY();
            if (dx >= 0 && dx < width && dy >= 0 && dy < height) {
                display[dy][dx] = drone.getDisplayChar();
            }
        }

        printBorder();
        
        // Affichage de haut en bas
        for (int y = height - 1; y >= 0; y--) {
            System.out.print(String.format("%3d |", y));
            for (int x = 0; x < width; x++) {
                char c = display[y][x];
                if (c == EMPTY) System.out.print(" .");
                else if (c == OBSTACLE) System.out.print(" #");
                else System.out.print(" " + c);
            }
            System.out.println();
        }

        printXAxis();
        printBorder();
    }

    private void printBorder() {
        System.out.print("    +");
        for (int x = 0; x < width; x++) System.out.print("--");
        System.out.println("+");
    }

    private void printXAxis() {
        System.out.print("     ");
        for (int x = 0; x < width; x++) {
            System.out.print(String.format("%2d", x % 100));
        }
        System.out.println();
    }

    public boolean isBlocked(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return grid[y][x] == OBSTACLE;
    }

    public void placeObstacle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = OBSTACLE;
        }
    }

    // Gestion du dépassement de la carte (effet pacman)
    public int wrapCoordinate(int coord, int max) {
        return ((coord % max) + max) % max;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public char[][] getGrid() { return grid; }
}
