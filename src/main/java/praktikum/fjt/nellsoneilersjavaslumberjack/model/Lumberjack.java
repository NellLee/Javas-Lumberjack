package praktikum.fjt.nellsoneilersjavaslumberjack.model;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Direction;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.BlickrichtungException;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.TrageException;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;

public class Lumberjack {

  private Island island;
  private Position position;
  private Direction direction;
  private boolean hasWood = false;

  // Empty constructor is only necessary to make the instantiation of derived classes through the class loader possible.
  // Always make sure that the actor is set to an actual island after instantiating this way,
  // since island is initialised to "null" here, which would lead to exceptions otherwise.
  public Lumberjack() {
    this(null);
  }

  public Lumberjack(Island island) {
    this(island, new Position(0, 0));
  }

  public Lumberjack(Island island, Position position) {
    this(island, position, Direction.RIGHT);
  }

  public Lumberjack(Island island, Position position, Direction direction) {
    this.position = position;
    this.direction = direction;
    this.island = island;
  }

  public void vorwaerts() throws BlickrichtungException {
    Position posInDir = getPositionInDirection();

    if(!island.posOnLand(posInDir)) {
      throw new BlickrichtungException("'vorwaerts' nicht möglich: Meer in Blickrichtung");
    }
    if(island.hasPhysObjectAt(posInDir)) {
      throw new BlickrichtungException("'vorwaerts' nicht möglich: Kein Platz in Blickrichtung");
    }

    setPosition(posInDir);
    island.getActorObservable().notifyObservers();
  }

  public void dreheLinks() {
    setDirection(getDirection().ordinal()-1);
  }

  public void dreheRechts() {
    setDirection(getDirection().ordinal()+1);
  }

  public void nimmHolz() throws BlickrichtungException {
    Position posInDir = getPositionInDirection();

    if(hasWood) {
      throw new TrageException("'nimmHolz' nicht möglich: Trägt bereits Holz");
    }
    if(!island.removeWoodAt(posInDir)) {
      throw new BlickrichtungException("'nimmHolz' nicht möglich: Kein Holz in Blickrichtung");
    }

    hasWood = true;
  }

  public void legeHolz() throws BlickrichtungException {
    Position posInDir = getPositionInDirection();

    if(!hasWood) {
      throw new TrageException("'legeHolz' nicht möglich: Trägt kein Holz");
    }
    if(!island.posOnLand(posInDir)) {
      throw new BlickrichtungException("'legeHolz' nicht möglich: Meer in Blickrichtung. Das Holz würde ins Meer fallen.");
    }
    if(!island.setWoodAt(posInDir)) {
      throw new BlickrichtungException("'legeHolz' nicht möglich: Kein Platz in Blickrichtung");
    }

    hasWood = false;
  }
  
  public void faelleBaum() throws BlickrichtungException {
    Position posInDir = getPositionInDirection();
    Position posInDirRight = getPositionInDirectionRight();

    if(hasWood) {
      throw new TrageException("'faelleBaum' nicht möglich: Trägt Holz");
    }
    if(!island.hasTreeAt(posInDir)) {
      throw new BlickrichtungException("'faelleBaum' nicht möglich: Kein Baum in Blickrichtung");
    }
    if(island.hasTreeAt(posInDirRight)) {
      throw new BlickrichtungException("'faelleBaum' nicht möglich: Kein Platz zum Schwingen der Axt. Baum in Blickrichtung rechts");
    }
    if(island.hasStumpWithWoodAt(posInDirRight)) {
      throw new BlickrichtungException("'faelleBaum' nicht möglich: Kein Platz zum Schwingen der Axt. Baumstumpf mit Holz in Blickrichtung rechts");
    }
    if(!island.posOnLand(posInDirRight)) {
      throw new BlickrichtungException("'faelleBaum' nicht möglich: Meer in Blickrichtung rechts. Das Holz würde ins Meer fallen.");
    }
    if(!island.setWoodAt(posInDirRight)) {
      throw new BlickrichtungException("'faelleBaum' nicht möglich: Kein Platz in Blickrichtung rechts für fallendes Holz.");
    }

    island.setPhysObjectAt(posInDir, new Stump());
  }

  public boolean vorneBegehbar() {
    Position posInDir = getPositionInDirection();
    return (island.posOnLand(posInDir) && !island.hasPhysObjectAt(posInDir));
  }

  public boolean axtSchwingbar() {
    Position posInDirRight = getPositionInDirectionRight();
    return !(island.hasTreeAt(posInDirRight) || island.hasStumpWithWoodAt(posInDirRight));
  }

  public boolean vorneBaum() {
    return island.hasTreeAt(getPositionInDirection());
  }

  public boolean vorneHolz() {
    return island.hasWoodAt(getPositionInDirection());
  }

  public boolean traegtHolz() {
    return hasWood;
  }


  public Direction getDirection() {
    return direction;
  }

  void setDirection(Direction direction) {
    this.direction = direction;
    island.getActorObservable().notifyObservers();
  }

  void setDirection(int directionInt) {
    setDirection(Direction.values()[Math.floorMod((directionInt), Direction.values().length)]);
  }

  public Position getPosition() {
    return position;
  }

  void setPosition(Position position) {
    this.position = position;
  }

  void setIsland(Island island) {
    this.island = island;
  }

  Position getPositionInDirection() {
    Position oldPos = getPosition();
    int newX = oldPos.getX();
    int newY = oldPos.getY();
    switch (direction) {
      case RIGHT -> {
        newX++;
      }
      case DOWN -> {
        newY++;
      }
      case LEFT -> {
        newX--;
      }
      case UP -> {
        newY--;
      }
    }
    return new Position(newX, newY);
  }

  private Position getPositionInDirectionRight() {
    Position posInDir = getPositionInDirection();
    Position pos = getPosition();
    Position posInDirRight = new Position();
    if(posInDir.getX() == pos.getX()) { // Direction is UP or DOWN
      int offset = pos.getY() - posInDir.getY();
      posInDirRight.setX(posInDir.getX()+offset);
      posInDirRight.setY(posInDir.getY());
    } else if(posInDir.getY() == pos.getY()) { // Direction is LEFT or RIGHT
      int offset = posInDir.getX() - pos.getX();
      posInDirRight.setX(posInDir.getX());
      posInDirRight.setY(posInDir.getY()+offset);
    }
    return posInDirRight;
  }

  public void main() {
  }
}
