/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import bean.Fenetre;
import bean.MyRobot;
import wrk.WrkIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author AudergonV01
 */
public class ListViewController implements Initializable, ICtrlEdit, Ctrl {

    @FXML
    private ListView<MyRobot> lstRobot;

    private static final String SAVE_PATH = "robots.json";

    private ArrayList<MyRobot> robots;
    private WrkIO wrkIO;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        wrkIO = new WrkIO();
        robots = new ArrayList<>();
        MyRobot[] arr = (MyRobot[]) wrkIO.jsonToObj(SAVE_PATH, MyRobot[].class);
        if (arr != null) {
            robots.addAll(Arrays.asList(arr));
            refreshList();
        }
    }

    @FXML
    private void select(ActionEvent event) {
        MyRobot selectedRobot = lstRobot.getSelectionModel().getSelectedItem();
        if (selectedRobot != null) {
            openMainView(selectedRobot);
        } else {
            WindowManager.afficherAlerte("Merci de sélectionner un robot !");
        }
    }

    @FXML
    private void manuel(ActionEvent event) {
        openMainView(new MyRobot());
    }

    private void openMainView(MyRobot robot) {
        Fenetre f = WindowManager.creerFenetre("view/MainView.fxml", "Pilotage");
        MainViewController ctrl = (MainViewController) f.getCtrl();
        if (ctrl == null) {
            WindowManager.afficherErreur("Impossible d'ouvrir la vue principale. Le contrôleur n'a pas pu être chargé.");
            return;
        }
        ctrl.setRobot(robot);
        f.getStage().setOnCloseRequest(e -> ctrl.onClose(e));
        f.show();
    }

    public void onClose(WindowEvent e) {
        wrkIO.objToJson(robots, SAVE_PATH);
    }

    public void setRobots(ArrayList<MyRobot> robots) {
        this.robots = robots;
        lstRobot.getItems().setAll(robots);
    }

    @FXML
    private void delete(ActionEvent event) {
        MyRobot selectedRobot = lstRobot.getSelectionModel().getSelectedItem();
        if (selectedRobot != null) {
            robots.remove(selectedRobot);
            refreshList();
        } else {
            WindowManager.afficherAlerte("Merci de sélectionner un robot !");

        }
    }

    @FXML
    private void edit(ActionEvent event) {
        MyRobot selectedRobot = lstRobot.getSelectionModel().getSelectedItem();
        if (selectedRobot != null) {
            Fenetre f = WindowManager.creerFenetre("view/EditView.fxml", "Editer un robot");
            EditViewController ctrl = (EditViewController) f.getCtrl();
            ctrl.setRefCtrl(this);
            ctrl.setRobot(selectedRobot);
            f.show();
        } else {
            WindowManager.afficherAlerte("Merci de sélectionner un robot !");
        }
    }

    @Override
    public void onSave(MyRobot robot) {
        int index = lstRobot.getSelectionModel().getSelectedIndex();
        robots.set(index, robot);
        refreshList();
    }

    public void addRobot(MyRobot robot) {
        robots.add(robot);
        refreshList();
    }

    private void refreshList() {
        lstRobot.getItems().setAll(robots);
    }
}
