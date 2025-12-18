/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.ctrl;

import api_robot7links_test.bean.Fenetre;
import com.sun.scenario.Settings;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Audergonv01
 */
public class WindowManager {

    public static Class<?> c;
    private static final String CSS_PATH = "/api_robot7links_test/resources/css/view.css";
    private static final String THEME = "darktheme";

    public static Fenetre creerFenetre(String fxml, String titre) {
        Stage stage = null;
        Ctrl ctrl = null;
        try {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(c.getResource(fxml));
            Parent root = (Parent) loader.load();
            ctrl = loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(titre);
            scene.getRoot().getStyleClass().add(THEME);

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage() + "\n" + ex.getMessage());
        }
        return new Fenetre(ctrl, stage);
    }

    public static void afficherErreur(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(c.getResource(CSS_PATH).toExternalForm());
            alert.getDialogPane().getStyleClass().add(THEME);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public static void afficherAlerte(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(c.getResource(CSS_PATH).toExternalForm());
            alert.getDialogPane().getStyleClass().add(THEME);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public static void afficherInfo(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add(c.getResource(CSS_PATH).toExternalForm());
            alert.getDialogPane().getStyleClass().add(THEME);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public static boolean afficherConfirmation(String titre, String msg) {
        boolean ok = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(c.getResource(CSS_PATH).toExternalForm());
        alert.getDialogPane().getStyleClass().add(THEME);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            ok = true;
        }

        return ok;

    }

    public static String creerFileChooser() {
        String retour = "";
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            retour = file.getPath();

        }
        return retour;
    }

}
