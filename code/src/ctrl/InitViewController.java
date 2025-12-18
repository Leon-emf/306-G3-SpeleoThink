/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.ctrl;

import api_robot7links_test.bean.Fenetre;
import api_robot7links_test.bean.MyRobot;
import ch.emf.info.robot.links.Robot;
import ch.emf.info.robot.links.bean.Wifi;
import ch.emf.info.robot.links.exception.UnreachableRobotException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author AudergonV01
 */
public class InitViewController implements Initializable, ICtrlPassword, Ctrl {

    @FXML
    private BorderPane pane;
    @FXML
    private Button btnInit;
    @FXML
    private Button btnScanner;
    @FXML
    private ListView<Wifi> lstWifi;
    @FXML
    private Button btnConnecter;
    @FXML
    private Button btnAnnuler;
    @FXML
    private Button btnTerminer;

    private Wifi wifi;
    @FXML
    private Label lblHostname;

    private ICtrlInit refCtrl;
    private Robot robot;
    @FXML
    private ProgressIndicator loaderWifi;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        robot = new Robot();
    }

    public void setRefCtrl(ICtrlInit refCtrl) {
        this.refCtrl = refCtrl;
    }

    public void onClose(WindowEvent e) {
        close();
    }

    private void close() {
        robot.stopInitConnection();
        ((Stage) (pane.getScene().getWindow())).close();
    }

    @FXML
    private void init(ActionEvent event) {
        try {
            boolean ok = robot.startInitConnection();
            if (ok) {
                btnScanner.setDisable(false);
                lblHostname.setText(robot.getRobotState().getName());
                lblHostname.setVisible(true);
            }
        } catch (UnreachableRobotException ex) {
            WindowManager.afficherErreur(ex.getMessage());
        }
    }

    @FXML
    private void scan(ActionEvent event) {
        loaderWifi.setVisible(true);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    ArrayList<Wifi> wifis = robot.scanWifiSync();
                    if (!wifis.isEmpty()) {
                        lstWifi.getItems().setAll(wifis);
                        lstWifi.setDisable(false);
                        btnConnecter.setDisable(false);
                    } else {
                        WindowManager.afficherInfo("Aucun réseau wifi trouvé.");
                    }
                } finally {
                    Platform.runLater(() -> {
                        loaderWifi.setVisible(false);
                    });
                }
            }
        };
        t.start();

    }

    @FXML
    private void connect(ActionEvent event) {
        Wifi w = lstWifi.getSelectionModel().getSelectedItem();
        if (w != null) {
            wifi = w;
            if (!wifi.getSecurityType().equals(Wifi.SecurityType.OPEN)) {
                Fenetre f = WindowManager.creerFenetre("/api_robot7links_test/view/PasswordView.fxml", "Mot de passe");
                PasswordViewController ctrl = (PasswordViewController) f.getCtrl();
                ctrl.setRefCtrl(this);
                f.show();
            } else {
                onPasswordReceived("");
            }
        }
    }

    @FXML
    private void annuler(ActionEvent event) {
        close();
    }

    @FXML
    private void terminer(ActionEvent event) {
        if (refCtrl != null) {
            refCtrl.onFinish(new MyRobot("0.0.0.0", robot.getRobotState().getName(), robot.getRobotState().getId(), robot.getRobotState().getPw()));
        }
        close();
    }

    @Override
    public void onPasswordReceived(String password) {
        wifi.setPassword(password);
        if (robot.connectWifiSync(wifi)) {
            btnTerminer.setDisable(false);
            WindowManager.afficherInfo("Ordre de connexion envoyé. Attendez la réponse vocale du robot (\"I'm ready\") (ou \"I can not connect to the wireless network\" en cas d'erreur.)");
        } else {
            WindowManager.afficherErreur(robot.getLastError());
        }
    }

}
