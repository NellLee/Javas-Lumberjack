package praktikum.fjt.nellsoneilersjavaslumberjack;

import javafx.application.Platform;
import praktikum.fjt.nellsoneilersjavaslumberjack.controller.SimulationController;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.LumberjackException;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.SimulationStopException;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observable;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observer;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class Simulation extends Thread {

  private final SimulationController simulationController;
  private final Observer simObserver;
  private final Island island;

  private volatile boolean pause;
  private volatile boolean stop;

  public Simulation(SimulationController simulationController, Island island) {
    this.island = island;
    this.simulationController = simulationController;
    simObserver = new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        if(Platform.isFxApplicationThread()) return;

        try {
          sleep(1000 - (simulationController.getSimSpeed() * 10L));

          if(isStop()) {
            throw new SimulationStopException();
          }

          while (isPause()) {
            synchronized (Simulation.this) {
              Simulation.this.wait();
            }
          }

          if(isStop()) {
            throw new SimulationStopException();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }


    };
  }

  @Override
  public void run() {
    island.getActorObservable().addObserver(simObserver);
    island.getPhysicalsObservable().addObserver(simObserver);
    try {
      island.getActor().main();
    } catch (SimulationStopException ignored) {
      //Nothing to do
    } catch (LumberjackException e) {
      Platform.runLater(() -> AlertFactory.createError("Simulation abgebrochen: " + e.getMessage()));
    } finally {
      island.getActorObservable().deleteObserver(simObserver);
      island.getPhysicalsObservable().deleteObserver(simObserver);
      simulationController.finalizeSim();
    }
  }

  public boolean isPause() {
    return pause;
  }

  public synchronized void setPause(boolean pause) {
    this.pause = pause;
  }

  public boolean isStop() {
    return stop;
  }

  public synchronized void setStop(boolean stop) {
    this.stop = stop;
  }

  public synchronized void syncedNotify() {
    notify();
  }
}
