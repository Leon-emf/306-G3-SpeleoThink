/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.ctrl;

import api_robot7links_test.bean.Fenetre;
import api_robot7links_test.bean.MyRobot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author AudergonV01
 */
public class MenuViewController implements Initializable, ICtrlInit, Ctrl {

    private ListViewController listCtrl;
    private Fenetre fList;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fList = WindowManager.creerFenetre("/api_robot7links_test/view/ListView.fxml", "Sélectionner un robot");
        listCtrl = (ListViewController) fList.getCtrl();
        fList.getStage().setOnCloseRequest(e -> listCtrl.onClose(e));

    }

    public void onClose(WindowEvent e) {
        close();
    }

    @FXML
    private void connecterRobot(ActionEvent event) {
        fList.show();
    }

    @FXML
    private void initialiserRobot(ActionEvent event) {
        Fenetre f = WindowManager.creerFenetre("/api_robot7links_test/view/InitView.fxml", "Initialiser un robot");
        InitViewController ctrl = (InitViewController) f.getCtrl();
        ctrl.setRefCtrl(this);
        f.getStage().setOnCloseRequest(e -> ctrl.onClose(e));
        f.show();
    }

    @FXML
    private void quitter(ActionEvent event) {
        close();
    }

    private void close() {
        System.exit(0);
    }

    @Override
    public void onFinish(MyRobot robot) {
        if (robot != null) {
            listCtrl.addRobot(robot);
        }
    }

}
