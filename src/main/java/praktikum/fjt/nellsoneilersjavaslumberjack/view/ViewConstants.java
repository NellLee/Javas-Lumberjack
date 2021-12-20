package praktikum.fjt.nellsoneilersjavaslumberjack.view;

import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import praktikum.fjt.nellsoneilersjavaslumberjack.Main;

public final class ViewConstants {

  // Private ctor to avoid instantiation
  private ViewConstants() {}

  public static final Color GROUND_COLOR = Color.rgb(119, 203, 100);
  public static final Color WATER_COLOR = Color.rgb(2, 182, 255);
  public static final Color WATER_COLOR_TRANSPARENT = Color.rgb(2, 182, 255, 0.5d);
  public static final Color SAND_COLOR = Color.rgb(229, 231, 159);
  public static final int CELL_BORDER_SIZE = 4;
  public static final int INNER_CELL_SIZE = 36;
  public static final int CELL_SIZE = INNER_CELL_SIZE + 2 * CELL_BORDER_SIZE;
  public static final int CANVAS_BORDER_SIZE = 1 * CELL_SIZE;


  public static final Image STUMP_TEXTURE = new Image(
      Objects.requireNonNull(Main.class.getResource("images/Stump.png")).toString());
  public static final Image TREE_TEXTURE = new Image(
      Objects.requireNonNull(Main.class.getResource("images/Tree.png")).toString());
  public static final Image WOOD_TEXTURE = new Image(
      Objects.requireNonNull(Main.class.getResource("images/Wood.png")).toString());
  public static final Image ACTOR_TEXTURE = new Image(
      Objects.requireNonNull(Main.class.getResource("images/Lumberjack.png")).toString());
}
