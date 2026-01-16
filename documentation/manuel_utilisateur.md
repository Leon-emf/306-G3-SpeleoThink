Voici le manuel utilisateur complet, structuré au format Markdown, intégrant la procédure de lancement spécifique pour un environnement de développement (via `Launcher.java`).

---

# Manuel Utilisateur - Application de Pilotage Robot 7Links

## 1. Introduction

Bienvenue dans le manuel de l'application de contrôle pour le robot 7Links. Ce logiciel permet de piloter le robot à distance depuis un ordinateur, en utilisant soit le clavier/souris, soit une manette Xbox compatible. L'application offre un retour vidéo en temps réel, l'enregistrement de séquences multimédias et la gestion de scripts de déplacement automatisés.

### Fonctionnalités Clés

* **Pilotage en temps réel :** Contrôle précis des moteurs et de la direction.
* **Support Manette Xbox :** Pilotage intuitif avec retour de force (vibrations en cas d'alerte).
* **Multimédia :** Affichage du flux caméra, enregistrement vidéo et retour audio.
* **Automatisation :** Création, sauvegarde et exécution de listes de commandes (scripts).

---

## 2. Prérequis Techniques

Avant d'utiliser l'application, assurez-vous que votre environnement respecte les critères suivants :

* **Système d'exploitation :** Windows 10 ou 11 (Requis pour la compatibilité native XInput / Manette Xbox).
* **Java :** JDK (Java Development Kit) version 17 ou supérieure.
* **Environnement de Développement (IDE) :** Eclipse, IntelliJ IDEA ou NetBeans avec support JavaFX.
* **Matériel :**
* Robot 7Links fonctionnel.
* Connexion Wi-Fi ou Bluetooth active.
* (Recommandé) Manette Xbox connectée au PC.



---

## 3. Installation et Exécution (Environnement de Développement)

Ce chapitre décrit la procédure pour importer et exécuter le projet au sein d'un IDE afin d'analyser le code source ou de tester l'application.

### 3.1 Architecture de Démarrage

L'application utilise une architecture JavaFX. Pour contourner les restrictions de modules des versions récentes de Java, le point d'entrée a été découplé de la classe principale de l'application.

* **Point d'entrée technique :** `Launcher.java`
* **Classe JavaFX principale :** `Main.java`

### 3.2 Procédure de Lancement

1. **Importation :** Importez le dossier du projet dans votre IDE. Assurez-vous que le dossier `resources` (contenant les images, CSS et vues FXML) est correctement inclus dans le *classpath* ou le *Build Path*.
2. **Localisation du Main :** Dans l'explorateur de projet, localisez le fichier **`Launcher.java`** (situé généralement à la racine des packages sources ou dans le package `app`).
3. **Exécution :**
* Effectuez un clic-droit sur le fichier `Launcher.java`.
* Sélectionnez l'option **Run As > Java Application**.

> **Note importante :** Il est impératif de lancer `Launcher.java` et non les contrôleurs individuels (comme `MainViewController`), car ces derniers nécessitent l'initialisation du contexte JavaFX pour fonctionner.

## 4. Initialisation et Connexion Réseau

La connexion au robot se fait en deux étapes : la configuration du robot sur le réseau Wi-Fi, puis la connexion de l'application au robot.

### 4.1 Préparation du Robot (Reset)

1. **Démarrer l'application** sur votre ordinateur.
2. Assurez-vous que le robot est **éteint**.
3. Maintenez appuyées simultanément les touches **POWER** et **RESET** du robot.
4. Attendez que le robot prononce la phrase : **"I'm ready"**.
5. À ce moment, le robot émet son propre signal Wi-Fi.

### 4.2 Configuration Wi-Fi (Appairage)

1. Sur votre ordinateur, ouvrez la liste des réseaux Wi-Fi disponibles.
2. Connectez-vous au réseau émis par le robot.
3. Si un mot de passe est demandé, entrez : **`link2014`**.
4. Une fois connecté au Wi-Fi du robot :
* Dans l'application, cliquez sur le bouton **"Initialiser le robot"**.
* Le nom du robot devrait s'afficher.


5. Cliquez sur **"Scanner les réseaux"**.

> **⚠️ Important :** Il arrive souvent que le premier scan échoue. Si c'est le cas :
> * Fermez la fenêtre de scan/configuration.
> * Cliquez à nouveau sur "Initialiser le robot".
> * Relancez "Scanner les réseaux".
> 
> 

6. Sélectionnez votre réseau Wi-Fi domestique (celui sur lequel vous voulez que le robot se connecte).
7. Entrez le mot de passe de votre Wi-Fi domestique et validez.
8. La tentative de connexion est envoyée. **Patientez 2 à 3 minutes.**
9. Le robot confirmera la réussite en prononçant à nouveau **"I'm ready"**.

### 4.3 Connexion à l'Interface

Maintenant que le robot est connecté à votre réseau :

1. Reconnectez votre ordinateur à votre réseau Wi-Fi habituel (Forcement il doit être le même que celui du robot...).
2. Récupérez l'**adresse IP** attribuée au robot par votre routeur (via l'interface de votre box internet ou un scanner d'IP).
3. Dans l'application (écran d'accueil) :
* **IP :** Entrez l'adresse IP récupérée.


1. Cliquez sur le bouton de connexion.



---

## 5. Interface de Pilotage (Tableau de Bord)

Une fois connecté, vous accédez à l'interface principale (`MainView`).

### A. Retour Vidéo et Audio

L'espace central affiche la vue caméra du robot.

* **Enregistrement Vidéo :** Cliquez sur le bouton "Record" pour sauvegarder le flux vidéo sur votre disque.
* **Audio :** Le son capté par le robot est diffusé via les haut-parleurs de votre PC.

### B. Contrôles de Mouvement

Vous pouvez piloter le robot de deux manières :

#### Option 1 : Interface Graphique / Clavier

Utilisez les boutons fléchés à l'écran ou les touches directionnelles du clavier pour :

* Avancer / Reculer
* Tourner à Gauche / Droite

#### Option 2 : Manette Xbox (Recommandé)

L'application détecte automatiquement les manettes Xbox connectées (via `WrkXboxController`).

* **Stick Gauche :** Contrôle la direction et la vitesse (progressif).
* **Boutons :**
  - **A :** Lever le robot (`standUp`).
  - **B :** Allumer/éteindre le LED infrarouge.
  - **X :** Bascule le mode contrôle Xbox (active/désactive le pilotage par manette).
  - **Y :** Arrêt d'urgence (stoppe immédiatement le robot).
  - **Start :** Démarrer/arrêter l'enregistrement vidéo.
  - **Guide :** Connecter/déconnecter le robot.
  - **LB (gâchette gauche) :** Docking du robot (stationnement/recharge).
  - **RB (gâchette droite) :** Undocking du robot (sortie du dock).
* **Vibrations :** La manette vibre sur appuis de boutons ou événements critiques (obstacles, erreurs).

### C. État du Robot

L'indicateur de connexion en bas de l'écran montre si le robot est connecté (vert) ou déconnecté (rouge).

## 6. Informations Développeur

* **Langage :** Java 17+
* **Interface Graphique :** JavaFX (FXML)
* **Librairies Externes :** JInput / XInput (Gestion manette), API Robot 7Links.
* **Logs :** En cas de problème technique, consultez la console de votre IDE pour voir les traces d'erreurs (`WrkIO`).