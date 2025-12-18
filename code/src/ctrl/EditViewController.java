/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.ctrl;

import api_robot7links_test.bean.MyRobot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author AudergonV01
 */
public class EditViewController implements Initializable, Ctrl {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtID;
    @FXML
    private TextField txtPW;

    private MyRobot robot;
    private ICtrlEdit refCtrl;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setRefCtrl(ICtrlEdit refCtrl) {
        this.refCtrl = refCtrl;
    }

    @FXML
    private void annuler(ActionEvent event) {
        close();
    }

    @FXML
    private void save(ActionEvent event) {
        if (refCtrl != null) {
            refCtrl.onSave(saveRobot());
        }
        close();
    }

    private void close() {
        ((Stage) (txtID.getScene().getWindow())).close();
    }

    public void setRobot(MyRobot robot) {
        this.robot = robot;
        txtID.setText(robot.getId() + "");
        txtPW.setText(robot.getPw() + "");
        txtNom.setText(robot.getHostname());
        txtIP.setText(robot.getIp());
    }

    private MyRobot saveRobot() {
        if (robot == null) {
            robot = new MyRobot();
        }
        robot.setHostname(txtNom.getText());
        int id = 0;
        try {
            id = Integer.parseInt(txtID.getText());
        } catch (NumberFormatException ex) {
            WindowManager.afficherAlerte("L'ID donné n'est pas valide ! ID défini à 0");
        }
        robot.setId(id);
        int pw = 0;
        try {
            pw = Integer.parseInt(txtPW.getText());
        } catch (NumberFormatException ex) {
            WindowManager.afficherAlerte("Le PW donné n'est pas valide ! PW défini à 0");
        }
        robot.setPw(pw);
        robot.setIp(txtIP.getText());
        return robot;
    }

}
