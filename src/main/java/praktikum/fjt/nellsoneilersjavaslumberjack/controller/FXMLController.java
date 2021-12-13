package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import praktikum.fjt.nellsoneilersjavaslumberjack.IslandGenerator;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.IslandRegion;

public class FXMLController {

  Stage stage;
  @FXML
  ScrollPane islandScrollPane;
  @FXML
  ToggleButton placeActorBtn;
  @FXML
  ToggleButton placeTreeBtn;
  @FXML
  ToggleButton placeStumpBtn;
  @FXML
  ToggleButton placeWoodBtn;
  @FXML
  ToggleButton placeWaterBtn;
  @FXML
  ToggleButton deleteObjectBtn;
  @FXML
  RadioMenuItem placeActorMenuItem;
  @FXML
  RadioMenuItem placeTreeMenuItem;
  @FXML
  RadioMenuItem placeStumpMenuItem;
  @FXML
  RadioMenuItem placeWoodMenuItem;
  @FXML
  RadioMenuItem placeWaterMenuItem;
  @FXML
  RadioMenuItem deleteObjectMenuItem;
  @FXML
  Spinner<Integer> islandWidthSpinner;
  @FXML
  Spinner<Integer> islandHeightSpinner;
  @FXML
  CheckBox editIslandCheckBox;
  @FXML
  ToolBar editIslandToolBar;
  @FXML
  Menu editIslandMenu;
  @FXML
  Button newProgramBtn;
  @FXML
  MenuItem newProgramMenuItem;
  @FXML
  Button openProgramBtn;
  @FXML
  MenuItem openProgramMenuItem;
  @FXML
  Button saveProgramBtn;
  @FXML
  MenuItem saveProgramMenuItem;
  @FXML
  Button closeProgramBtn;
  @FXML
  MenuItem closeProgramMenuItem;
  @FXML
  Button compileProgramBtn;
  @FXML
  MenuItem compileProgramMenuItem;
  @FXML
  Button actorTurnLeftBtn;
  @FXML
  MenuItem actorTurnLeftMenuItem;
  @FXML
  Button actorForwardBtn;
  @FXML
  MenuItem actorForwardMenuItem;
  @FXML
  Button actorTurnRightBtn;
  @FXML
  MenuItem actorTurnRightMenuItem;
  @FXML
  Button actorTakeWoodBtn;
  @FXML
  MenuItem actorTakeWoodMenuItem;
  @FXML
  Button actorPutWoodBtn;
  @FXML
  MenuItem actorPutWoodMenuItem;
  @FXML
  Button actorCutTreeBtn;
  @FXML
  MenuItem actorCutTreeMenuItem;
  @FXML
  MenuItem actorFrontFreeMenuItem;
  @FXML
  MenuItem actorAxeSwingableMenuItem;
  @FXML
  MenuItem actorFrontTreeMenuItem;
  @FXML
  MenuItem actorFrontWoodMenuItem;
  @FXML
  MenuItem actorHasWoodMenuItem;
  @FXML
  TextArea programEditor;
  @FXML
  Label statusLabel;

  IslandRegion islandRegion;

  TextInputDialog newProgramDialog;

  Island island;


  private IslandController islandController;
  private BindingsController bindingsController;
  private EditorController editorController;


  @FXML
  public void initialize() {
    island = IslandGenerator.createExampleIsland();
    islandRegion = new IslandRegion(island);
    islandScrollPane.setContent(islandRegion);

    bindingsController = new BindingsController(this);
    bindingsController.initialize();

    islandController = new IslandController(this, island);
    islandController.initialize();

    editorController = new EditorController(this);
    editorController.initialize();
  }

  public void updateActorContextMenu() {
    islandController.updateActorContextMenu();
  }
}