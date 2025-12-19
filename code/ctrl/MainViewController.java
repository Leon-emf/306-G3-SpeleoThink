/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import wrk.WrkEtatRobot;
import wrk.WrkAudio;
import wrk.WrkXboxController;
import bean.MyRobot;
import bean.XboxButton;
import ch.emf.info.robot.links.Robot;
import ch.emf.info.robot.links.exception.UnreachableRobotException;
import ch.emf.info.robot.links.bean.RobotState;
import ch.emf.info.robot.links.bean.Wifi;
import ch.emf.info.robot.links.listeners.RobotListener;
import java.util.ArrayList;
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
import javafx.scene.input.KeyEvent;
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
public class MainViewController implements Initializable, ICtrlEtatRobot, ICtrlXboxInput, Ctrl, RobotListener {

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
    @FXML
    private Label lblBatterieRover;
    @FXML
    private Label lblBatterieManette;

    private Robot robot;
    private MyRobot myRobot;
    private WrkAudio wrkAudio;
    private WrkEtatRobot wrkEtatRobot;
    private WrkXboxController wrkXboxController;
    private static final int STICK_MULTIPLIER = 200;
    private static final int MAX_SPEED = 2000;
    private boolean xboxControlEnabled = true; // Enabled by default for easier testing

    private ToggleButton btnJoystick;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtPw;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        robot = new Robot();
        robot.setListener(this); // Register this controller as the robot listener for callbacks
        wrkEtatRobot = new WrkEtatRobot(robot, this);
        wrkEtatRobot.start();
        wrkAudio = new WrkAudio();
        myRobot = new MyRobot();
        
        // Initialize Xbox controller support
        wrkXboxController = new WrkXboxController(this);
        wrkXboxController.start();
        
        // Setup keyboard input for testing (when no physical Xbox controller)
        setupKeyboardInput();
    }
    
    /**
     * Setup keyboard input as fallback for Xbox controller testing
     */
    private void setupKeyboardInput() {
        // Attach when the scene becomes available, even if it's created after initialize()
        boxCommands.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                attachKeyboardFilters(newScene);
            }
        });
        // If the scene is already ready, attach immediately
        Platform.runLater(() -> {
            if (boxCommands.getScene() != null) {
                attachKeyboardFilters(boxCommands.getScene());
            }
            // Click on image to regain focus for keyboard control
            imgView.setOnMouseClicked(e -> {
                imgView.requestFocus();
                System.out.println("[KEYBOARD] Focus set to image view");
            });
            imgView.setFocusTraversable(true);
        });
    }
    
    private void handleKeyPressed(KeyEvent event) {
        if (wrkXboxController != null) {
            wrkXboxController.updateFromKeyboard(event, true);
        }
    }
    
    private void handleKeyReleased(KeyEvent event) {
        if (wrkXboxController != null) {
            wrkXboxController.updateFromKeyboard(event, false);
        }
    }
    
    /**
     * Attach keyboard event filters to the scene.
     * Uses EVENT FILTERS (not handlers) to intercept keys BEFORE TextField consumes them.
     */
    private void attachKeyboardFilters(javafx.scene.Scene scene) {
        // Use addEventFilter to intercept events BEFORE they reach TextField
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Only intercept control keys, let text input pass through when in TextField
            if (isControlKey(event.getCode())) {
                handleKeyPressed(event);
                event.consume(); // Prevent TextField from receiving it
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (isControlKey(event.getCode())) {
                handleKeyReleased(event);
                event.consume();
            }
        });
        System.out.println("[KEYBOARD] Event filters attached to scene");
    }
    
    /**
     * Check if the key is a control key for the robot (not for text input)
     */
    private boolean isControlKey(javafx.scene.input.KeyCode code) {
        return code == javafx.scene.input.KeyCode.W ||
               code == javafx.scene.input.KeyCode.A ||
               code == javafx.scene.input.KeyCode.S ||
               code == javafx.scene.input.KeyCode.D ||
               code == javafx.scene.input.KeyCode.UP ||
               code == javafx.scene.input.KeyCode.DOWN ||
               code == javafx.scene.input.KeyCode.LEFT ||
               code == javafx.scene.input.KeyCode.RIGHT ||
               code == javafx.scene.input.KeyCode.SPACE ||
               code == javafx.scene.input.KeyCode.F || // LED toggle
               code == javafx.scene.input.KeyCode.L ||
               code == javafx.scene.input.KeyCode.H ||
               code == javafx.scene.input.KeyCode.U ||
               code == javafx.scene.input.KeyCode.J ||
               code == javafx.scene.input.KeyCode.Q ||
               code == javafx.scene.input.KeyCode.E;
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
    public void onBatteryReceived(int battery) {
        System.out.println("[BATTERY CALLBACK] Battery received: " + battery + "%");
        Platform.runLater(() -> {
            // Always display the battery value
            lblBatterieRover.setText("Batterie rover : " + battery + "%");
            
            // Change color based on battery level
            if (battery <= 20) {
                lblBatterieRover.setStyle("-fx-text-fill: red;");
            } else if (battery <= 50) {
                lblBatterieRover.setStyle("-fx-text-fill: orange;");
            } else {
                lblBatterieRover.setStyle("-fx-text-fill: lime;");
            }
        });
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
        if (wrkXboxController != null) {
            wrkXboxController.setRunning(false);
        }
        try {
            wrkEtatRobot.join();
            if (wrkXboxController != null) {
                wrkXboxController.join();
            }
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
    
    // ========== Xbox Controller Interface Implementation ==========
    
    // Track previous speeds to avoid console spam
    private short prevLeftSpeed = 0;
    private short prevRightSpeed = 0;
    private boolean prevConnected = true;
    
    @Override
    public void onXboxInputReceived(XboxButton xboxButton) {
        if (!xboxControlEnabled) {
            return; // Silent when disabled
        }

        if (!robot.isConnected()) {
            if (prevConnected) {
                System.out.println("[XBOX] Robot not connected!");
                prevConnected = false;
            }
            return;
        }
        prevConnected = true;
        
        // Calculate robot speeds based on left stick input
        // Y-axis: forward/backward, X-axis: turning
        double forward = -xboxButton.getLeftStickY(); // Inverted because stick Y is negative when up
        double turn = -xboxButton.getLeftStickX(); // INVERTED to fix left/right direction
        
        // Tank drive calculation
        // Left motor = forward - turn
        // Right motor = forward + turn
        double leftPower = forward - turn;
        double rightPower = forward + turn;
        
        // Normalize if values exceed 1.0
        double maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (maxPower > 1.0) {
            leftPower /= maxPower;
            rightPower /= maxPower;
        }
        
        // Convert to robot speed values (-999 to 999)
        short leftSpeed = (short) (leftPower * MAX_SPEED);
        short rightSpeed = (short) (rightPower * MAX_SPEED);
        
        // Apply speeds to robot (only log when changed)
        if (leftSpeed != prevLeftSpeed || rightSpeed != prevRightSpeed) {
            System.out.printf("[XBOX] speeds L:%d R:%d%n", leftSpeed, rightSpeed);
            prevLeftSpeed = leftSpeed;
            prevRightSpeed = rightSpeed;
        }
        robot.setLeftSpeed(leftSpeed);
        robot.setRightSpeed(rightSpeed);
        
        // Head control with D-Pad
        if (xboxButton.isdPadUp()) {
            robot.setHeadDirection(RobotState.HeadDirection.UP);
        } else if (xboxButton.isdPadDown()) {
            robot.setHeadDirection(RobotState.HeadDirection.DOWN);
        } else if (!xboxButton.isdPadUp() && !xboxButton.isdPadDown()) {
            robot.setHeadDirection(RobotState.HeadDirection.NONE);
        }
        
        // Bumpers for dock/undock
        if (xboxButton.isLeftBumper()) {
            robot.dock();
        }
        if (xboxButton.isRightBumper()) {
            robot.undock();
        }
    }
    
    @Override
    public void onButtonAPressed() {
        System.out.println("[XBOX] Button A pressed");
        if (robot.isConnected()) {
            robot.standUp();
        }
    }
    
    @Override
    public void onButtonBPressed() {
        // Toggle LED (infrared)
        System.out.println("[XBOX] Button B / F pressed - Toggle LED");
        if (robot.isConnected()) {
            boolean newState = !robot.getRobotState().isLedEnabled();
            robot.setLedEnabled(newState);
            System.out.println("[XBOX] LED infrarouge: " + (newState ? "ON" : "OFF"));
        } else {
            System.out.println("[XBOX] Cannot toggle LED - robot not connected");
        }
    }
    
    @Override
    public void onButtonXPressed() {
        // Toggle Xbox control mode
        xboxControlEnabled = !xboxControlEnabled;
        System.out.println("[XBOX] Mode toggle -> " + xboxControlEnabled);
        Platform.runLater(() -> {
            if (xboxControlEnabled) {
                lblHostName.setText(myRobot.getHostname() + " [XBOX MODE]");
                lblHostName.setStyle("-fx-text-fill: lime;");
            } else {
                lblHostName.setText(myRobot.getHostname());
                lblHostName.setStyle("");
                // Stop robot when disabling Xbox control
                robot.setLeftSpeed((short) 0);
                robot.setRightSpeed((short) 0);
            }
        });
    }
    
    @Override
    public void onButtonYPressed() {
        // Emergency stop
        System.out.println("[XBOX] Button Y pressed (STOP)");
        if (robot.isConnected()) {
            robot.setLeftSpeed((short) 0);
            robot.setRightSpeed((short) 0);
            robot.setHeadDirection(RobotState.HeadDirection.NONE);
        }
    }

    // ========== RobotListener implementation ==========
    // These callbacks are called directly by the Robot when it receives data
    // Note: onBatteryReceived(int), onImageReceived, onAudioReceived are shared with ICtrlEtatRobot
    
    @Override
    public void onWifiScanResultReceived(ArrayList<Wifi> wifiList) {
        System.out.println("[ROBOT LISTENER] WiFi scan result: " + wifiList.size() + " networks");
    }
    
    @Override
    public void onConnectionLost() {
        System.out.println("[ROBOT LISTENER] Connection lost!");
        Platform.runLater(() -> {
            connectionIndicator.setFill(Color.RED);
            lblBatterieRover.setText("Batterie: --");
        });
    }
    
    @Override
    public void onConnectionEtablished() {
        System.out.println("[ROBOT LISTENER] Connection established!");
        Platform.runLater(() -> {
            connectionIndicator.setFill(Color.LIME);
        });
    }
    
    @Override
    public void onConnectionClosed() {
        System.out.println("[ROBOT LISTENER] Connection closed");
        Platform.runLater(() -> {
            connectionIndicator.setFill(Color.GRAY);
        });
    }
    
    @Override
    public void onHostNameReceived(String hostName) {
        System.out.println("[ROBOT LISTENER] Hostname: " + hostName);
        Platform.runLater(() -> {
            lblHostName.setText(hostName);
            myRobot.setHostname(hostName);
        });
    }
    
    @Override
    public void onIdReceived(int id) {
        System.out.println("[ROBOT LISTENER] ID received: " + id);
        Platform.runLater(() -> {
            txtId.setText(String.valueOf(id));
        });
    }
    
    @Override
    public void onPwReceived(int pw) {
        System.out.println("[ROBOT LISTENER] PW received: " + pw);
        Platform.runLater(() -> {
            txtPw.setText(String.valueOf(pw));
        });
    }

}
