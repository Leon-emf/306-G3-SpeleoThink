# 4.4 Diagrammes de classes

Voici un diagramme de classes simplifié représentant les principales entités et relations du projet :

```mermaid
classDiagram
    class MyRobot {
        +int id
        +String nom
        +... // autres attributs
    }
    class Fenetre {
        +int id
        +String nom
        +... // autres attributs
    }
    class XboxButton {
        +boolean boutonA
        +boolean boutonB
        +float joystickGaucheX
        +... // autres attributs
    }
    class MainViewController {
        +MyRobot robot
        +WindowManager windowManager
        +WrkEtatRobot wrkEtatRobot
        +WrkXboxController wrkXboxController
        +WrkVideoRecorder wrkVideoRecorder
        +WrkAudio wrkAudio
        +... // méthodes
    }
    class WindowManager {
        +... // méthodes de gestion de fenêtres
    }
    class WrkEtatRobot {
        +MyRobot robot
        +... // méthodes de gestion d'état
    }
    class WrkXboxController {
        +XboxButton xboxButton
        +... // méthodes de gestion manette
    }
    class WrkVideoRecorder {
        +... // méthodes d'enregistrement vidéo
    }
    class WrkAudio {
        +... // méthodes audio
    }
    MyRobot "1" --o "*" Fenetre : possède
    MyRobot "1" --o "1" XboxButton : contrôléPar
    MainViewController --> WindowManager
    MainViewController --> WrkEtatRobot
    MainViewController --> WrkXboxController
    MainViewController --> WrkVideoRecorder
    MainViewController --> WrkAudio
    WrkEtatRobot --> MyRobot
    WrkXboxController --> XboxButton
```

*Ce diagramme peut être détaillé selon les besoins (méthodes, attributs, interfaces, etc.).*
