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

    // Intervalle de polling (ms)
    private static final int POLL_INTERVAL = 200;
    // Intervalle de log (toutes les 25 itérations = 5 secondes)
    private static final int LOG_INTERVAL = 25;

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
            if (lastConnected != currentConnected) {
                refCtrl.onConnectionStateReceived(currentConnected);
                lastConnected = currentConnected;
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
}
