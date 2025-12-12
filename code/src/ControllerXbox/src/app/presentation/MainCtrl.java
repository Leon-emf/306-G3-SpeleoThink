package app.presentation;

import app.beans.XboxButton;
import app.ctrl.Ctrl;
import app.helpers.JfxPopup;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author PA
 */
public class MainCtrl implements Initializable {

    private Ctrl ctrl;
    private boolean connexionOk;
    @FXML
    private Label lblY;
    @FXML
    private Label lblB;
    @FXML
    private Label lblA;
    @FXML
    private Label lblX;
    @FXML
    private Label lblDpadDroite;
    @FXML
    private Label lblDpadHaut;
    @FXML
    private Label lblDpadGauche;
    @FXML
    private Label lblDpadBas;
    @FXML
    private Label lblRightThumbHaut;
    @FXML
    private Label lblRightThumbBas;
    @FXML
    private Label lblLeftThumbBas;
    @FXML
    private Label lblLeftThumbHaut;
    @FXML
    private Label lblTriggerDroite;
    @FXML
    private Label lblTriggerGauche;
    @FXML
    private Label lblStart;
    @FXML
    private Label lblStartInfo;
    @FXML
    private Label lblLeftThumbGauche;
    @FXML
    private Label lblLeftThumbDroite;
    @FXML
    private Label lblRightThumbGauche;
    @FXML
    private Label lblRightThumbDroite;
    @FXML
    private Label lblShoulderDroite;
    @FXML
    private Label lblShoulderGauche;
    @FXML
    private Label lblBack;
    @FXML
    private Button btnConnect;

    /*
   * INITIALISATION DE LA VUE
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        desactiverLbl();
        connexionOk = false;
    }

    public MainCtrl(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    public void start() {
        // Initialiser le toolkit JavaFX (utile en lancement hors Application.launch)
        new JFXPanel();

        Platform.runLater( () -> {
            try {
                Stage mainStage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource( "MainView.fxml" ) );
                fxmlLoader.setControllerFactory( type -> this );
                Parent root = ( Parent ) fxmlLoader.load();
                Scene principalScene = new Scene( root );
                mainStage.setScene( principalScene );
                mainStage.setTitle( "Application de Controller Xbox" );
                mainStage.show();
            }
            catch ( IOException ex ) {
                ex.printStackTrace();
                Platform.exit();
            }
        } );
    }


    /*
   * METHODES DE LECTURE/ECRITURE DANS LA VUE
     */
    public void setDetected(XboxButton control) {
        switch (control.getNom()) {
            case "Y":
                lblY.setVisible(control.isPressed());
                break;
            case "B":
                lblB.setVisible(control.isPressed());
                break;
            case "A":
                lblA.setVisible(control.isPressed());
                break;
            case "X":
                lblX.setVisible(control.isPressed());
                break;
            case "start":
                lblStart.setVisible(control.isPressed());
                break;
            case "Haut":
                lblDpadHaut.setVisible(control.isPressed());
                break;
            case "Bas":
                lblDpadBas.setVisible(control.isPressed());
                break;
            case "Droite":
                lblDpadDroite.setVisible(control.isPressed());
                break;
            case "Gauche":
                lblDpadGauche.setVisible(control.isPressed());
                break;
            case "Haut + Gauche":
                lblDpadGauche.setVisible(control.isPressed());
                lblDpadHaut.setVisible(control.isPressed());
                break;
            case "Bas + Gauche":
                lblDpadGauche.setVisible(control.isPressed());
                lblDpadBas.setVisible(control.isPressed());
                break;
            case "Haut + Droite":
                lblDpadDroite.setVisible(control.isPressed());
                lblDpadHaut.setVisible(control.isPressed());
                break;
            case "Bas + Droite":
                lblDpadDroite.setVisible(control.isPressed());
                lblDpadBas.setVisible(control.isPressed());
                break;
            case "Left Trigger":
                lblTriggerGauche.setVisible(control.isPressed());
                break;
            case "Right Trigger":
                lblTriggerDroite.setVisible(control.isPressed());
                break;
            case "Joystic Gauche - Haut":
                lblLeftThumbHaut.setVisible(control.isPressed());
                break;
            case "Joystic Gauche - Bas":
                lblLeftThumbBas.setVisible(control.isPressed());
                break;
            case "Joystic Gauche - Gauche":
                lblLeftThumbGauche.setVisible(control.isPressed());
                break;
            case "Joystic Gauche - Droite":
                lblLeftThumbDroite.setVisible(control.isPressed());
                break;
            case "Joystic Droit - Haut":
                lblRightThumbHaut.setVisible(control.isPressed());
                break;
            case "Joystic Droit - Bas":
                lblRightThumbBas.setVisible(control.isPressed());
                break;
            case "Joystic Droit - Gauche":
                lblRightThumbGauche.setVisible(control.isPressed());
                break;
            case "Joystic Droit - Droite":
                lblRightThumbDroite.setVisible(control.isPressed());
                break;
            case "Shoulder Droit":
                lblShoulderDroite.setVisible(control.isPressed());
                break;
            case "Shoulder Gauche":
                lblShoulderGauche.setVisible(control.isPressed());
                break;
        }
    }

    public void setCtrl(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    private void desactiverLbl() {
        lblStartInfo.setVisible(false);
        lblY.setVisible(false);
        lblB.setVisible(false);
        lblA.setVisible(false);
        lblX.setVisible(false);
        lblDpadDroite.setVisible(false);
        lblDpadHaut.setVisible(false);
        lblDpadGauche.setVisible(false);
        lblDpadBas.setVisible(false);
        lblRightThumbHaut.setVisible(false);
        lblRightThumbBas.setVisible(false);
        lblRightThumbGauche.setVisible(false);
        lblRightThumbDroite.setVisible(false);
        lblLeftThumbBas.setVisible(false);
        lblLeftThumbHaut.setVisible(false);
        lblLeftThumbGauche.setVisible(false);
        lblLeftThumbDroite.setVisible(false);
        lblTriggerDroite.setVisible(false);
        lblTriggerGauche.setVisible(false);
        lblBack.setVisible(false);
        lblStart.setVisible(false);
        lblShoulderDroite.setVisible(false);
        lblShoulderGauche.setVisible(false);
    }

    public void messageConnexion() {
        JfxPopup.displayInformation("Connexion à la manette", "Manette connectée", "Manette connectée avec succès.");
    }

    /*
   * METHODE PUBLIQUE POUR App.java
     */
    public void quitter() {
        ctrl.quitter();
        Platform.exit();
    }

    @FXML
    private void connect(ActionEvent event) {
        if (!connexionOk) {
            btnConnect.setText("Quitter");
        } else {
            btnConnect.setText("Connexion à la manette");
            desactiverLbl();
            quitter();
        }
        connexionOk = !connexionOk;
        ctrl.connect(connexionOk);
    }

    public void erreurConnexion() {
        JfxPopup.displayInformation("Connexion à la manette", "Manette déconnectée", "Une erreur est survenue, la manette a été déconnectée");
    }

}
