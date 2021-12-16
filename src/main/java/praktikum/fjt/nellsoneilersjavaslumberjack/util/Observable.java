package praktikum.fjt.nellsoneilersjavaslumberjack.util;

import java.util.concurrent.CopyOnWriteArrayList;

public class Observable {

  private final CopyOnWriteArrayList<Observer> observers;
  private boolean notification;

  public Observable() {
    observers = new CopyOnWriteArrayList<Observer>();
    notification = true;
  }

  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  public void deleteObserver(Observer observer) {
    observers.remove(observer);
  }

  public void clearObservers() {
    observers.clear();
  }

  public CopyOnWriteArrayList<Observer> getObservers() {
    return observers;
  }

  public void activateNotification() {
    notification = true;
    notifyObservers();
  }

  public void deactivateNotification() {
    notification = false;
  }

  public void notifyObservers() {
    if (notification) {
      for (Observer observer : observers) {
        observer.update(this);
      }
    }
  }

  public void notifyObservers(Object... objects) {
    if (notification) {
      for (Observer observer : observers) {
        observer.update(this, objects);
      }
    }
  }
}
