/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.wrk;

import api_robot7links_test.ctrl.ICtrlEtatRobot;
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
        while (running) {
            _sleep(10);
            refCtrl.onBatteryReceived(robot.getBatteryLevel());
            refCtrl.onImageReceived(robot.getLastImage());
            refCtrl.onAudioReceived(robot.getLastAudio());
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
