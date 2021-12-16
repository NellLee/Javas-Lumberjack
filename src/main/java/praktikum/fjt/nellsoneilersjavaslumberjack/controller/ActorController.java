package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Lumberjack;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.LumberjackException;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.CoordinateConverterUtil;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Invisible;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class ActorController {

  private final FXMLController fxmlCtrl;
  private final Island island;
  private ContextMenu contextMenu;

  public ActorController(FXMLController fxmlCtrl, Island island) {
    this.fxmlCtrl = fxmlCtrl;
    this.island = island;
  }

  public void initialize() {
    initializeActorContextMenu();
    initializeActorBtnEventHandling();
  }

  private void initializeActorBtnEventHandling() {
    // Actions
    EventHandler<ActionEvent> turnLeftHandler = actionEvent -> {
      try {
        island.getActor().dreheLinks();
      } catch (LumberjackException e) {
        AlertFactory.createError(e.getMessage());
      }
    };
    fxmlCtrl.actorTurnLeftBtn.setOnAction(turnLeftHandler);
    fxmlCtrl.actorTurnLeftMenuItem.setOnAction(turnLeftHandler);
    EventHandler<ActionEvent> forwardHandler = actionEvent -> {
      try {
        island.getActor().vorwaerts();
      } catch (LumberjackException e) {
        AlertFactory.createError(e.getMessage());
      }
    };
    fxmlCtrl.actorForwardBtn.setOnAction(forwardHandler);
    fxmlCtrl.actorForwardMenuItem.setOnAction(forwardHandler);
    EventHandler<ActionEvent> turnRightHandler = actionEvent -> {
      try {
        island.getActor().dreheRechts();
      } catch (LumberjackException e) {
        AlertFactory.createError(e.getMessage());
      }
    };
    fxmlCtrl.actorTurnRightBtn.setOnAction(turnRightHandler);
    fxmlCtrl.actorTurnRightMenuItem.setOnAction(turnRightHandler);
    EventHandler<ActionEvent> takeWoodHandler = actionEvent -> {
      try {
        island.getActor().nimmHolz();
      } catch (LumberjackException e) {
        AlertFactory.createError(e.getMessage());
      }
    };
    fxmlCtrl.actorTakeWoodBtn.setOnAction(takeWoodHandler);
    fxmlCtrl.actorTakeWoodMenuItem.setOnAction(takeWoodHandler);
    EventHandler<ActionEvent> putWoodHandler = actionEvent -> {
      try {
        island.getActor().legeHolz();
      } catch (LumberjackException e) {
        AlertFactory.createError(e.getMessage());
      }
    };
    fxmlCtrl.actorPutWoodBtn.setOnAction(putWoodHandler);
    fxmlCtrl.actorPutWoodMenuItem.setOnAction(putWoodHandler);
    EventHandler<ActionEvent> cutTreeHandler = actionEvent -> {
      try {
        island.getActor().faelleBaum();
      } catch (LumberjackException e) {
        AlertFactory.createError(e.getMessage());
      }
    };
    fxmlCtrl.actorCutTreeBtn.setOnAction(cutTreeHandler);
    fxmlCtrl.actorCutTreeMenuItem.setOnAction(cutTreeHandler);

    // Queries
    fxmlCtrl.actorFrontFreeMenuItem.setOnAction(actionEvent -> AlertFactory.createInfo(String.valueOf(island.getActor().vorneBegehbar())));
    fxmlCtrl.actorAxeSwingableMenuItem.setOnAction(actionEvent -> AlertFactory.createInfo(String.valueOf(island.getActor().axtSchwingbar())));
    fxmlCtrl.actorFrontTreeMenuItem.setOnAction(actionEvent -> AlertFactory.createInfo(String.valueOf(island.getActor().vorneBaum())));
    fxmlCtrl.actorFrontWoodMenuItem.setOnAction(actionEvent -> AlertFactory.createInfo(String.valueOf(island.getActor().vorneHolz())));
    fxmlCtrl.actorHasWoodMenuItem.setOnAction(actionEvent -> AlertFactory.createInfo(String.valueOf(island.getActor().traegtHolz())));
  }

  private void initializeActorContextMenu() {
    updateActorContextMenu();
    fxmlCtrl.islandRegion.setOnContextMenuRequested(
        contextMenuEvent -> {
          if(CoordinateConverterUtil.toModelPos(contextMenuEvent.getX(), contextMenuEvent.getY()).equals(island.getActorPosition())) {
            contextMenu.show(fxmlCtrl.stage, contextMenuEvent.getScreenX(),
                contextMenuEvent.getScreenY());
          }
        });
  }

  public void updateActorContextMenu() {
    contextMenu = new ContextMenu();

    Lumberjack actor = island.getActor();
    ArrayList<Method> actorMethods = getActorMethods(actor);

    for (Method method : actorMethods) {
      MenuItem menuItem = new MenuItem(generateDisplayString(method));
      if (method.getParameterCount() == 0) {
        menuItem.setOnAction(actionEvent -> {
          island.deactivateObservableNotifications();
          try {
            method.setAccessible(true);
            Object result = method.invoke(actor);
            if(!method.getReturnType().equals(Void.TYPE) && result != null) {
              AlertFactory.createInfo(result.toString());
            }
          } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            if(e.getCause().getClass() != ThreadDeath.class) {
              AlertFactory.createError(e.getCause().getMessage());
            }
          } finally {
            island.activateObservableNotifications();
          }
        });
      } else {
        menuItem.setDisable(true);
      }
      contextMenu.getItems().add(menuItem);
    }
  }

  private String generateDisplayString(Method method) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(method.getReturnType().getSimpleName())
        .append(" ")
        .append(method.getName())
        .append("(");

    Class<?>[] parameterTypes = method.getParameterTypes();
    if(parameterTypes.length > 0) {
      for (Class<?> parameterType : parameterTypes) {
        stringBuilder.append(parameterType.getSimpleName())
            .append(", ");
      }
      stringBuilder.setLength(stringBuilder.length() - 2); // remove last ", "
    }
    stringBuilder.append(")");

    return stringBuilder.toString();
  }

  private ArrayList<Method> getActorMethods(Lumberjack actor) {
    ArrayList<Method> result = new ArrayList<>();

    for(Method method : Lumberjack.class.getDeclaredMethods()) {
      if(Modifier.isPublic(method.getModifiers()) && !isMainMethod(method)) {
        result.add(method);
      }
    }

    if (Lumberjack.class != actor.getClass()) {
      for(Method method : actor.getClass().getDeclaredMethods()) {
        int mod = method.getModifiers();
        if(!Modifier.isPrivate(mod) && !Modifier.isStatic(mod) && !Modifier.isAbstract(mod) && method.getAnnotation(
            Invisible.class) == null) {
          result.add(method);
        }
      }
    }
    return result;
  }

  private boolean isMainMethod(Method method) {
    return method.getName().equals("main");
  }
}
