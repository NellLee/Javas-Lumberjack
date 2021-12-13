package praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects;

public class Tree implements PhysicalObject {

  private final int rotation = (int) (Math.random()*360);

  @Override
  public int getRotation() {
    return rotation;
  }
}
