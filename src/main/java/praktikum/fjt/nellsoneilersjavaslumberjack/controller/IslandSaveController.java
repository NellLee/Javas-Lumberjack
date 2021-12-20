package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;

public class IslandSaveController {

  private final FXMLController fxmlCtrl;
  private final Island island;

  public IslandSaveController(FXMLController fxmlCtrl, Island island) {
    this.fxmlCtrl = fxmlCtrl;
    this.island = island;
  }


  public void initialize() {

    initializeSerializationBtnEventHandling();
  }

  private void initializeSerializationBtnEventHandling() {
    fxmlCtrl.serializeIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel speichern unter...");
      fileChooser.getExtensionFilters().add(new ExtensionFilter("JavasLumberjack Insel Dateien", "*.island"));
      File file = fileChooser.showSaveDialog(new Stage());
      try (
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
      ) {
        objectOut.writeObject(island);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    fxmlCtrl.deserializeIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel öffnen...");
      fileChooser.getExtensionFilters().add(new ExtensionFilter("JavasLumberjack Insel Dateien", "*.island"));
      File file = fileChooser.showOpenDialog(new Stage());
      try (
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn)
      ){
        island.replaceBy((Island) objectIn.readObject());
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    });
  }
}
