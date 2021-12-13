package praktikum.fjt.nellsoneilersjavaslumberjack.util;

import static praktikum.fjt.nellsoneilersjavaslumberjack.view.ViewConstants.*;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;

public class CoordinateConverterUtil {

  public static double toViewCoord(int pos) {
    return pos * CELL_SIZE + CANVAS_BORDER_SIZE;
  }

  private static int toModelCoord(double pos) {
    return (int)(pos - CANVAS_BORDER_SIZE) / CELL_SIZE;
  }

  public static Position toModelPos(double x, double y) {
    return new Position(toModelCoord(x), toModelCoord(y));
  }
}
