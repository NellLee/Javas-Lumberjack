package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.print.PrinterJob;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.LumberjackProgram;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class EditorController {

  private final FXMLController fxmlCtrl;

  public EditorController(FXMLController fxmlCtrl) {
    this.fxmlCtrl = fxmlCtrl;
  }

  public void initialize() {
    initializeNewProgramDialog();
    initializeProgramBtnEventHandling();
    initializePrintBtnEventHandling();

    fxmlCtrl.programEditor.requestFocus();
  }

  private void initializePrintBtnEventHandling() {
    fxmlCtrl.printEditorMenuItem.setOnAction(actionEvent -> {
      PrinterJob job = PrinterJob.createPrinterJob();
      if (job != null && job.showPrintDialog(fxmlCtrl.stage)){
        boolean success = job.printPage(fxmlCtrl.programEditor);
        if (success) {
          job.endJob();
          AlertFactory.createInfo("Editor erfolgreich gedruckt!");
        } else {
          AlertFactory.createError("Drucken des Editors fehlgeschlagen!");
        }
      }
    });
  }

  private void initializeProgramBtnEventHandling() {
    ProgramController programController = ProgramController.getInstance();

    fxmlCtrl.newProgramBtn.setOnAction(actionEvent -> fxmlCtrl.newProgramDialog.show());
    fxmlCtrl.newProgramMenuItem.setOnAction(actionEvent -> fxmlCtrl.newProgramDialog.show());

    fxmlCtrl.openProgramBtn.setOnAction(actionEvent -> programController.openProgram());
    fxmlCtrl.openProgramMenuItem.setOnAction(actionEvent -> programController.openProgram());

    fxmlCtrl.saveProgramBtn.setOnAction(actionEvent -> programController.saveProgram(
        fxmlCtrl.stage.getTitle() + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText()));
    fxmlCtrl.saveProgramMenuItem.setOnAction(actionEvent -> programController.saveProgram(
        fxmlCtrl.stage.getTitle() + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText()));

    fxmlCtrl.closeProgramBtn.setOnAction(actionEvent -> programController.saveAndCloseProgramAndStage(
        fxmlCtrl.stage.getTitle() + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText(), fxmlCtrl));
    fxmlCtrl.closeProgramMenuItem.setOnAction(actionEvent -> programController.saveAndCloseProgramAndStage(
        fxmlCtrl.stage.getTitle() + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText(), fxmlCtrl));

    fxmlCtrl.compileProgramBtn.setOnAction(actionEvent -> programController.compileAndLoadProgram(
        fxmlCtrl.stage.getTitle() + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText(), fxmlCtrl));
    fxmlCtrl.compileProgramMenuItem.setOnAction(actionEvent -> programController.compileAndLoadProgram(
        fxmlCtrl.stage.getTitle() + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText(), fxmlCtrl));
  }

  private void initializeNewProgramDialog() {
    ProgramController programController = ProgramController.getInstance();

    fxmlCtrl.newProgramDialog = new TextInputDialog("MyLumberjackProgram");
    fxmlCtrl.newProgramDialog.setHeaderText("Gib einen Namen für dein neues Lumberjack Program ein.");
    fxmlCtrl.newProgramDialog.setTitle("Neues Program erstellen");
    fxmlCtrl.newProgramDialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      boolean isValid = programController.isValidProgramName(newValue);
      fxmlCtrl.newProgramDialog.getDialogPane().lookupButton(
          ButtonType.OK).setDisable(!isValid);
    });

    fxmlCtrl.newProgramDialog.setResultConverter(buttonType -> {
      if (buttonType == ButtonType.OK) {
        return fxmlCtrl.newProgramDialog.getEditor().getText();
      }
      return null;
    });
    fxmlCtrl.newProgramDialog.setOnCloseRequest(dialogEvent -> {
      String result = fxmlCtrl.newProgramDialog.getResult();
      if(result != null) {
        if(LumberjackProgram.programFileExists(result + LumberjackProgram.EXTENSION)) {
          AlertFactory.createError("Erstellen von  '" + result + "' nicht möglich: Es existiert bereits eine Datei mit diesem Namen.");
          dialogEvent.consume();
        } else {
          programController.createProgramAndStage(result);
        }
      }
    });
  }

}
