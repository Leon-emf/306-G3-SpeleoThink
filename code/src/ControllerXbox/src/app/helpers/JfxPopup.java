package app.helpers;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 * Affichage de popups.
 *
 * @author P-A Mettraux
 */
public class JfxPopup {

  /**
   * Affiche une popup de type "error".
   *
   * @param title le titre pour la popup
   * @param header un entête (si null = pas d'entête)
   * @param erreur le message d'erreur
   */
  public static void displayError(String title, String header, String erreur) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(erreur);
    alert.showAndWait();
  }

  /**
   * Affiche une popup de type "information".
   *
   * @param title le titre pour la popup
   * @param header un entête (si null = pas d'entête)
   * @param information le message d'information
   */
  public static void displayInformation(String title, String header, String information) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(information);
    alert.showAndWait();
  }

  /**
   * Affiche une popup de type "avertissement".
   *
   * @param title le titre pour la popup
   * @param header un entête (si null = pas d'entête)
   * @param attention le message d'avertissement
   */
  public static void displayWarning(String title, String header, String attention) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(attention);
    alert.showAndWait();
  }

  /**
   * Affiche une popup de type "confirmation" avec OK ou annuler.
   *
   * @param title le titre pour la popup
   * @param header un entête (si null = pas d'entête)
   * @param message un message pour la popup
   *
   * @return true if OK
   */
  public static boolean askConfirmation(String title, String header, String message) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);

    Optional<ButtonType> rep = alert.showAndWait();

    return rep.get() == ButtonType.OK;
  }

  /**
   * Affiche une popup de type "confirmation" avec OUI/NON.
   *
   * @param title le titre pour la popup
   * @param header un entête (si null = pas d'entête)
   * @param message un message pour la popup
   * @param french true les boutons sont en français "oui" et "non"
   * @param defaultNo true le bouton par défaut est le "non"
   *
   * @return true si OK
   */
  public static boolean askYesNo(String title, String header, String message, boolean french, boolean defaultNo) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);

    ButtonType btnYes = new ButtonType(french ? "Oui" : "Yes");
    ButtonType btnNo = new ButtonType(french ? "Non" : "No");
    alert.getButtonTypes().setAll(btnYes, btnNo);

    if (defaultNo) {
      // déactive le bouton yes par défaut
      Button yesButton = (Button) alert.getDialogPane().lookupButton(btnYes);
      yesButton.setDefaultButton(false);

      // active le bouton non par défaut
      Button noButton = (Button) alert.getDialogPane().lookupButton(btnNo);
      noButton.setDefaultButton(true);
    }

    Optional<ButtonType> rep = alert.showAndWait();

    return rep.get() == btnYes;
  }

  /**
   * Affiche une popup de type "info" furtive.
   *
   * @param title le titre pour la popup
   * @param header un entête (si null = pas d'entête)
   * @param label un message
   *
   * @return one string or null
   */
  public static String askInfo(String title, String header, String label) {
    TextInputDialog dia = new TextInputDialog();

    dia.setTitle(title);
    dia.setHeaderText(header);
    dia.setContentText(label);

    Optional<String> text = dia.showAndWait();

    return text.isPresent() ? text.get() : null;
  }

}
