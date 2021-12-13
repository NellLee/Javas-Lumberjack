package praktikum.fjt.nellsoneilersjavaslumberjack;

import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.exceptions.SimulationStopException;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observable;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observer;

public class Simulation extends Thread {

  private Island island;
  private Observer islandObserver;

  volatile boolean pause;
  volatile boolean stop;

  public Simulation(Island island) {
    this.island = island;
    islandObserver = new Observer() {
      @Override
      public void update(Observable obs, Object... objects) {
        //if(!isFXApplicationThread()) return;
        int sleepTime = 100; //TODO slider
        try {
          sleep(sleepTime);

          if(stop) {
            throw new SimulationStopException();
          }

          while (pause) {
            wait();
          }

          if(stop) {
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
    //TODO
  }
}
