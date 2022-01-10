package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.PrinterJob;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class IslandSaveController {

  private final FXMLController fxmlCtrl;
  private final Island island;

  public IslandSaveController(FXMLController fxmlCtrl, Island island) {
    this.fxmlCtrl = fxmlCtrl;
    this.island = island;
  }


  public void initialize() {

    initializeSerializationBtnEventHandling();
    initializePrintBtnEventHandling();
    initializeSaveImageBtnEventHandling();
  }

  private void initializeSaveImageBtnEventHandling() {
    fxmlCtrl.saveIslandPNGMenuItem.setOnAction(actionEvent -> saveIslandRegionAsImage("png"));
    fxmlCtrl.saveIslandGIFMenuItem.setOnAction(actionEvent -> saveIslandRegionAsImage("gif"));
  }

  private void saveIslandRegionAsImage(String fileExtension) {
    fileExtension = fileExtension.toLowerCase();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Bild speichern unter...");
    fileChooser.getExtensionFilters().add(new ExtensionFilter(fileExtension.toUpperCase() + " Bild Dateien", "*." + fileExtension));
    File file = fileChooser.showSaveDialog(fxmlCtrl.stage);
    if(file == null) {
      return;
    }

    WritableImage islandImage = new WritableImage((int) fxmlCtrl.islandRegion.getWidth(), (int) fxmlCtrl.islandRegion.getHeight());
    fxmlCtrl.islandRegion.snapshot(null, islandImage);
    RenderedImage renderedImage = SwingFXUtils.fromFXImage(islandImage, null);
    try {
      ImageIO.write(renderedImage, fileExtension, file);
      fxmlCtrl.setStatusLabelMessage("Bild '" + file.getName() + "' erfolgreich gespeichert!");
    } catch (IOException e) {
      AlertFactory.createError("Speichern des Bildes fehlgeschlagen: " + e.getMessage());
    }
  }

  private void initializePrintBtnEventHandling() {
    fxmlCtrl.printIslandMenuItem.setOnAction(actionEvent -> {
      PrinterJob job = PrinterJob.createPrinterJob();
      if (job != null && job.showPrintDialog(fxmlCtrl.stage)){
        boolean success = job.printPage(fxmlCtrl.islandRegion);
        if (success) {
          job.endJob();
          fxmlCtrl.setStatusLabelMessage("Insel erfolgreich gedruckt!");
        } else {
          AlertFactory.createError("Drucken der Insel fehlgeschlagen!");
        }
      }
    });
  }

  private void initializeSerializationBtnEventHandling() {
    fxmlCtrl.serializeIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel speichern unter...");
      fileChooser.getExtensionFilters().add(new ExtensionFilter("JavasLumberjack Insel Dateien", "*.island"));
      File file = fileChooser.showSaveDialog(fxmlCtrl.stage);
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
      File file = fileChooser.showOpenDialog(fxmlCtrl.stage);
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
