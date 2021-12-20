package praktikum.fjt.nellsoneilersjavaslumberjack.view;

import static praktikum.fjt.nellsoneilersjavaslumberjack.view.ViewConstants.*;

import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.PhysicalObject;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Tree;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Wood;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.CoordinateConverterUtil;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observable;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observer;

public class IslandRegion extends Region {

  private final Canvas backgroundCanvas = new Canvas();
  private final Canvas oceanCanvas = new Canvas();
  private final Canvas gridCanvas = new Canvas();
  private final Canvas physicalsCanvas = new Canvas();
  private final Canvas actorCanvas = new Canvas();

  private final Island island;

  public IslandRegion(Island island) {
    this.island = island;

    createCanvases();

    setCanvasesSizes();

    addIslandObservers(island);

    // Invoke first draw
    drawAll();
  }

  private void addIslandObservers(Island island) {
    island.getSizeObservable().addObserver(new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        Platform.runLater(() -> drawAll());
      }
    });
    island.getOceanObservable().addObserver(new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        Platform.runLater(() -> drawOcean());
      }
    });
    island.getPhysicalsObservable().addObserver(new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        Platform.runLater(() -> drawPhysicals());
      }
    });
    island.getActorObservable().addObserver(new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        Platform.runLater(() -> drawActor());
      }
    });
  }

  private void createCanvases() {
    this.getChildren().addAll(backgroundCanvas, oceanCanvas, gridCanvas, physicalsCanvas, actorCanvas);
  }

  private void setCanvasesSizes() {
    double width = getRegionWidth();
    double height = getRegionHeight();

    backgroundCanvas.setWidth(width);
    backgroundCanvas.setHeight(height);
    oceanCanvas.setWidth(width);
    oceanCanvas.setHeight(height);
    gridCanvas.setWidth(width);
    gridCanvas.setHeight(height);
    physicalsCanvas.setWidth(width);
    physicalsCanvas.setHeight(height);
    actorCanvas.setWidth(width);
    actorCanvas.setHeight(height);
  }

  private double getRegionHeight() {
    return getInnerHeight() + CANVAS_BORDER_SIZE * 2;
  }

  private double getRegionWidth() {
    return getInnerWidth() + CANVAS_BORDER_SIZE * 2;
  }

  private double getInnerWidth() {
    return island.getWidth() * CELL_SIZE;
  }

  private double getInnerHeight() {
    return island.getHeight() * CELL_SIZE;
  }


  private void drawAll() {
    setCanvasesSizes();
    drawBackground();
    drawOcean();
    drawGrid();
    drawPhysicals();
    drawActor();
  }

  private void drawBackground() {
    GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, getRegionWidth(), getRegionHeight());

    gc.setStroke(Color.BLACK);
    gc.setLineWidth(5.0d);
    gc.strokeRect(CANVAS_BORDER_SIZE, CANVAS_BORDER_SIZE, getInnerWidth(), getInnerHeight());

    gc.setFill(GROUND_COLOR);
    gc.fillRect(CANVAS_BORDER_SIZE, CANVAS_BORDER_SIZE, getInnerWidth(), getInnerHeight());
  }

  private void drawOcean() {
    GraphicsContext gc = oceanCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, getRegionWidth(), getRegionHeight());

    drawWater(gc, WATER_COLOR);

    gc.setLineWidth(15d);
    drawSand(gc);

    // Second water iteration to make the sand look like it's actually in the water
    drawWater(gc, WATER_COLOR_TRANSPARENT);
  }

  private void drawWater(GraphicsContext gc, Color waterColor) {
    for (int y = 0; y < island.getHeight(); y++) {
      double canvasY  = CoordinateConverterUtil.toViewCoord(y);
      for (int x = 0; x < island.getWidth(); x++) {
        double canvasX  = CoordinateConverterUtil.toViewCoord(x);

        if(island.hasOceanAt(new Position(x, y))) {
          gc.setFill(waterColor);
          gc.fillRect(canvasX, canvasY, CELL_SIZE, CELL_SIZE);
        }
      }
    }
  }

  private void drawSand(GraphicsContext gc) {
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setStroke(SAND_COLOR);

    for (int y = 0; y < island.getHeight(); y++) {
      double canvasY  = CoordinateConverterUtil.toViewCoord(y);
      for (int x = 0; x < island.getWidth(); x++) {
        double canvasX  = CoordinateConverterUtil.toViewCoord(x);

        double x1;
        double x2;
        double y1;
        double y2;

        if(!island.hasOceanAt(new Position(x, y))) {
          if(island.posInRange(new Position(x-1, y)) && island.hasOceanAt(new Position(x-1, y))) {
            x1 = canvasX;
            y1 = canvasY;
            x2 = canvasX;
            y2 = canvasY+ CELL_SIZE;
            gc.strokeLine(x1, y1, x2, y2);
          }
          if(island.posInRange(new Position(x+1, y)) && island.hasOceanAt(new Position(x+1, y))) {
            x1 = canvasX+ CELL_SIZE;
            y1 = canvasY;
            x2 = canvasX+ CELL_SIZE;
            y2 = canvasY+ CELL_SIZE;
            gc.strokeLine(x1, y1, x2, y2);
          }
          if(island.posInRange(new Position(x, y-1)) && island.hasOceanAt(new Position(x, y-1))) {
            x1 = canvasX;
            y1 = canvasY;
            x2 = canvasX + CELL_SIZE;
            y2 = canvasY;
            gc.strokeLine(x1, y1, x2, y2);
          }
          if(island.posInRange(new Position(x, y+1)) && island.hasOceanAt(new Position(x, y+1))) {
            x1 = canvasX;
            y1 = canvasY+ CELL_SIZE;
            x2 = canvasX+ CELL_SIZE;
            y2 = canvasY+ CELL_SIZE;
            gc.strokeLine(x1, y1, x2, y2);
          }
        }
      }
    }

    clearClippingSand(gc);
  }

  private void clearClippingSand(GraphicsContext gc) {
    //Top
    gc.clearRect(0, 0, getRegionWidth(), CANVAS_BORDER_SIZE);
    //Left
    gc.clearRect(0, 0, CANVAS_BORDER_SIZE, getRegionHeight());
    //Right
    gc.clearRect(getRegionWidth() - CANVAS_BORDER_SIZE, 0, CANVAS_BORDER_SIZE, getRegionHeight());
    //Bottom
    gc.clearRect(0, getRegionHeight() - CANVAS_BORDER_SIZE, getRegionWidth(), CANVAS_BORDER_SIZE);
  }

  private void drawGrid() {
    GraphicsContext gc = gridCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, getRegionWidth(), getRegionHeight());

    gc.setLineWidth(0.8d);
    for (int y = 0; y < island.getHeight(); y++) {
      double canvasY  = CoordinateConverterUtil.toViewCoord(y);
      for (int x = 0; x < island.getWidth(); x++) {
        double canvasX  = CoordinateConverterUtil.toViewCoord(x);
        gc.strokeRect(canvasX, canvasY, CELL_SIZE, CELL_SIZE);

      }
    }
  }

  private void drawPhysicals() {
    GraphicsContext gc = physicalsCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, getRegionWidth(), getRegionHeight());

    for (int y = 0; y < island.getHeight(); y++) {
      double canvasY  = CoordinateConverterUtil.toViewCoord(y) + CELL_BORDER_SIZE;
      for (int x = 0; x < island.getWidth(); x++) {
        double canvasX  = CoordinateConverterUtil.toViewCoord(x) + CELL_BORDER_SIZE;

        Optional<PhysicalObject> contentOpt = island.getPhysObjectAt(new Position(x, y));
        if(contentOpt.isPresent()) {
          PhysicalObject content = contentOpt.get();

          if(content instanceof Tree) {
            drawRotatedImage(gc, TREE_TEXTURE, content.getRotation(), canvasX, canvasY);
          } else if(content instanceof Stump stump) {
            drawRotatedImage(gc, STUMP_TEXTURE, content.getRotation(), canvasX, canvasY);
            // Special case
            if(stump.hasWoodOnTop()) {
              gc.drawImage(WOOD_TEXTURE, canvasX, canvasY);
            }
          } else if(content instanceof Wood) {
            drawRotatedImage(gc, WOOD_TEXTURE, content.getRotation(), canvasX, canvasY);
          }
        }
      }
    }
  }

  private void drawActor() {
    GraphicsContext gc = actorCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, getRegionWidth(), getRegionHeight());

    Position actorPos = island.getActorPosition();
    // The lumberjacks texture is twice as large as a tile
    // Because of this, the rotation is done around a translated origin (0.25 of the width/height)
    drawRotatedImage(gc, ACTOR_TEXTURE, island.getActorDirection().ordinal() * 90, CoordinateConverterUtil.toViewCoord(actorPos.getX()) + CELL_BORDER_SIZE, CoordinateConverterUtil.toViewCoord(actorPos.getY()) + CELL_BORDER_SIZE, 0.25, 0.25);
  }


  /**
   * Sets the transform for the GraphicsContext to rotate around a pivot point.
   *
   * Source: Stack Overflow (https://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas)
   *
   * @param gc the graphics context the transform to applied to.
   * @param angle the angle of rotation.
   * @param px the x pivot co-ordinate for the rotation (in canvas co-ordinates).
   * @param py the y pivot co-ordinate for the rotation (in canvas co-ordinates).
   */
  private void rotate(GraphicsContext gc, double angle, double px, double py) {
    Rotate r = new Rotate(angle, px, py);
    gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
  }

  /**
   * Draws an image on a graphics context.
   *
   * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
   *   (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
   *
   * Source: Stack Overflow (https://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas)
   * (slightly enhanced)
   *
   * @param gc the graphics context the image is to be drawn on.
   * @param angle the angle of rotation.
   * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
   * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
   * @param ox the origins x co-ordinate for image rotation (relative factor of image width).
   * @param oy the origins y co-ordinate for image rotation (relative factor of image height).
   */
  private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy, double ox, double oy) {
    gc.save(); // saves the current state on stack, including the current transform
    rotate(gc, angle, tlpx + image.getWidth() * ox, tlpy + image.getHeight() * oy);
    gc.drawImage(image, tlpx, tlpy);
    gc.restore(); // back to original state (before rotation)
  }

  private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
    drawRotatedImage(gc, image, angle, tlpx, tlpy, 0.5d, 0.5d);
  }

}
