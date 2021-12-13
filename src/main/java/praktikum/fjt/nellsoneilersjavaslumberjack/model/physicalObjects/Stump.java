package praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects;

public class Stump implements PhysicalObject{

  private final int rotation = (int) (Math.random()*360);
  private Wood woodOnTop;


  public Stump() { }

  public Stump(Wood woodOnTop) {
    this.woodOnTop = woodOnTop;
  }


  public boolean hasWoodOnTop() {
    return woodOnTop != null;
  }

  public void setWoodOnTop(Wood woodOnTop) {
    this.woodOnTop = woodOnTop;
  }

  public void clearWoodOnTop() {
    this.woodOnTop = null;
  }

  @Override
  public int getRotation() {
    return rotation;
  }
}
