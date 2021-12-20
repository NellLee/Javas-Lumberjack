package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import praktikum.fjt.nellsoneilersjavaslumberjack.Main;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Lumberjack;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.LumberjackProgram;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class ProgramController {

  private static final ProgramController INSTANCE = new ProgramController();
  private final ArrayList<LumberjackProgram> programs;

  private ProgramController() {
    programs = new ArrayList<>();
  }

  public static ProgramController getInstance() {
    return INSTANCE;
  }

  public void saveAndCloseProgramAndStage(String fileName, String code, FXMLController fxmlCtrl) {
    saveProgram(fileName, code);
    getOpenProgram(fileName).ifPresent(programs::remove);
    fxmlCtrl.stage.close();
  }

  public void saveProgram(String fileName, String code) {
    getOpenProgram(fileName).ifPresent(program -> program.saveCodeToFile(code));
  }

  public void createProgramAndStage(String name) {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));

    Stage stage = new Stage();
    try {
      stage.setTitle(name);
      stage.setScene(new Scene(fxmlLoader.load(), 1000, 725));
      stage.show();

      FXMLController fxmlCtrl = fxmlLoader.getController();
      fxmlCtrl.stage = stage;

      LumberjackProgram program = new LumberjackProgram(name);
      programs.add(program);

      initialCompileAndLoadProgram(program, fxmlCtrl);

      fxmlCtrl.programEditor.setText(program.loadCodeFromFile());
      fxmlCtrl.stage.setOnCloseRequest(windowEvent -> saveAndCloseProgramAndStage(name + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText(), fxmlCtrl));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void compileAndLoadProgram(String fileName, String code, FXMLController fxmlCtrl) {
    saveProgram(fileName, code);

    getOpenProgram(fileName).ifPresent(program -> {

      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

      ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
      boolean success = compiler.run(null, null, errorStream, program.getFullPath()) == 0;

      if(success) {
        loadProgram(program, fxmlCtrl);
        AlertFactory.createInfo("Kompilieren erfolgreich!");
      } else {
        AlertFactory.createError(errorStream.toString());
      }
    });
  }

  private void initialCompileAndLoadProgram(LumberjackProgram program, FXMLController fxmlCtrl) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    boolean success = compiler.run(null, null, errorStream, program.getFullPath()) == 0;

    if(success) {
      loadProgram(program, fxmlCtrl);
      fxmlCtrl.statusLabel.setText(program.getFileName() + " erfolgreich geladen.");
    } else {
      fxmlCtrl.statusLabel.setText("Fehler bei initialer Kompilation von " + program.getFileName() + ". (Standard-Akteur wird stattdessen genutzt)");
    }
  }

  private void loadProgram(LumberjackProgram program, FXMLController fxmlCtrl) {
    try (URLClassLoader classLoader = new URLClassLoader( new URL[] {new File(LumberjackProgram.DIRECTORY).toURI().toURL()})) {
      Lumberjack newActor = (Lumberjack) classLoader.loadClass(program.getName()).getDeclaredConstructor().newInstance();
      fxmlCtrl.island.changeActor(newActor);
      fxmlCtrl.updateActorContextMenu();
    } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  private boolean isOpenProgram(String fileName) {
    return getOpenProgram(fileName).isPresent();
  }

  private Optional<LumberjackProgram> getOpenProgram(String fileName) {
    for (LumberjackProgram program : programs) {
      if(program.getFileName().equals(fileName)) {
        return Optional.of(program);
      }
    }
    return Optional.empty();
  }

  boolean isValidProgramName(String str) {
    return SourceVersion.isIdentifier(str) && !SourceVersion.isKeyword(str);
  }


  public void openProgram() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Vorhandene Programm-Datei öffnen");
    File dir = new File(LumberjackProgram.DIRECTORY);
    fileChooser.setInitialDirectory(dir);
    fileChooser.getExtensionFilters().add(new ExtensionFilter("Java Dateien", "*.java"));
    File file = fileChooser.showOpenDialog(new Stage());
    if(file != null) {
      String fileName = file.getName();
      String name = fileName.replaceFirst("[.][^.]+$", ""); // remove extension
      if(isOpenProgram(fileName)) {
        AlertFactory.createError("Öffnen von '" + fileName + "' nicht möglich: Das Program ist bereits geöffnet.");
      } else if (!file.getParentFile().getAbsolutePath().equals(dir.getAbsolutePath())) {
        AlertFactory.createError("Öffnen von '" + fileName + "' nicht möglich: Die Datei befindet sich nicht im Verzeichnis 'programs'.");
      } else {
        createProgramAndStage(name);
      }
    }
  }

  //TODO create a dialog to choose between this method  and the method above that is currently used
  public void openProgramInPlace(FXMLController fxmlCtrl) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Vorhandene Programm-Datei öffnen");
    File dir = new File(LumberjackProgram.DIRECTORY);
    fileChooser.setInitialDirectory(dir);
    ExtensionFilter filter = new ExtensionFilter("Java Dateien", "*.java");
    fileChooser.getExtensionFilters().add(filter);
    File file = fileChooser.showOpenDialog(fxmlCtrl.stage);
    if(file != null) {
      String fileName = file.getName();
      String name = fileName.replaceFirst("[.][^.]+$", ""); // remove extension
      if(isOpenProgram(fileName)) {
        AlertFactory.createError("Öffnen von '" + fileName + "' nicht möglich: Das Program ist bereits geöffnet.");
      } else if (!file.getParentFile().getAbsolutePath().equals(dir.getAbsolutePath())) {
        AlertFactory.createError("Öffnen von '" + fileName + "' nicht möglich: Die Datei befindet sich nicht im Verzeichnis 'programs'.");
      } else {
        fxmlCtrl.stage.setTitle(name);

        LumberjackProgram program = new LumberjackProgram(name);
        programs.add(program);

        fxmlCtrl.programEditor.setText(program.loadCodeFromFile());
        fxmlCtrl.stage.setOnCloseRequest(windowEvent -> saveAndCloseProgramAndStage(name + LumberjackProgram.EXTENSION, fxmlCtrl.programEditor.getText(), fxmlCtrl));

      }
    }
  }
}
