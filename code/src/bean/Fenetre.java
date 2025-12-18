/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.bean;

import api_robot7links_test.ctrl.Ctrl;
import java.io.Serializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Fenetre implements Serializable {
    private final Ctrl ctrl;
    private final Stage stage;
    private Scene scene;
    private final String titre;

    public Fenetre(Ctrl ctrl, Stage stage) {
        this.ctrl = ctrl;
        this.stage = stage;
        scene = stage.getScene();
        titre = stage.getTitle();
    }

    
    
    public void show(){
        stage.show();
    }
    
    public void showAndWait(){
        stage.showAndWait();
    }
    
    public void hide(){
        stage.hide();
    }
    
    public Ctrl getCtrl() {
        return ctrl;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    public void setResizable(boolean b){
        stage.initStyle(StageStyle.DECORATED);
        stage.resizableProperty().setValue(b);
    }
    
    public void setClosable (boolean b){
       if (!b){
           stage.setOnCloseRequest((WindowEvent event) -> {
               event.consume();
           });
       } else{
           stage.setOnCloseRequest((WindowEvent event) -> {
           });
       }
    }

    public String getTitre() {
        return titre;
    }
}

