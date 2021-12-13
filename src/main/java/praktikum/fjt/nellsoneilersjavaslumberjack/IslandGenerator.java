package praktikum.fjt.nellsoneilersjavaslumberjack;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Direction;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Tree;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Wood;

public class IslandGenerator {

  public static Island createExampleIsland() {
    Island island = new Island(9, 6, new Position(1, 4), Direction.RIGHT);
    createExampleOcean(island);
    createExampleObjects(island);
    return island;
  }

  private static void createExampleObjects(Island island) {
    // Create some objects
    island.setPhysObjectAt(new Position(6, 1), new Tree());
    island.setPhysObjectAt(new Position(7, 2), new Tree());
    island.setPhysObjectAt(new Position(3, 2), new Tree());
    island.setPhysObjectAt(new Position(3, 3), new Tree());
    island.setPhysObjectAt(new Position(4, 3), new Tree());
    island.setPhysObjectAt(new Position(1, 3), new Tree());
    island.setPhysObjectAt(new Position(2, 1), new Wood());
    island.setPhysObjectAt(new Position(7, 1), new Stump(new Wood()));
    island.setPhysObjectAt(new Position(3, 4), new Stump());
  }

  private static void createExampleOcean(Island island) {
    // Define the ocean (I know that this is ugly and long, but outside this test one would probably never define the ocean through code)
    island.setOceanAt(new Position(0, 0));
    island.setOceanAt(new Position(0, 1));
    island.setOceanAt(new Position(0, 2));
    island.setOceanAt(new Position(0, 3));
    island.setOceanAt(new Position(0, 4));
    island.setOceanAt(new Position(0, 5));
    island.setOceanAt(new Position(1, 5));
    island.setOceanAt(new Position(2, 5));
    island.setOceanAt(new Position(3, 5));
    island.setOceanAt(new Position(4, 5));
    island.setOceanAt(new Position(5, 5));
    island.setOceanAt(new Position(6, 5));
    island.setOceanAt(new Position(7, 5));
    island.setOceanAt(new Position(8, 5));
    island.setOceanAt(new Position(6, 4));
    island.setOceanAt(new Position(7, 4));
    island.setOceanAt(new Position(8, 4));
    island.setOceanAt(new Position(8, 3));
    island.setOceanAt(new Position(8, 2));
    island.setOceanAt(new Position(8, 1));
    island.setOceanAt(new Position(8, 0));
    island.setOceanAt(new Position(7, 0));
    island.setOceanAt(new Position(6, 0));
    island.setOceanAt(new Position(5, 0));
    island.setOceanAt(new Position(4, 0));
    island.setOceanAt(new Position(3, 0));
    island.setOceanAt(new Position(2, 0));
    island.setOceanAt(new Position(1, 0));
  }

}
