# 4.3 Modèle relationnel de la base de données

Le projet ne comporte pas de base de données SQL, mais voici un exemple de modèle relationnel basé sur les entités manipulées (pour une éventuelle persistance future) :

| Table        | Clé primaire | Attributs principaux                | Clés étrangères         |
|--------------|--------------|-------------------------------------|------------------------|
| MYROBOT      | id           | nom, ...                            |                        |
| FENETRE      | id           | nom, ...                            | id_robot (MYROBOT.id)  |
| XBOXBUTTON   | id           | boutonA, boutonB, joystickGaucheX…  | id_robot (MYROBOT.id)  |

## DDL SQL exemple
```sql
CREATE TABLE MYROBOT (
    id INT PRIMARY KEY,
    nom VARCHAR(100)
    -- autres attributs
);

CREATE TABLE FENETRE (
    id INT PRIMARY KEY,
    nom VARCHAR(100),
    id_robot INT,
    FOREIGN KEY (id_robot) REFERENCES MYROBOT(id)
    -- autres attributs
);

CREATE TABLE XBOXBUTTON (
    id INT PRIMARY KEY,
    boutonA BOOLEAN,
    boutonB BOOLEAN,
    joystickGaucheX FLOAT,
    id_robot INT,
    FOREIGN KEY (id_robot) REFERENCES MYROBOT(id)
    -- autres attributs
);
```

*Ce modèle est à adapter selon les besoins réels de persistance.*
