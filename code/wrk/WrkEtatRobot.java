/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrk;

import ctrl.ICtrlEtatRobot;
import ch.emf.info.robot.links.Robot;
import ch.emf.info.robot.links.bean.RobotState;

/**
 * Thread de surveillance de l'état du robot 7links HSR-2.nv
 * Récupère périodiquement la batterie, l'image et l'audio du robot.
 *
 * @author AudergonV01
 */
public class WrkEtatRobot extends Thread {

    private volatile boolean running;
    private final Robot robot;
    private final ICtrlEtatRobot refCtrl;

    private boolean lastConnected;
    private int lastBattery = -1; // Pour éviter les mises à jour inutiles
    
    // Auto-reconnexion
    private boolean autoReconnectEnabled = true;
    private int disconnectCount = 0;
    private long lastDisconnectTime = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 3;
    private static final long RECONNECT_COOLDOWN_MS = 5000; // 5 secondes entre les tentatives
    private String lastIp = null;
    private int lastId = 0;
    private int lastPw = 0;

    // Intervalle de polling (ms)
    private static final int POLL_INTERVAL = 200;
    // Intervalle de log (toutes les 25 itérations = 5 secondes)
    private static final int LOG_INTERVAL = 25;
    
    // Compteur pour détecter les déconnexions silencieuses
    private int noDataCount = 0;
    private static final int MAX_NO_DATA_COUNT = 15; // 3 secondes sans données = déconnexion

    public WrkEtatRobot(Robot robot, ICtrlEtatRobot refCtrl) {
        super("Thread Etat Robot");
        this.robot = robot;
        this.refCtrl = refCtrl;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        running = true;
        int logCounter = 0;
        
        while (running) {
            _sleep(POLL_INTERVAL);
            
            // Gérer l'état de connexion
            boolean currentConnected = robot.isConnected();
            
            // Détecter changement d'état de connexion
            if (lastConnected != currentConnected) {
                refCtrl.onConnectionStateReceived(currentConnected);
                lastConnected = currentConnected;
                
                // Si déconnexion détectée, tenter auto-reconnexion
                if (!currentConnected && autoReconnectEnabled && lastIp != null) {
                    handleDisconnection();
                }
                
                // Reset compteur si connecté
                if (currentConnected) {
                    disconnectCount = 0;
                    noDataCount = 0;
                }
            }
            
            // Récupérer les données uniquement si connecté
            if (currentConnected) {
                try {
                    // Récupérer la batterie via RobotState (méthode la plus fiable)
                    int battery = getBatteryPercentage();
                    
                    // Log périodique pour debug
                    if (logCounter++ % LOG_INTERVAL == 0) {
                        System.out.println("[BATTERY] Niveau de batterie: " + battery + "%");
                    }
                    
                    // Notifier seulement si la valeur a changé (évite le spam)
                    if (battery != lastBattery) {
                        refCtrl.onBatteryReceived(battery);
                        lastBattery = battery;
                    }
                    
                    // Récupérer l'image et l'audio
                    byte[] image = robot.getLastImage();
                    if (image != null && image.length > 0) {
                        refCtrl.onImageReceived(image);
                        noDataCount = 0; // Reset compteur si on reçoit des données
                    } else {
                        noDataCount++;
                        // Déconnexion silencieuse détectée
                        if (noDataCount >= MAX_NO_DATA_COUNT) {
                            System.out.println("[WrkEtatRobot] Pas de données depuis " + (noDataCount * POLL_INTERVAL / 1000) + "s - connexion instable");
                            noDataCount = 0;
                        }
                    }
                    
                    byte[] audio = robot.getLastAudio();
                    if (audio != null && audio.length > 0) {
                        refCtrl.onAudioReceived(audio);
                    }
                    
                } catch (Exception e) {
                    System.err.println("[WrkEtatRobot] Erreur lors de la récupération des données: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Récupère le pourcentage de batterie du robot.
     * Essaie plusieurs méthodes pour obtenir une valeur valide.
     * 
     * @return pourcentage de batterie (0-100), ou 0 si non disponible
     */
    private int getBatteryPercentage() {
        int battery = 0;
        
        try {
            // Méthode 1: Via RobotState (généralement plus fiable car mis à jour automatiquement)
            RobotState state = robot.getRobotState();
            if (state != null) {
                byte rawBattery = state.getBattery();
                battery = rawBattery & 0xFF; // Convertir byte signé en int non-signé
            }
            
            // Méthode 2: Si RobotState retourne 0, essayer getBatteryLevel()
            if (battery == 0) {
                byte rawBattery = robot.getBatteryLevel();
                battery = rawBattery & 0xFF;
            }
            
            // Limiter entre 0 et 100 (au cas où la valeur serait mal formatée)
            if (battery > 100) {
                // Si la valeur est > 100, c'est peut-être une valeur brute (0-255)
                // Dans ce cas, la convertir en pourcentage
                battery = (battery * 100) / 255;
            }
            
            battery = Math.max(0, Math.min(100, battery));
            
        } catch (Exception e) {
            System.err.println("[BATTERY] Erreur: " + e.getMessage());
        }
        
        return battery;
    }

    private void _sleep(int millis) {
        try {
            sleep(millis);
        } catch (InterruptedException ex) {
            System.err.println("Erreur lors du sleep du thread " + super.getName()
                    + ". \n" + ex.getMessage());
        }
    }
    
    // ========== Auto-reconnexion ==========
    
    /**
     * Sauvegarde les paramètres de connexion pour l'auto-reconnexion
     */
    public void setConnectionParams(String ip, int id, int pw) {
        this.lastIp = ip;
        this.lastId = id;
        this.lastPw = pw;
    }
    
    /**
     * Active ou désactive l'auto-reconnexion
     */
    public void setAutoReconnectEnabled(boolean enabled) {
        this.autoReconnectEnabled = enabled;
    }
    
    /**
     * Gère une déconnexion et tente une auto-reconnexion
     */
    private void handleDisconnection() {
        long now = System.currentTimeMillis();
        
        // Vérifier le cooldown
        if (now - lastDisconnectTime < RECONNECT_COOLDOWN_MS) {
            return;
        }
        
        lastDisconnectTime = now;
        disconnectCount++;
        
        if (disconnectCount <= MAX_RECONNECT_ATTEMPTS) {
            System.out.println("[WrkEtatRobot] Déconnexion détectée - Tentative de reconnexion " 
                    + disconnectCount + "/" + MAX_RECONNECT_ATTEMPTS + " dans 3.5s...");
            
            // Attendre 3.5 secondes avant de reconnecter (évite les erreurs)
            _sleep(3500);
            
            try {
                robot.connect(lastIp, lastId, lastPw);
                if (robot.isConnected()) {
                    System.out.println("[WrkEtatRobot] ✓ Reconnexion réussie!");
                    disconnectCount = 0;
                } else {
                    System.out.println("[WrkEtatRobot] ✗ Échec de reconnexion");
                }
            } catch (Exception e) {
                System.err.println("[WrkEtatRobot] Erreur reconnexion: " + e.getMessage());
            }
        } else {
            System.out.println("[WrkEtatRobot] Nombre max de tentatives atteint (" 
                    + MAX_RECONNECT_ATTEMPTS + ") - Arrêt auto-reconnexion");
            // Reset après un délai plus long
            if (now - lastDisconnectTime > 30000) { // 30 secondes
                disconnectCount = 0;
            }
        }
    }
    
    /**
     * Force une reconnexion immédiate
     */
    public void forceReconnect() {
        if (lastIp != null) {
            disconnectCount = 0;
            lastDisconnectTime = 0;
            handleDisconnection();
        }
    }
}
