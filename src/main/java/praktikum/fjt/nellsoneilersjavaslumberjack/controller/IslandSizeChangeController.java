package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;

public class IslandSizeChangeController {

  private final FXMLController fxmlCtrl;

  private final Island island;

  private boolean refreshing;

  public IslandSizeChangeController(
      FXMLController fxmlCtrl, Island island) {
    this.fxmlCtrl = fxmlCtrl;
    this.island = island;
  }

  public void initialize() {
    // Initialize the size spinners
    fxmlCtrl.islandWidthSpinner.getValueFactory().setValue(island.getWidth());
    addIntegerValidationListener(fxmlCtrl.islandWidthSpinner);
    fxmlCtrl.islandHeightSpinner.getValueFactory().setValue(island.getHeight());
    addIntegerValidationListener(fxmlCtrl.islandHeightSpinner);

    // Configure size changing event handling
    fxmlCtrl.islandWidthSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
      if(!refreshing && newValue != null && newValue > 0) setIslandWidth(newValue);
    });
    fxmlCtrl.islandHeightSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
      if(!refreshing && newValue != null && newValue > 0) setIslandHeight(newValue);
    });
  }

  public void refreshSpinners() {
    refreshing = true;
    fxmlCtrl.islandWidthSpinner.getValueFactory().setValue(island.getWidth());
    fxmlCtrl.islandHeightSpinner.getValueFactory().setValue(island.getHeight());
    refreshing = false;
  }

  private void addIntegerValidationListener(Spinner<Integer> spinner) {
    TextField editor = spinner.getEditor();
    editor.textProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue == null || newValue.equals("")) { // Handle empty input
        editor.setText("0");
      } else if(!newValue.matches("\\d*")){ // Handle non-numeric input
        editor.setText(oldValue);
      } else { // Removes leading zeros
        editor.setText(newValue.replaceFirst("^0+(?!$)", ""));
      }
    });
  }


  private void setIslandWidth(int newValue) {
    island.setSize(newValue, island.getHeight());
  }

  private void setIslandHeight(int newValue) {
    island.setSize(island.getWidth(), newValue);
  }
}
