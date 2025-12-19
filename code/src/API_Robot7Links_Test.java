
package api_robot7links_test;

import api_robot7links_test.bean.Fenetre;
import api_robot7links_test.ctrl.MenuViewController;
import api_robot7links_test.ctrl.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author AudergonV01
 */
public class API_Robot7Links_Test extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        WindowManager.c = this.getClass();
        Fenetre f = WindowManager.creerFenetre("../view/MenuView.fxml", "Application Demo 7Links");
        MenuViewController ctrl = (MenuViewController) f.getCtrl();
        f.getStage().setOnCloseRequest(e -> ctrl.onClose(e));
        f.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
