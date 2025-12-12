/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.workers;

import app.beans.XboxButton;
import app.ctrl.Ctrl;
import ch.aplu.xboxcontroller.XboxController;
import javafx.application.Platform;

/**
 *
 * @author AzevedoZaralA
 */
public class Wrk {

    private Xbone xb;
    private XboxController xc;
    private Ctrl ctrl;

    public Wrk(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    public void button(XboxButton control) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ctrl.detect(control);
            }
        });

    }

    public boolean connect(boolean connexion) {
        boolean retour = connexion;
        if (connexion) {
            xb = new Xbone();
            xb.setWrk(this);
            xb.connecterManette();
            retour = true;
        } else {
            if (xb != null) {
                xb.deconnecterManette();
            }
            retour = false;
        }
        return retour;
    }

    public void etatConnexion(boolean connected) {
        if (!connected) {
            ctrl.erreurConnexion();
            xb.deconnecterManette();
        }
    }

    public void setCtrl(Ctrl ctrl) {
        this.ctrl = ctrl;
    }
}
