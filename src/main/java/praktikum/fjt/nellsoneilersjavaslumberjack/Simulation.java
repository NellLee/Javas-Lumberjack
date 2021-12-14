package praktikum.fjt.nellsoneilersjavaslumberjack;

import praktikum.fjt.nellsoneilersjavaslumberjack.controller.SimulationController;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.SimulationStopException;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observable;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observer;

public class Simulation extends Thread {

  private Observer simObserver;

  private volatile boolean pause;
  private volatile boolean stop;
  private SimulationController simulationController;


  public Simulation(SimulationController simulationController) {

    this.simulationController = simulationController;
    simObserver = new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        //TODO temporal debug
        System.out.println("Simulation update called from: " + Thread.currentThread().getName() + "'");
        System.out.println();
        //FIXME this method somehow ALWAYS gets called from the sim thread instead of the fx thread, so the following line would always return
        //if(!Platform.isFxApplicationThread()) return;
          try {
            sleep(1000 / Math.max(simulationController.getSimSpeed(), 1));

            if(isStop()) {
              throw new SimulationStopException();
            }

            while (isPause()) {
              //FIXME Pause does not work yet. Can't be fixed before the other fixme is resolved
              wait();
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
    System.out.println("Running simulation thread: '" + Thread.currentThread().getName() + "'");
    System.out.println();
    simulationController.addActorSimObserver(simObserver);
    try {
      simulationController.runActorMain();
    } catch (SimulationStopException ignored) {

    } finally {
      simulationController.deleteActorSimObserver(simObserver);
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
