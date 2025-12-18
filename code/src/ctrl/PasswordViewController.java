/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.ctrl;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author AudergonV01
 */
public class PasswordViewController implements Initializable, Ctrl {

    @FXML
    private BorderPane pane;
    @FXML
    private PasswordField txtPassword;

    /**
     * Initializes the controller class.
     */
    ICtrlPassword refCtrl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void ok(ActionEvent event) {
        if (refCtrl != null) {
            String password = txtPassword.getText();
            refCtrl.onPasswordReceived(password);
            ((Stage) (pane.getScene().getWindow())).close();
        }
    }

    public void setRefCtrl(ICtrlPassword refCtrl) {
        this.refCtrl = refCtrl;
    }

}
