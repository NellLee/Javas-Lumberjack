package praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes;

import java.util.Optional;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.PhysicalObject;

public class Tile {

  private PhysicalObject content;

  public Tile(PhysicalObject content) {
    this.content = content;
  }

  public Tile() { }

  public Optional<PhysicalObject> getContent() {
    return Optional.ofNullable(content);
  }

  public void setContent(PhysicalObject content) {
    this.content = content;
  }

  public void clearContent() {
    setContent(null);
  }
}
