/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ctrl;

import app.beans.XboxButton;
import app.presentation.MainCtrl;
import app.workers.Wrk;
import javafx.application.Platform;

/**
 *
 * @author AzevedoZaralA
 */
public class Ctrl {

    private MainCtrl ihm;
    private Wrk wrk;

    public Ctrl()
    {
        ihm = new MainCtrl(this);
        wrk = new Wrk(this);
    }

    public void detect(XboxButton action) {
        ihm.setDetected(action);
    }

    public void connect(boolean connect) {
        wrk.connect(connect);
//        ihm.messageConnexion(connect);
    }

    public void setIhm(MainCtrl ihm) {
        this.ihm = ihm;
    }

    public void setWrk(Wrk wrk) {
        this.wrk = wrk;
    }

    public void quitter() {
        wrk.connect(false);
        Platform.exit();
    }

    public void start() {
        ihm.start();
    }

    public void erreurConnexion() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ihm.erreurConnexion();
            }
        });
    }
}
