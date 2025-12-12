package app.workers;

import ch.aplu.xboxcontroller.*;
import app.beans.XboxButton;
import javax.swing.JOptionPane;

public class Xbone {

    private Wrk wrk;
    private final static XboxController xc = new XboxController();
    private static double magnitudeGauche;
    private static double magnitudeDroite;
    private static boolean isStart;

    public Xbone() {
    }

    public void connecterManette() {
//        if (xc == null) {
//            this.xc = new XboxController();
//        }
        if (!xc.isConnected()) {
            JOptionPane.showMessageDialog(null,
                    "Connexion échouée.",
                    "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
            xc.release();
            return;
        }
        xc.addXboxControllerListener(new XboxControllerAdapter() {
            @Override
            public void leftTrigger(double value) {
                boolean pressed = (value > 0.0) ? true : false;
                button("Left Trigger", pressed);
            }

            @Override
            public void rightTrigger(double value) {
                boolean pressed = (value > 0.0) ? true : false;
                button("Right Trigger", pressed);
            }

            @Override
            public void buttonY(boolean pressed) {
                button("Y", pressed);
            }

            @Override
            public void buttonX(boolean pressed) {
                button("X", pressed);
            }

            @Override
            public void buttonA(boolean pressed) {
                button("A", pressed);
            }

            @Override
            public void buttonB(boolean pressed) {
                button("B", pressed);
            }

            @Override
            public void leftThumbMagnitude(double magnitude) {
                magnitudeGauche = magnitude;
            }

            @Override
            public void leftThumbDirection(double direction) {
                boolean pressed = false;
                String joystic = "Joystic Gauche - ";
                if ((direction < 45) || (direction >= 315)) {
                    joystic += "Haut";
                    pressed = (magnitudeGauche > 0.7) ? true : false;
                } else if ((direction < 135) && (direction >= 45)) {
                    joystic += "Droite";
                    pressed = (magnitudeGauche > 0.7) ? true : false;
                } else if ((direction < 225) && (direction >= 135)) {
                    joystic += "Bas";
                    pressed = (magnitudeGauche > 0.7) ? true : false;
                } else if ((direction < 315) && (direction >= 225)) {
                    joystic += "Gauche";
                    pressed = (magnitudeGauche > 0.7) ? true : false;
                }
                button(joystic, pressed);
            }

            @Override
            public void rightThumbMagnitude(double magnitude) {
                magnitudeDroite = magnitude;
            }

            @Override
            public void rightThumbDirection(double direction) {
                boolean pressed = false;
                String joystic = "Joystic Droit - ";
                if ((direction < 45) || (direction >= 315)) {
                    joystic += "Haut";
                    pressed = (magnitudeDroite > 0.7) ? true : false;
                } else if ((direction < 135) && (direction >= 45)) {
                    joystic += "Droite";
                    pressed = (magnitudeDroite > 0.7) ? true : false;
                } else if ((direction < 225) && (direction >= 135)) {
                    joystic += "Bas";
                    pressed = (magnitudeDroite > 0.7) ? true : false;
                } else if ((direction < 315) && (direction >= 225)) {
                    joystic += "Gauche";
                    pressed = (magnitudeDroite > 0.7) ? true : false;
                } else {
                    joystic += "Tous";
                    pressed = false;
                }
                button(joystic, pressed);
            }

            @Override
            public void dpad(int direction, boolean pressed) {
                String button = null;
                switch (direction) {
                    case 0:
                        button = "Haut";
                        break;
                    case 1:
                        button = "Haut + Droite";
                        break;
                    case 2:
                        button = "Droite";
                        break;
                    case 3:
                        button = "Bas + Droite";
                        break;
                    case 4:
                        button = "Bas";
                        break;
                    case 5:
                        button = "Bas + Gauche";
                        break;
                    case 6:
                        button = "Gauche";
                        break;
                    case 7:
                        button = "Haut + Gauche";
                        break;
                }
                button(button, pressed);
            }

            @Override
            public void rightShoulder(boolean pressed) {
                button("Shoulder Droit", pressed);
            }

            @Override
            public void leftShoulder(boolean pressed) {
                button("Shoulder Gauche", pressed);
            }

            @Override
            public void start(boolean pressed) {
                button("start", pressed);
                if (pressed) {
                    xc.release();
                    System.exit(0);
                }
            }

            @Override
            public void isConnected(boolean connected) {
                wrk.etatConnexion(connected);
            }

            private void button(String nom, boolean pressed) {
                XboxButton retour = new XboxButton(nom, pressed);
                wrk.button(retour);
                if (pressed) {
                    xc.vibrate(20000, 20000, 100);
                }
            }
        });
    }

    public void deconnecterManette() {
        xc.release();
    }

    public void setWrk(Wrk wrk) {
        this.wrk = wrk;
    }

}

//    public static void main(String[] args) {
//        new Xbone();
//    }

