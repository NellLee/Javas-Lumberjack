package praktikum.fjt.nellsoneilersjavaslumberjack.model;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.BlickrichtungException;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.TrageException;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;

public class Lumberjack {

  private Island island;

  // Empty constructor is only necessary to make the instantiation of derived classes through the class loader possible.
  // Always make sure that the actor is bound to an actual island after instantiating this way,
  // since island is initialised to "null" here, which would lead to exceptions otherwise.
  public Lumberjack() {
    this(null);
  }

  public Lumberjack(Island island) {
    this.island = island;
  }

  public void vorwaerts() throws BlickrichtungException {
    Position posInDir = island.getPositionInDirection();

    if(!island.posOnLand(posInDir)) {
      throw new BlickrichtungException("'vorwaerts' nicht möglich: Meer in Blickrichtung");
    }
    if(island.hasPhysObjectAt(posInDir)) {
      throw new BlickrichtungException("'vorwaerts' nicht möglich: Kein Platz in Blickrichtung");
    }

    island.setActorPosition(posInDir);
  }

  public void dreheLinks() {
    island.setActorDirection(island.getActorDirection().ordinal()-1);
  }

  public void dreheRechts() {
    island.setActorDirection(island.getActorDirection().ordinal()+1);
  }

  public void nimmHolz() throws BlickrichtungException {
    Position posInDir = island.getPositionInDirection();

    if(island.isActorHasWood()) {
      throw new TrageException("'nimmHolz' nicht möglich: Trägt bereits Holz");
    }
    if(!island.removeWoodAt(posInDir)) {
      throw new BlickrichtungException("'nimmHolz' nicht möglich: Kein Holz in Blickrichtung");
    }

    island.setActorHasWood(true);
  }

  public void legeHolz() throws BlickrichtungException {
    Position posInDir = island.getPositionInDirection();

    if(!island.isActorHasWood()) {
      throw new TrageException("'legeHolz' nicht möglich: Trägt kein Holz");
    }
    if(!island.posOnLand(posInDir)) {
      throw new BlickrichtungException("'legeHolz' nicht möglich: Meer in Blickrichtung. Das Holz würde ins Meer fallen.");
    }
    if(!island.setWoodAt(posInDir)) {
      throw new BlickrichtungException("'legeHolz' nicht möglich: Kein Platz in Blickrichtung");
    }

    island.setActorHasWood(false);
  }
  
  public void faelleBaum() throws BlickrichtungException {
    Position posInDir = island.getPositionInDirection();
    Position posInDirRight = island.getPositionInDirectionRight();

    if(island.isActorHasWood()) {
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

    /* Temporarily deactivating the physical object observable notification.
       The two actions (making the tree a stump and placing wood next to it) will now only cause one update and
       will therefore visually appear as one action.
       (Since these actions are normally executed immediately after each other, this is only relevant for the simulation
       where there will be thread sleep between actions)
     */
    island.getPhysicalsObservable().deactivateNotification();
    if(!island.setWoodAt(posInDirRight)) {
      throw new BlickrichtungException("'faelleBaum' nicht möglich: Kein Platz in Blickrichtung rechts für fallendes Holz.");
    }
    island.setPhysObjectAt(posInDir, new Stump());
    island.getPhysicalsObservable().activateNotification();
  }

  public boolean vorneBegehbar() {
    Position posInDir = island.getPositionInDirection();
    return (island.posOnLand(posInDir) && !island.hasPhysObjectAt(posInDir));
  }

  public boolean axtSchwingbar() {
    Position posInDirRight = island.getPositionInDirectionRight();
    return !(island.hasTreeAt(posInDirRight) || island.hasStumpWithWoodAt(posInDirRight));
  }

  public boolean vorneBaum() {
    return island.hasTreeAt(island.getPositionInDirection());
  }

  public boolean vorneHolz() {
    return island.hasWoodAt(island.getPositionInDirection());
  }

  public boolean traegtHolz() {
    return island.isActorHasWood();
  }

  void setIsland(Island island) {
    this.island = island;
  }

  public void main() {
  }
}
