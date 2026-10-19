package com.drone.simulator;

import com.drone.simulator.map.SimulatorMap;
import com.drone.simulator.model.Drone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests pour vérifier que le drone bouge correctement
class DroneTest {
    private SimulatorMap map;

    @BeforeEach
    void setUp() {
        map = new SimulatorMap(20, 20); // Carte vide de 20x20
    }

    @Test
    void testTurnLeft() {
        Drone drone = new Drone(0, 5, 5, 'N', "");
        drone.turnLeft(); assertEquals('W', drone.getOrientation());
        drone.turnLeft(); assertEquals('S', drone.getOrientation());
        drone.turnLeft(); assertEquals('E', drone.getOrientation());
        drone.turnLeft(); assertEquals('N', drone.getOrientation());
    }

    @Test
    void testTurnRight() {
        Drone drone = new Drone(0, 5, 5, 'N', "");
        drone.turnRight(); assertEquals('E', drone.getOrientation());
        drone.turnRight(); assertEquals('S', drone.getOrientation());
        drone.turnRight(); assertEquals('W', drone.getOrientation());
        drone.turnRight(); assertEquals('N', drone.getOrientation());
    }

    @Test
    void testMoveForwardNorth() {
        Drone drone = new Drone(0, 5, 5, 'N', "");
        drone.moveForward(map);
        assertEquals(5, drone.getX());
        assertEquals(6, drone.getY()); // Y augmente quand on va au Nord
    }

    @Test
    void testMoveForwardSouth() {
        Drone drone = new Drone(0, 5, 5, 'S', "");
        drone.moveForward(map);
        assertEquals(5, drone.getX());
        assertEquals(4, drone.getY()); // Y diminue vers le Sud
    }

    // Exemple exact donné dans le sujet du projet
    @Test
    void testExampleFromSubject() {
        Drone drone = new Drone(0, 2, 5, 'N', "LMLM");
        for (char cmd : "LMLM".toCharArray()) {
            drone.executeCommand(cmd, map);
        }
        assertEquals(1, drone.getX());
        assertEquals(4, drone.getY());
        assertEquals('S', drone.getOrientation());
    }

    // Test de la sortie de carte en haut (revient en bas)
    @Test
    void testWrappingNorth() {
        Drone drone = new Drone(0, 5, 19, 'N', "");
        drone.moveForward(map);
        assertEquals(5, drone.getX());
        assertEquals(0, drone.getY()); 
    }

    // Test des obstacles
    @Test
    void testObstacleBlocksMovement() {
        map.placeObstacle(5, 6);
        Drone drone = new Drone(0, 5, 5, 'N', "");
        drone.moveForward(map);
        
        // Le drone ne doit pas avoir bougé
        assertEquals(5, drone.getX());
        assertEquals(5, drone.getY());
    }

    @Test
    void testGetPosition() {
        Drone drone = new Drone(0, 3, 7, 'E', "");
        assertEquals("3 7 E", drone.getPosition());
    }
}
