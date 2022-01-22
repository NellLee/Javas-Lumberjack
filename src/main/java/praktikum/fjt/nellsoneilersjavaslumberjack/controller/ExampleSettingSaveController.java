package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.util.ArrayList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.ExampleSetting;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class ExampleSettingSaveController {

  private final FXMLController fxmlCtrl;

  //the regex matches a comma seperated list of words (whitespaces allowed, trailing comma not allowed)
  private static final String commaSeperatedListRegex = "((\\s)*(\\w)+(\\s)*(,(\\s)*(\\w)+(\\s)*)*)*";

  public ExampleSettingSaveController(FXMLController fxmlCtrl) {
    this.fxmlCtrl = fxmlCtrl;
  }
  public void initialize() {
    initializeSaveExampleSettingDialog();

    fxmlCtrl.saveExampleMenuItem.setOnAction(actionEvent -> fxmlCtrl.saveExampleSettingWithTagsDialog.show());
    fxmlCtrl.loadExampleMenuItem.setOnAction(actionEvent -> fxmlCtrl.loadExampleSettingByTagsDialog.show());
  }

  private void initializeSaveExampleSettingDialog() {
    fxmlCtrl.saveExampleSettingWithTagsDialog = new TextInputDialog("ExampleTag, AnotherTag");
    fxmlCtrl.saveExampleSettingWithTagsDialog.setHeaderText("""
        Gib Tags mit denen die aktuelle Umgebung gespeichert werden soll.\s
        Lasse das Textfeld leer um keine Tags zu setzen.\s
        Mehrere Tags werden durch ein Komma ',' getrennt.""");
    fxmlCtrl.saveExampleSettingWithTagsDialog.setTitle("Beispielumgebung speichern");
    fxmlCtrl.saveExampleSettingWithTagsDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      boolean isValid = newValue.matches(commaSeperatedListRegex);
      fxmlCtrl.saveExampleSettingWithTagsDialog.getDialogPane().lookupButton(
          ButtonType.OK).setDisable(!isValid);
    });

    fxmlCtrl.saveExampleSettingWithTagsDialog.setResultConverter(buttonType -> {
      if (buttonType == ButtonType.OK) {
        return fxmlCtrl.saveExampleSettingWithTagsDialog.getEditor().getText();
      }
      return null;
    });
    fxmlCtrl.saveExampleSettingWithTagsDialog.setOnCloseRequest(dialogEvent -> {
      //split the comma seperated tag list at every comma and trim all whitespaces
      String[] tags = fxmlCtrl.saveExampleSettingWithTagsDialog.getResult().trim().split("\\s*,\\s*");
      if(!DatabaseController.getInstance().saveExampleSetting(fxmlCtrl.stage.getTitle(), fxmlCtrl.programEditor.getText(),
          fxmlCtrl.getIslandXMLString(), tags)) {
        AlertFactory.createError("Beispielumgebung konnte nicht in der Datenbank gespeichert werden!");
      }
    });

    fxmlCtrl.loadExampleSettingByTagsDialog = new TextInputDialog("ExampleTag, AnotherTag");
    fxmlCtrl.loadExampleSettingByTagsDialog.setHeaderText("""
        Gib Tags an nach denen gesucht werden soll.
        Lasse das Textfeld leer um keine Einschränkung zu treffen.
        Mehrere Tags werden durch ein Komma ',' getrennt.""");
    fxmlCtrl.loadExampleSettingByTagsDialog.setTitle("Beispielumgebung laden");
    fxmlCtrl.loadExampleSettingByTagsDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      boolean isValid = newValue.matches(commaSeperatedListRegex);
      fxmlCtrl.loadExampleSettingByTagsDialog.getDialogPane().lookupButton(
          ButtonType.OK).setDisable(!isValid);
    });

    fxmlCtrl.loadExampleSettingByTagsDialog.setResultConverter(buttonType -> {
      if (buttonType == ButtonType.OK) {
        return fxmlCtrl.loadExampleSettingByTagsDialog.getEditor().getText();
      }
      return null;
    });
    fxmlCtrl.loadExampleSettingByTagsDialog.setOnCloseRequest(tagDialogEvent -> {
      String result = fxmlCtrl.loadExampleSettingByTagsDialog.getResult();
      if(result == null) {
        return;
      }
      //split the comma seperated tag list at every comma and trim all whitespaces
      String[] tags;
      if(!result.equals("")) {
        tags = result.trim().split("\\s*,\\s*");
      } else {
        //an empty array will cause no restriction, so all database entries will be shown
        tags = new String[] {};
      }
      ArrayList<String> nameIdPairs = DatabaseController.getInstance().getExampleNameIdPairByTags(tags);
      if(nameIdPairs != null) {
        if(nameIdPairs.isEmpty()) {
          AlertFactory.createInfo("Keine Beispielumgebung mit diesen Tags gefunden!");
          tagDialogEvent.consume();
          return;
        }
        fxmlCtrl.loadExampleSettingByNameIdPairDialog = new ChoiceDialog<>(nameIdPairs.get(0),
            nameIdPairs);
        initializeLoadDialog(nameIdPairs);
        fxmlCtrl.loadExampleSettingByNameIdPairDialog.show();
      }
    });
  }

  private void initializeLoadDialog(ArrayList<String> nameIdPairs) {
    fxmlCtrl.loadExampleSettingByNameIdPairDialog.setHeaderText("Wähle die Beispielumgebung aus die geladen werden soll.");
    fxmlCtrl.loadExampleSettingByNameIdPairDialog.setTitle("Beispielumgebung laden");

    fxmlCtrl.loadExampleSettingByNameIdPairDialog.setResultConverter(buttonType -> {
      if (buttonType == ButtonType.OK) {
        return fxmlCtrl.loadExampleSettingByNameIdPairDialog.getSelectedItem();
      }
      return null;
    });
    fxmlCtrl.loadExampleSettingByNameIdPairDialog.setOnCloseRequest(nameIdPairDialogEvent -> {
      String result2 = fxmlCtrl.loadExampleSettingByNameIdPairDialog.getResult();
      if(result2 == null) {
        return;
      }
      ExampleSetting exampleSetting = DatabaseController.getInstance().getExampleByNameIdPair(
          result2);
      if(exampleSetting != null) {
        fxmlCtrl.programEditor.setText(exampleSetting.getEditorContent());
        if(!fxmlCtrl.setIslandFromXMLString(exampleSetting.getIslandContent())) {
          AlertFactory.createError("Die Insel der Beispielumgebung konnte nicht geladen werden!");
        }
      } else {
        AlertFactory.createError("Beispielumgebung konnte nicht aus der Datenbank geladen werden!");
      }
    });
  }
}
