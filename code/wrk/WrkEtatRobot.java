/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrk;

import ctrl.ICtrlEtatRobot;
import ch.emf.info.robot.links.Robot;

/**
 *
 * @author AudergonV01
 */
public class WrkEtatRobot extends Thread {

    private volatile boolean running;
    private final Robot robot;
    private final ICtrlEtatRobot refCtrl;
	private String test;

    private boolean lastConnected;

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
            _sleep(100); // Reduced frequency to avoid spam
            
            // Only get battery/image/audio if connected
            if (robot.isConnected()) {
                // Try both methods to get battery
                byte batteryLevel = robot.getBatteryLevel();
                byte batteryFromState = robot.getRobotState().getBattery();
                
                // Log once every 50 iterations (5 seconds)
                if (logCounter++ % 50 == 0) {
                    System.out.println("[BATTERY] getBatteryLevel(): " + (batteryLevel & 0xFF) + 
                                       ", getRobotState().getBattery(): " + (batteryFromState & 0xFF));
                }
                
                // Use the one that has a value (as int)
                int actualBattery = batteryFromState != 0 ? (batteryFromState & 0xFF) : (batteryLevel & 0xFF);
                refCtrl.onBatteryReceived(actualBattery);
                refCtrl.onImageReceived(robot.getLastImage());
                refCtrl.onAudioReceived(robot.getLastAudio());
            }
            
            if (lastConnected != robot.isConnected()) {
                refCtrl.onConnectionStateReceived(robot.isConnected());
            }
            lastConnected = robot.isConnected();
        
        }
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
