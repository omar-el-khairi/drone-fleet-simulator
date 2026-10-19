# 🚁 Simulateur de Flotte de Drones

**FSTS M1 RSI - 2025/2026 | Pr. A. Marzouk**

Simulateur Java de déplacements de drones sur un plateau avec obstacles, implémentant trois versions : séquentielle, concurrente (threads) et communicante (sockets TCP).

---

## 📁 Structure du Projet

```
drone-simulator/
├── pom.xml
├── drones.txt              ← Fichier de config principal (exemple sujet)
├── drones_test2.txt        ← Test : 4 drones, carte 10x10
├── drones_test3.txt        ← Test : 5 drones, grande carte 30x30
└── src/
    ├── main/java/com/drone/simulator/
    │   ├── Main.java                          ← Point d'entrée + menu
    │   ├── model/
    │   │   └── Drone.java                     ← Classe métier drone
    │   ├── map/
    │   │   └── SimulatorMap.java              ← Carte + obstacles + affichage
    │   ├── io/
    │   │   └── ConfigurationReader.java       ← Lecture fichier de config
    │   ├── basic/
    │   │   └── SimulatorBasic.java            ← Version 1 : Séquentielle
    │   ├── concurrent/
    │   │   ├── SimulatorConcurrent.java       ← Version 2 : Concurrente
    │   │   ├── DroneWorker.java               ← Thread par drone
    │   │   └── DisplayWorker.java             ← Thread d'affichage
    │   ├── server/
    │   │   ├── DroneServer.java               ← Version 3 : Serveur TCP
    │   │   └── ServerConnectionHandler.java   ← Handler par client
    │   └── client/
    │       ├── DroneClient.java               ← Client TCP
    │       ├── ConfigurationProvider.java     ← Interface config
    │       └── FileConfigurationProvider.java ← Config depuis fichier
    └── test/java/com/drone/simulator/
        └── DroneTest.java                     ← Tests JUnit 5
```

---

## ⚙️ Prérequis

| Outil  | Version minimum |
|--------|-----------------|
| Java   | JDK 11+         |
| Maven  | 3.6+            |

---

## 🔨 Compilation

```bash
cd drone-simulator
mvn clean package -DskipTests
```

Le JAR exécutable est généré dans `target/drone-simulator.jar`.

Pour compiler et lancer les tests :

```bash
mvn clean test
```

---

## ▶️ Exécution

### Menu interactif (recommandé)

```bash
java -jar target/drone-simulator.jar
```

Un menu s'affiche pour choisir le mode.

---

### Mode 1 — Basique (Séquentiel)

Les drones s'exécutent l'un après l'autre.

```bash
# Avec le menu → choisir 1
java -jar target/drone-simulator.jar --basic drones.txt
```

**Exemple de sortie :**
```
Drone 0 : Position initiale : 0 5 N | Commandes : LMLMLMLMM
  [CMD: L] 0 5 N  ->  Position du drone 0 : 0 5 W
  [CMD: M] 0 5 W  ->  Position du drone 0 : 19 5 W
  ...
Positions finales :
  Drone 0 : 1 3 N
  Drone 1 : 5 1 E
```

---

### Mode 2 — Concurrent (Multi-Thread)

Tous les drones s'exécutent **simultanément** dans des threads séparés. La carte se rafraîchit en temps réel.

```bash
java -jar target/drone-simulator.jar --concurrent drones.txt
```

**Mécanismes de synchronisation utilisés :**
- `ReentrantLock` + `Condition` pour la synchronisation affichage ↔ drones
- `CountDownLatch` pour attendre la fin de tous les threads
- `synchronized (map)` pour protéger l'accès concurrent à la carte

---

### Mode 3 — Serveur (Réseau)

Démarrer le serveur (dans un terminal) :

```bash
java -jar target/drone-simulator.jar --server 5000
```

**Sortie :**
```
╔═══════════════════════════════════════════╗
║      SERVEUR SIMULATEUR DE DRONES         ║
╚═══════════════════════════════════════════╝
  Port d'écoute : 5000
  En attente de connexions...
```

---

### Mode 4 — Client (Réseau)

Dans un **second terminal**, lancer le client :

```bash
java -jar target/drone-simulator.jar --client drones.txt localhost 5000
```

**Protocole d'échange :**
```
Client → Serveur :
  20 20              ← dimensions carte
  2                  ← nombre de drones
  0 5 N LMLMLMLMM   ← drone 0
  5 0 S MMRMMRMRRM  ← drone 1

Serveur → Client :
  SUCCESS
  1 3 N              ← position finale drone 0
  5 1 E              ← position finale drone 1
```

---

## 📄 Format du Fichier de Configuration

```
[LARGEUR] [HAUTEUR]
[NOMBRE_DRONES]          ← optionnel
[X] [Y] [ORIENTATION] [COMMANDES]
...
```

**Orientations :** `N` (Nord), `S` (Sud), `E` (Est), `W` (Ouest)

**Commandes :**

| Caractère | Action                          |
|-----------|---------------------------------|
| `L`       | Tourner 90° à gauche            |
| `R`       | Tourner 90° à droite            |
| `M`       | Avancer d'une case              |
| `B`       | Reculer d'une case              |

**Exemple (`drones.txt`) :**
```
20 20
2
0 5 N LMLMLMLMM
5 0 S MMRMMRMRRM
```

---

## 🗺️ Légende de la Carte

| Symbole | Signification     |
|---------|-------------------|
| `^`     | Drone orienté Nord|
| `v`     | Drone orienté Sud |
| `>`     | Drone orienté Est |
| `<`     | Drone orienté Ouest|
| `#`     | Obstacle          |
| `.`     | Case vide         |

---

## 🧪 Tests

```bash
mvn test
```

**Tests couverts (`DroneTest.java`) :**
- ✅ Rotation gauche (4 cas)
- ✅ Rotation droite (4 cas)
- ✅ Avancer/Reculer dans les 4 directions
- ✅ Exemple du sujet : `2 5 N + LMLM → 1 4 S`
- ✅ Wrapping dans les 4 directions (bords de carte)
- ✅ Obstacle bloquant (drone reste sur place)
- ✅ `isBlocked()`, `wrapCoordinate()`, `getPosition()`, `getDisplayChar()`

---

## 🧩 Architecture et Conception

### Diagramme des packages

```
com.drone.simulator
├── model        → Drone (métier)
├── map          → SimulatorMap (grille + obstacles)
├── io           → ConfigurationReader (fichier → objets)
├── basic        → SimulatorBasic (version séquentielle)
├── concurrent   → SimulatorConcurrent, DroneWorker, DisplayWorker
├── server       → DroneServer, ServerConnectionHandler
└── client       → DroneClient, ConfigurationProvider, FileConfigurationProvider
```

### Synchronisation (Version Concurrente)

```
[DroneWorker-0] ──────┐
[DroneWorker-1] ───── ┼──► ReentrantLock/Condition ──► [DisplayWorker]
[DroneWorker-N] ──────┘
        │
        └──► CountDownLatch ──► Main (attend fin de tous)
```

---

## 📦 Remise

Le projet est soumis sous forme d'archive ZIP contenant :

```
drone-simulator.zip
├── pom.xml
├── src/
├── drones.txt
├── drones_test2.txt
├── drones_test3.txt
└── README.md
```

---

## 👤 Auteur

Projet réalisé dans le cadre du cours **Projet Java/N-Tiers**  
FSTS M1 RSI — 2025/2026 — Pr. A. Marzouk
