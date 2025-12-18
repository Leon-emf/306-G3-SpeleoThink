/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.ctrl;

import api_robot7links_test.wrk.WrkEtatRobot;
import api_robot7links_test.wrk.WrkAudio;
import api_robot7links_test.bean.MyRobot;
import ch.emf.info.robot.links.Robot;
import ch.emf.info.robot.links.exception.UnreachableRobotException;
import ch.emf.info.robot.links.bean.RobotState;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author AudergonV01
 */
public class MainViewController implements Initializable, ICtrlEtatRobot, Ctrl {

    @FXML
    private TextField txtIp1;
    @FXML
    private TextField txtIp2;
    @FXML
    private TextField txtIp3;
    @FXML
    private TextField txtIp4;
    @FXML
    private HBox boxCommands;
    @FXML
    private ImageView imgView;
    @FXML
    private ProgressIndicator loader;
    @FXML
    private Circle connectionIndicator;
    @FXML
    private Button btnOnOff;
    @FXML
    private TextField txtDire;
    @FXML
    private Label lblHostName;

    private Robot robot;
    private MyRobot myRobot;
    private WrkAudio wrkAudio;
    private WrkEtatRobot wrkEtatRobot;
    private static final int STICK_MULTIPLIER = 200;

    private ToggleButton btnJoystick;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtPw;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        robot = new Robot();
        wrkEtatRobot = new WrkEtatRobot(robot, this);
        wrkEtatRobot.start();
        wrkAudio = new WrkAudio();
        myRobot = new MyRobot();
    }

    @FXML
    private void dock(ActionEvent event) {
        robot.dock();
    }

    @FXML
    private void undock(ActionEvent event) {
        robot.undock();
    }

    @FXML
    private void headUp(ActionEvent event) {
        robot.setHeadDirection(RobotState.HeadDirection.UP);
    }

    @FXML
    private void headNeutre(ActionEvent event) {
        robot.setHeadDirection(RobotState.HeadDirection.NONE);
    }

    @FXML
    private void headDown(ActionEvent event) {
        robot.setHeadDirection(RobotState.HeadDirection.DOWN);
    }

    @FXML
    private void neutre(ActionEvent event) {
        robot.setRightSpeed((short) 0);
        robot.setLeftSpeed((short) 0);
    }

    @FXML
    private void gauche(ActionEvent event) {
        robot.setRightSpeed((short) 600);
        robot.setLeftSpeed((short) 200);
    }

    @FXML
    private void droite(ActionEvent event) {
        robot.setRightSpeed((short) 200);
        robot.setLeftSpeed((short) 600);
    }

    @FXML
    private void avant(ActionEvent event) {
        robot.setRightSpeed((short) 999);
        robot.setLeftSpeed((short) 999);
    }

    @FXML
    private void arriere(ActionEvent event) {
        robot.setRightSpeed((short) -600);
        robot.setLeftSpeed((short) -600);
    }

    @FXML
    private void avantGauche(ActionEvent event) {
    }

    @FXML
    private void avantDroite(ActionEvent event) {
    }

    @FXML
    private void led(ActionEvent event) {
        robot.setLedEnabled(!robot.getRobotState().isLedEnabled());
    }

    @FXML
    private void standUp(ActionEvent event) {
        robot.standUp();
    }

    @Override
    public void onBatteryReceived(byte battery) {
    }

    @Override
    public void onImageReceived(byte[] image) {
        Platform.runLater(() -> {
            imgView.setImage(new Image(new ByteArrayInputStream(image)));
        });
    }

    @Override
    public void onAudioReceived(byte[] audio) {
    }

    public void onClose(WindowEvent e) {
        close(e);
    }

    private void connect() {
        loader.setVisible(true);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    String ip = getIP();
                    if (ip != null) {
                        robot.connect(ip, myRobot.getId(), myRobot.getPw());
                        if (!robot.isConnected()) {
                            WindowManager.afficherErreur(robot.getLastError());
                        }
                    }
                } catch (UnreachableRobotException ex) {
                    WindowManager.afficherErreur(ex.getMessage());
                } finally {
                    Platform.runLater(() -> {
                        loader.setVisible(false);
                    });
                }
            }
        };
        t.start();
    }

    private String getIP() {
        String ip = null;
        String b1 = txtIp1.getText();
        String b2 = txtIp2.getText();
        String b3 = txtIp3.getText();
        String b4 = txtIp4.getText();
        if (b1.isEmpty() | b2.isEmpty() | b3.isEmpty() | b4.isEmpty()) {
            WindowManager.afficherAlerte("L'adresse ip n'est pas complète.");
        } else {
            try {
                Integer.parseInt(b1);
                Integer.parseInt(b2);
                Integer.parseInt(b3);
                Integer.parseInt(b4);
                ip = b1 + "." + b2 + "." + b3 + "." + b4;
            } catch (NumberFormatException ex) {
                WindowManager.afficherAlerte("L'adresse ip n'est pas valide.");
            }
        }
        return ip;
    }

    private void off(ActionEvent event) {
        if (!robot.disconnect()) {
            WindowManager.afficherErreur(robot.getLastError());
        }
    }

    @Override
    public void onConnectionStateReceived(boolean state) {
        Platform.runLater(() -> {
            connectionIndicator.setFill(state ? Color.LIME : Color.RED);
            btnOnOff.setText(state ? "OFF" : "ON");
            imgView.setVisible(state);
        });
    }

    @FXML
    private void onOff(ActionEvent event) {
        if (robot.isConnected()) {
            robot.disconnect();
        } else {
            connect();
        }
    }

    private void close(Event event) {
        ((Stage) (btnOnOff.getScene().getWindow())).close();
        wrkEtatRobot.setRunning(false);
        try {
            wrkEtatRobot.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        robot.disconnect();
    }

    @FXML
    private void onBtnClose(ActionEvent event) {
        close(event);
    }

    @FXML
    private void dire(ActionEvent event) {
        String dire = txtDire.getText();
        txtDire.setText("");
        if (dire.length() > 0) {
            byte[] data = wrkAudio.textToSpeech(dire);
            if (data != null) {
                robot.sendAudio(data);
            } else {
                WindowManager.afficherErreur("Erreur survenue lors du TextToSpeech.");
            }
        }
    }

    public void setRobot(MyRobot myRobot) {
        if (myRobot != null) {
            this.myRobot = myRobot;
            lblHostName.setText(myRobot.getHostname());
            txtId.setText(myRobot.getId()+"");
            txtPw.setText(myRobot.getPw()+"");
            String[] ipParts = myRobot.getIp().split("\\.");
            if (ipParts.length == 4) {
                txtIp1.setText(ipParts[0]);
                txtIp2.setText(ipParts[1]);
                txtIp3.setText(ipParts[2]);
                txtIp4.setText(ipParts[3]);
            } else {
                WindowManager.afficherAlerte("L'adresse ip du robot est invalide. (" + myRobot.getIp() + ")");
            }
        }
    }

}
