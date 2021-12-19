package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Tree;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.CoordinateConverterUtil;

public class IslandPlaceModeController {

  private final FXMLController fxmlCtrl;

  private final Island island;

  public enum PlaceMode {
    Tree,
    Wood,
    Stump,
    Actor,
    Water,
    Delete,
    None
  }

  private PlaceMode currentPlaceMode = PlaceMode.None;
  private boolean isDraggingActor = false;

  public IslandPlaceModeController(FXMLController fxmlCtrl, Island island) {
    this.island = island;
    this.fxmlCtrl = fxmlCtrl;
  }

  public void initialize() {
    initializePlaceModeBtnEventHandling();
  }

  private void initializePlaceModeBtnEventHandling() {
    fxmlCtrl.islandRegion.setOnMousePressed(mouseEvent -> {
      Position clickPosition = CoordinateConverterUtil.toModelPos(mouseEvent.getX(), mouseEvent.getY());
      if(clickPosition.equals(island.getActorPosition())) {
        startDraggingActor();
      } else {
        stopDraggingActor();
        executeCurrentPlaceMode(clickPosition);
      }
    });
    fxmlCtrl.islandRegion.setOnMouseReleased(mouseEvent -> stopDraggingActor());
    fxmlCtrl.islandRegion.setOnMouseDragged(mouseEvent -> executeDragging(CoordinateConverterUtil.toModelPos(mouseEvent.getX(), mouseEvent.getY())));
    fxmlCtrl.placeActorBtn.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Actor));
    fxmlCtrl.placeActorMenuItem.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Actor));
    fxmlCtrl.placeTreeBtn.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Tree));
    fxmlCtrl.placeTreeMenuItem.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Tree));
    fxmlCtrl.placeStumpBtn.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Stump));
    fxmlCtrl.placeStumpMenuItem.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Stump));
    fxmlCtrl.placeWoodBtn.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Wood));
    fxmlCtrl.placeWoodMenuItem.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Wood));
    fxmlCtrl.placeWaterBtn.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Water));
    fxmlCtrl.placeWaterMenuItem.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Water));
    fxmlCtrl.deleteObjectBtn.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Delete));
    fxmlCtrl.deleteObjectMenuItem.setOnAction(actionEvent -> setCurrentPlaceModeOrReset(PlaceMode.Delete));
    fxmlCtrl.editIslandCheckBox.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
      if(!newVal) {
        setCurrentPlaceModeOrReset(PlaceMode.None);
      }
    });
  }


  private void startDraggingActor() {
    isDraggingActor = true;
  }

  private void stopDraggingActor() {
    isDraggingActor = false;
  }

  private void executeDragging(Position dragPosition) {
    if (fxmlCtrl.editIslandCheckBox.isSelected()) {
      if (isDraggingActor) {
        if (island.posOnLand(dragPosition) && !island.hasPhysObjectAt(dragPosition)) {
          island.setActorPosition(dragPosition);
        }
      } else if (currentPlaceMode == PlaceMode.Water || currentPlaceMode == PlaceMode.Delete) {
        executeCurrentPlaceMode(dragPosition);
      }
    }
  }

  private void executeCurrentPlaceMode(Position clickPosition) {
    switch(currentPlaceMode) {
      case Actor -> {
        island.setActorPosition(clickPosition);
      }
      case Tree -> {
        island.setPhysObjectAt(clickPosition, new Tree(), false);
      }
      case Wood -> {
        island.setWoodAt(clickPosition);
      }
      case Stump -> {
        island.setPhysObjectAt(clickPosition, new Stump(), false);
      }
      case Water -> {
        island.setOceanAt(clickPosition);
      }
      case Delete -> {
        island.deletePhysObjectAt(clickPosition);
        island.setOceanAt(clickPosition, false);
      }
    }

  }

  private void setCurrentPlaceModeOrReset(PlaceMode newPlaceMode) {
    if(newPlaceMode == currentPlaceMode) {
      currentPlaceMode = PlaceMode.None;
    } else {
      currentPlaceMode = newPlaceMode;
    }
  }
}
