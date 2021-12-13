package praktikum.fjt.nellsoneilersjavaslumberjack.util;

import java.util.ArrayList;

public class Observable {

  private final ArrayList<Observer> observers;
  private boolean notification;

  public Observable() {
    observers = new ArrayList<Observer>();
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

  public ArrayList<Observer> getObservers() {
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