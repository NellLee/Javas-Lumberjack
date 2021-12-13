package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import praktikum.fjt.nellsoneilersjavaslumberjack.view.IslandRegion;

public class BindingsController {

  private final FXMLController fxmlCtrl;

  public BindingsController(FXMLController fxmlCtrl) {
    this.fxmlCtrl = fxmlCtrl;
  }

  public void initialize() {
    // Center the position of the island view to half the size of the scroll pane
    IslandRegion view = fxmlCtrl.islandRegion;
    view.translateXProperty().bind(
        fxmlCtrl.islandScrollPane.widthProperty().subtract(view.widthProperty()).divide(2f));
    view.translateYProperty().bind(
        fxmlCtrl.islandScrollPane.heightProperty().subtract(view.heightProperty()).divide(2f));

    // Synchronize the elements used for model manipulation (environment menu items and environment toolbar buttons)
    fxmlCtrl.placeActorBtn.selectedProperty().bindBidirectional(fxmlCtrl.placeActorMenuItem.selectedProperty());
    fxmlCtrl.placeTreeBtn.selectedProperty().bindBidirectional(fxmlCtrl.placeTreeMenuItem.selectedProperty());
    fxmlCtrl.placeStumpBtn.selectedProperty().bindBidirectional(fxmlCtrl.placeStumpMenuItem.selectedProperty());
    fxmlCtrl.placeWoodBtn.selectedProperty().bindBidirectional(fxmlCtrl.placeWoodMenuItem.selectedProperty());
    fxmlCtrl.placeWaterBtn.selectedProperty().bindBidirectional(fxmlCtrl.placeWaterMenuItem.selectedProperty());
    fxmlCtrl.deleteObjectBtn.selectedProperty().bindBidirectional(fxmlCtrl.deleteObjectMenuItem.selectedProperty());

    // Link the activation status of the toolbar and menu to the corresponding checkbox
    fxmlCtrl.editIslandToolBar.disableProperty().bind(
        fxmlCtrl.editIslandCheckBox.selectedProperty().not());
    fxmlCtrl.editIslandMenu.disableProperty().bind(fxmlCtrl.editIslandCheckBox.selectedProperty().not());
  }
}
