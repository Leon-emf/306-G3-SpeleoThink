# 4.2 Diagramme Entité-Relation (ERD)

Le projet ne semble pas utiliser une base de données relationnelle classique, mais il existe des entités manipulées (robot, fenêtre, etc.) qui peuvent être modélisées pour une éventuelle persistance ou pour clarifier les relations.

```mermaid
erDiagram
    MYROBOT ||--o{ FENETRE : "possède"
    MYROBOT {
        int id
        String nom
        ...
    }
    FENETRE {
        int id
        String nom
        ...
    }
    XBOXBUTTON ||--|| MYROBOT : "contrôle"
    XBOXBUTTON {
        boolean boutonA
        boolean boutonB
        float joystickGaucheX
        ...
    }
```

- **MyRobot** : Représente le robot piloté.
- **Fenetre** : Représente une fenêtre de l'application ou une configuration.
- **XboxButton** : Représente l'état des boutons de la manette Xbox.

*Ce diagramme peut être adapté si une base de données réelle est ajoutée.*
