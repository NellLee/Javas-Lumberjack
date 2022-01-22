package praktikum.fjt.nellsoneilersjavaslumberjack;

import javafx.application.Application;
import javafx.stage.Stage;

import praktikum.fjt.nellsoneilersjavaslumberjack.controller.DatabaseController;
import praktikum.fjt.nellsoneilersjavaslumberjack.controller.ProgramController;

public class Main extends Application {

  @Override
  public void start(Stage stage) {
    ProgramController.getInstance().createProgramAndStage("DefaultLumberjackProgram");
  }

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void stop() throws Exception {
    super.stop();

    DatabaseController.getInstance().shutDownDatabase();
  }
}