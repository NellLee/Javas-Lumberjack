package praktikum.fjt.nellsoneilersjavaslumberjack.model;

import static praktikum.fjt.nellsoneilersjavaslumberjack.ConsoleTester.ANSI_BLUE;
import static praktikum.fjt.nellsoneilersjavaslumberjack.ConsoleTester.ANSI_GREEN;
import static praktikum.fjt.nellsoneilersjavaslumberjack.ConsoleTester.ANSI_RED;
import static praktikum.fjt.nellsoneilersjavaslumberjack.ConsoleTester.ANSI_RESET;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import praktikum.fjt.nellsoneilersjavaslumberjack.ConsoleTester;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Direction;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Tile;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.PhysicalObject;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Tree;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Wood;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observable;

public class Island {
  private Tile[][] physObjGrid;
  private boolean[][] oceanGrid;
  private int height;
  private int width;

  private Lumberjack actor;
  private Position actorPosition;
  private Direction actorDirection;
  private boolean actorHasWood = false;

  private final Observable sizeObservable = new Observable();
  private final Observable oceanObservable = new Observable();
  private final Observable physicalsObservable = new Observable();
  private final Observable actorObservable = new Observable();

  public Island(int width, int height) {
    this(width, height, new Position(0, 0), Direction.RIGHT);
  }

  public Island(int width, int height, Position actorPosition, Direction actorDirection) {
    createGrids(width, height);
    this.actor = new Lumberjack(this);
    this.actorPosition = actorPosition;
    this.actorDirection = actorDirection;
  }

  private void createGrids(int width, int height) {
    if(height <= 0 || width <= 0) {
      throw new IllegalArgumentException("Island must have a width/height of at least 1");
    }
    this.height = height;
    this.width = width;

    this.physObjGrid = new Tile[width][height];
    this.oceanGrid = new boolean[width][height];
    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        physObjGrid[x][y] = new Tile();
        oceanGrid[x][y] = false;
      }
    }
  }

  public int getHeight() {
    synchronized (this) {
      return height;
    }
  }

  public int getWidth() {
    synchronized (this) {
      return width;
    }
  }

  public void setSize(int newWidth, int newHeight) {
    if(newWidth <= 0 || newHeight <= 0) {
      throw new IllegalArgumentException("Island must have a width/height of at least 1");
    }
    Tile[][] tmpPhysObjGrid = new Tile[newWidth][newHeight];
    boolean[][] tmpOceanGrid = new boolean[newWidth][newHeight];
    for(int x = 0; x < newWidth; x++){
      for(int y = 0; y < newHeight; y++){
        tmpPhysObjGrid[x][y] = new Tile();
        tmpOceanGrid[x][y] = false;
      }
    }
    int minWidth = Math.min(newWidth, width);
    int minHeight = Math.min(newHeight, height);
    for (int i = 0; i < minWidth; i++) {
      System.arraycopy(physObjGrid[i], 0, tmpPhysObjGrid[i], 0, minHeight);
      System.arraycopy(oceanGrid[i], 0, tmpOceanGrid[i], 0, minHeight);
    }

    synchronized (this) {
      physObjGrid = tmpPhysObjGrid;
      oceanGrid = tmpOceanGrid;
      width = newWidth;
      height = newHeight;
    }

    if(!posInRange(getActorPosition())) {
      int newX = 0;
      int newY = 0;
      Position newPos = new Position(newX, newY);

      // clear position
      deletePhysObjectAt(newPos);
      synchronized (this) {
        oceanGrid[newX][newY] = false;
      }

      setActorPosition(newPos);
    }

    sizeObservable.notifyObservers();
  }

  public Lumberjack getActor() {
    synchronized (this) {
      return actor;
    }
  }

  public void changeActor(Lumberjack newActor) {
    newActor.setIsland(this);

    synchronized (this) {
      actor = newActor;
    }

    actorObservable.notifyObservers();
  }

  public boolean posInRange(Position position) {
    synchronized (this) {
      int x = position.getX();
      int y = position.getY();
      return (0 <= x && x < width && 0 <= y && y < height);
    }
  }

  public boolean posOnLand(Position position) {
    if(!posInRange(position)) {
      return false;
    }
    synchronized (this) {
      return !oceanGrid[position.getX()][position.getY()];
    }
  }

  public boolean hasPhysObjectAt(Position position) {
    if(!posInRange(position)) {
      return false;
    }
    synchronized (this) {
      return physObjGrid[position.getX()][position.getY()].getContent().isPresent();
    }
  }

  public boolean hasTreeAt(Position position) {
    if(!posOnLand(position)) {
      return false;
    }

    Optional<PhysicalObject> contentOpt;
    synchronized (this) {
      contentOpt = physObjGrid[position.getX()][position.getY()].getContent();
    }

    if(contentOpt.isEmpty()) {
      return false;
    }
    return contentOpt.get() instanceof Tree;
  }

  public boolean hasStumpWithWoodAt(Position position) {
    if(!posOnLand(position)) {
      return false;
    }

    Optional<PhysicalObject> contentOpt;
    synchronized (this) {
      contentOpt = physObjGrid[position.getX()][position.getY()].getContent();
    }

    if(contentOpt.isEmpty()) {
      return false;
    }
    return (contentOpt.get() instanceof Stump stump && stump.hasWoodOnTop());
  }

  private boolean hasOnlyWoodAt(Position position) {
    if(!posOnLand(position)) {
      return false;
    }

    Optional<PhysicalObject> contentOpt;
    synchronized (this) {
      contentOpt = physObjGrid[position.getX()][position.getY()].getContent();
    }

    if(contentOpt.isEmpty()) {
      return false;
    }

    PhysicalObject content = contentOpt.get();
    return content instanceof Wood;
  }

  public boolean hasWoodAt(Position position) {
    return hasStumpWithWoodAt(position) || hasOnlyWoodAt(position);
  }

  public boolean removeWoodAt(Position position) {
    boolean physObjChanged = false;

    if(!posOnLand(position)) {
      return false;
    }

    synchronized (this) {
      Optional<PhysicalObject> contentOpt = physObjGrid[position.getX()][position.getY()].getContent();
      if(contentOpt.isEmpty()) {
        return false;
      }
      PhysicalObject content = contentOpt.get();

      if (content instanceof Wood) {
        physObjGrid[position.getX()][position.getY()].clearContent();
        physObjChanged = true;
      }else if (content instanceof Stump stump && stump.hasWoodOnTop()) {
        stump.clearWoodOnTop();
        physObjChanged = true;
      }
    }

    if(physObjChanged) {
      physicalsObservable.notifyObservers();
    }

    return physObjChanged;
  }

  public boolean setWoodAt(Position position) {
    if(!posOnLand(position)) {
      return false;
    }

    Optional<PhysicalObject> contentOpt;
    synchronized (this) {
      contentOpt = physObjGrid[position.getX()][position.getY()].getContent();
    }

    if(contentOpt.isPresent()) {
      return setWoodOnPhysObject(contentOpt.get());
    }

    setPhysObjectAt(position, new Wood());
    return true;
  }

  public boolean setWoodOnPhysObject(PhysicalObject content) {
    boolean success = false;

    synchronized (this) {
      if (content instanceof Stump stump && !stump.hasWoodOnTop()) {
        stump.setWoodOnTop(new Wood());
        success =  true;
      }
    }

    if(success) {
      physicalsObservable.notifyObservers();
    }

    return success;
  }

  public void setPhysObjectAt(Position position, PhysicalObject physObject) {
    if(!posOnLand(position) || getActorPosition() == position ) {
      return;
    }
    synchronized (this) {
      physObjGrid[position.getX()][position.getY()].setContent(physObject);
    }
    physicalsObservable.notifyObservers();
  }

  public void setPhysObjectAt(Position position, PhysicalObject physObject, boolean replace) {
    if(replace || !hasPhysObjectAt(position)) {
      setPhysObjectAt(position, physObject);
    }
  }

  public void deletePhysObjectAt(Position position) {
    if(hasPhysObjectAt(position)) {
      setPhysObjectAt(position, null);
      physicalsObservable.notifyObservers();
    }
  }

  public void setOceanAt(Position position, boolean ocean) {
    if(!posInRange(position) || getActorPosition() == position) {
      return;
    }
    deletePhysObjectAt(position);
    synchronized (this) {
      oceanGrid[position.getX()][position.getY()] = ocean;
    }
    oceanObservable.notifyObservers();
  }

  public void setOceanAt(Position position) {
    setOceanAt(position, true);
  }
  public boolean hasOceanAt(Position position) {
    synchronized (this) {
      return oceanGrid[position.getX()][position.getY()];
    }
  }

  public Optional<PhysicalObject> getPhysObjectAt(Position position) {
    synchronized (this) {
      return physObjGrid[position.getX()][position.getY()].getContent();
    }
  }

  public boolean isActorHasWood() {
    synchronized (this) {
      return actorHasWood;
    }
  }

  public void setActorHasWood(boolean actorHasWood) {
    synchronized (this) {
      this.actorHasWood = actorHasWood;
    }
  }

  public Direction getActorDirection() {
    synchronized (this) {
      return actorDirection;
    }
  }

  void setActorDirection(int directionInt) {
    setActorDirection(Direction.values()[Math.floorMod((directionInt), Direction.values().length)]);
  }

  void setActorDirection(Direction actorDirection) {
    synchronized (this) {
      this.actorDirection = actorDirection;
    }
    actorObservable.notifyObservers();
  }

  public Position getActorPosition() {
    synchronized (this) {
      return actorPosition;
    }
  }

  public void setActorPosition(Position pos) {
    if (getPhysObjectAt(pos).isEmpty() && !hasOceanAt(pos)) {
      synchronized (this) {
        actorPosition = pos;
      }
      actorObservable.notifyObservers();
    }
  }

  Position getPositionInDirection() {
    Position oldPos = getActorPosition();
    int newX = oldPos.getX();
    int newY = oldPos.getY();
    synchronized (this) {
      switch (actorDirection) {
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
    }
    return new Position(newX, newY);
  }

  Position getPositionInDirectionRight() {
    Position posInDir = getPositionInDirection();
    Position pos = getActorPosition();
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

  public Observable getSizeObservable() {
    return sizeObservable;
  }

  public Observable getOceanObservable() {
    return oceanObservable;
  }

  public Observable getPhysicalsObservable() {
    return physicalsObservable;
  }

  public Observable getActorObservable() {
    return actorObservable;
  }

  public void deactivateObservableNotifications() {
    physicalsObservable.deactivateNotification();
    oceanObservable.deactivateNotification();
    actorObservable.deactivateNotification();
    sizeObservable.deactivateNotification();
  }

  public void activateObservableNotifications() {
    physicalsObservable.activateNotification();
    oceanObservable.activateNotification();
    actorObservable.activateNotification();
    sizeObservable.activateNotification();
  }

  // This method is only for console tester usage and can be removed later.
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("  ");
    String[] colIndices = IntStream.range(0, getWidth())
        .mapToObj(String::valueOf)
        .toArray(String[]::new);
    Arrays.stream(colIndices).forEach(colIndex -> {
      result.append(colIndex).append(" ");
    });
    result.append("\n");


    for(int y = 0; y < getHeight() ; y++){
      result.append(y).append(" ");
      for(int x = 0; x < getWidth() ; x++){
        String color = ConsoleTester.ANSI_WHITE;
        String symbol = "□";
        if(oceanGrid[x][y]) {
          color = ANSI_BLUE;
          symbol = "■";
        }
        if(getActorPosition().equals(new Position(x, y))) {
          color = ANSI_RED;
          switch (getActorDirection()) {
            case RIGHT -> symbol = ">";
            case DOWN -> symbol = "v";
            case LEFT -> symbol = "<";
            case UP -> symbol = "^";
          }
        } else {
          Optional<PhysicalObject> contentOpt = physObjGrid[x][y].getContent();
          if(contentOpt.isPresent()) {
            PhysicalObject content = contentOpt.get();
            color = ANSI_GREEN;
            symbol = content.getClass().getSimpleName().substring(0, 1);
            // Special case
            if(content instanceof Stump stump && stump.hasWoodOnTop()) {
              symbol = "$";
            }
          }
        }
        result.append(color).append(symbol).append(ANSI_RESET).append(" ");
      }
      result.append("\n");
    }

    return result.toString();

  }
}
