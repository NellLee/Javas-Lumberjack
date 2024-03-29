package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
  Menu placeObjectMenu;
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
  Button startSimButton;
  @FXML
  MenuItem startSimMenuItem;
  @FXML
  Button pauseSimButton;
  @FXML
  MenuItem pauseSimMenuItem;
  @FXML
  Button stopSimButton;
  @FXML
  MenuItem stopSimMenuItem;
  @FXML
  TextArea programEditor;
  @FXML
  Label statusLabel;
  @FXML
  Slider simSpeedSlider;
  @FXML
  MenuItem serializeIslandMenuItem;
  @FXML
  MenuItem deserializeIslandMenuItem;
  @FXML
  MenuItem printEditorMenuItem;
  @FXML
  MenuItem printIslandMenuItem;
  @FXML
  MenuItem saveIslandPNGMenuItem;
  @FXML
  MenuItem saveIslandGIFMenuItem;
  @FXML
  MenuItem saveXMLIslandMenuItem;
  @FXML
  MenuItem loadXMLIslandMenuItem;
  @FXML
  MenuItem loadExampleMenuItem;
  @FXML
  MenuItem saveExampleMenuItem;

  IslandRegion islandRegion;

  TextInputDialog newProgramDialog;

  TextInputDialog loadExampleSettingByTagsDialog;

  TextInputDialog saveExampleSettingWithTagsDialog;

  ChoiceDialog<String> loadExampleSettingByNameIdPairDialog;

  Island island;


  private IslandController islandController;
  private BindingsController bindingsController;
  private EditorController editorController;
  private SimulationController simulationController;
  private ExampleSettingSaveController exampleSettingSaveController;


  @FXML
  public void initialize() {
    DatabaseController.getInstance().checkStatus();

    island = IslandGenerator.createExampleIsland();
    islandRegion = new IslandRegion(island);
    islandScrollPane.setContent(islandRegion);

    bindingsController = new BindingsController(this);
    bindingsController.initialize();

    islandController = new IslandController(this, island);
    islandController.initialize();

    editorController = new EditorController(this);
    editorController.initialize();

    simulationController = new SimulationController(this, island);
    simulationController.initialize();

    exampleSettingSaveController = new ExampleSettingSaveController(this);
    exampleSettingSaveController.initialize();
  }

  public void updateActorContextMenu() {
    islandController.updateActorContextMenu();
  }

  public void refreshIslandSizeSpinners() {
    islandController.refreshIslandSizeSpinners();
  }

  public void setStatusLabelMessage(String message) {
    statusLabel.setText(message);
  }

  public String getIslandXMLString() {
    return islandController.getIslandXMLString();
  }

  public boolean setIslandFromXMLString(String xmlString) {
    return islandController.setIslandFromXMLString(xmlString);
  }
}