package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;

public class IslandController {
  private final Island island;

  private final FXMLController fxmlCtrl;

  private IslandPlaceModeController islandPlaceModeController;
  private IslandSizeChangeController islandSizeChangeController;
  private IslandSaveController islandSaveController;
  private ActorController actorController;


  public IslandController(FXMLController fxmlCtrl, Island island) {
    this.island = island;
    this.fxmlCtrl = fxmlCtrl;
  }


  public void initialize() {
    islandPlaceModeController = new IslandPlaceModeController(fxmlCtrl, island);
    islandPlaceModeController.initialize();

    islandSizeChangeController = new IslandSizeChangeController(fxmlCtrl, island);
    islandSizeChangeController.initialize();

    actorController = new ActorController(fxmlCtrl, island);
    actorController.initialize();

    islandSaveController = new IslandSaveController(fxmlCtrl, island);
    islandSaveController.initialize();
  }

  public void updateActorContextMenu() {
    actorController.updateActorContextMenu();
  }

  public void refreshIslandSizeSpinners() {
    islandSizeChangeController.refreshSpinners();
  }
}
