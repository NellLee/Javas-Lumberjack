package praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes;

public class Position {

  private int x;
  private int y;

  public Position() {
    this(0,0);
  }

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    Position otherPos = (Position) other;
    return y == otherPos.y && x == otherPos.x;
  }

  @Override
  public String toString() {
    return "x=" + x +
        ", y=" + y;
  }
}
