package praktikum.fjt.nellsoneilersjavaslumberjack.view;

import java.awt.Toolkit;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import praktikum.fjt.nellsoneilersjavaslumberjack.controller.FXMLController;

public class AlertFactory {

  public static void createError(String message) {
    Toolkit.getDefaultToolkit().beep();
    createAlertOfType(AlertType.ERROR, message);
  }

  public static void createInfo(String message) {
    createAlertOfType(AlertType.INFORMATION, message);
  }

  private static void createAlertOfType(AlertType type, String message) {
    new Alert(type, message, ButtonType.OK).showAndWait();
  }
}
