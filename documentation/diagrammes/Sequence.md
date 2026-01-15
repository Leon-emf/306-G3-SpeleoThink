# 4.5 Diagrammes de séquence des interactions

Voici deux exemples de diagrammes de séquence pour illustrer les interactions principales du système :

## 1. Démarrage et initialisation de l'application

```mermaid
sequenceDiagram
    participant User
    participant MainViewController
    participant WindowManager
    participant WrkEtatRobot
    participant MyRobot
    User->>MainViewController: Lance l'application
    MainViewController->>WindowManager: Initialise la fenêtre principale
    MainViewController->>WrkEtatRobot: Initialise l'état du robot
    WrkEtatRobot->>MyRobot: Charge les paramètres du robot
    MainViewController-->>User: Affiche l'interface
```

## 2. Contrôle du robot via la manette Xbox

```mermaid
sequenceDiagram
    participant User
    participant XboxButton
    participant WrkXboxController
    participant MainViewController
    participant WrkEtatRobot
    participant MyRobot
    User->>XboxButton: Appuie sur un bouton
    XboxButton->>WrkXboxController: Met à jour l'état
    WrkXboxController->>MainViewController: Notifie l'événement
    MainViewController->>WrkEtatRobot: Demande une action sur le robot
    WrkEtatRobot->>MyRobot: Exécute l'action
    MainViewController-->>User: Affiche le retour visuel
```

*Ces diagrammes peuvent être complétés selon les scénarios spécifiques de l'application.*
