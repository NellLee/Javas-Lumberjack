package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import praktikum.fjt.nellsoneilersjavaslumberjack.Simulation;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.util.Observer;

public class SimulationController {

    private FXMLController fxmlCtrl;
    private Island island;
    private Simulation sim;
    private volatile int simSpeed;

    public SimulationController(FXMLController fxmlCtrl, Island island) {
        this.fxmlCtrl = fxmlCtrl;
        this.island = island;
    }

    public void initialize() {
        initializeSimBtnEventHandling();
        initializeSimSpeedSliderHandling();

        enableOnlyStartButton();
    }

    private void initializeSimSpeedSliderHandling() {
        setSimSpeed((int) fxmlCtrl.simSpeedSlider.getValue());
        fxmlCtrl.simSpeedSlider.valueProperty().addListener((observable, oldVal, newVal) -> {
            setSimSpeed(newVal.intValue());
        });
    }

    private void initializeSimBtnEventHandling() {
        EventHandler<ActionEvent> startSimHandler = actionEvent -> {
            if (sim == null || sim.isStop()) {
                start();
            } else {
                resume();
            }
        };
        fxmlCtrl.startSimButton.setOnAction(startSimHandler);
        fxmlCtrl.startSimMenuItem.setOnAction(startSimHandler);
        fxmlCtrl.pauseSimButton.setOnAction(actionEvent -> pause());
        fxmlCtrl.pauseSimMenuItem.setOnAction(actionEvent -> pause());
        fxmlCtrl.stopSimButton.setOnAction(actionEvent -> stop());
        fxmlCtrl.stopSimMenuItem.setOnAction(actionEvent -> stop());
    }

    public void start() {
        sim = new Simulation(this);
        sim.setDaemon(true);
        sim.start();

        disableOnlyStartButton();
    }

    public void resume() {
        sim.setPause(false);
        sim.syncedNotify();

        disableOnlyStartButton();
    }

    public void pause() {
        sim.setPause(true);

        disableOnlyPauseButton();
    }

    public void stop() {
        sim.setStop(true);
        sim.setPause(false);
        sim.syncedNotify();

        enableOnlyStartButton();
    }

    public void addActorSimObserver(Observer simObserver) {
        island.getActorObservable().addObserver(simObserver);
    }

    public void deleteActorSimObserver(Observer simObserver) {
        island.getActorObservable().deleteObserver(simObserver);
    }

    public void runActorMain() {
        island.getActor().main();
    }

    public int getSimSpeed() {
        return simSpeed;
    }

    public void setSimSpeed(int simSpeed) {
        this.simSpeed = simSpeed;
    }

    public void finalizeSim() {
        sim = null;
        enableOnlyStartButton();
    }

    private void enableOnlyStartButton() {
        fxmlCtrl.startSimButton.setDisable(false);
        fxmlCtrl.startSimMenuItem.setDisable(false);
        fxmlCtrl.pauseSimButton.setDisable(true);
        fxmlCtrl.pauseSimMenuItem.setDisable(true);
        fxmlCtrl.stopSimButton.setDisable(true);
        fxmlCtrl.stopSimMenuItem.setDisable(true);
    }

    private void disableOnlyPauseButton() {
        fxmlCtrl.startSimButton.setDisable(false);
        fxmlCtrl.startSimMenuItem.setDisable(false);
        fxmlCtrl.pauseSimButton.setDisable(true);
        fxmlCtrl.pauseSimMenuItem.setDisable(true);
        fxmlCtrl.stopSimButton.setDisable(false);
        fxmlCtrl.stopSimMenuItem.setDisable(false);
    }

    private void disableOnlyStartButton() {
        fxmlCtrl.startSimButton.setDisable(true);
        fxmlCtrl.startSimMenuItem.setDisable(true);
        fxmlCtrl.pauseSimButton.setDisable(false);
        fxmlCtrl.pauseSimMenuItem.setDisable(false);
        fxmlCtrl.stopSimButton.setDisable(false);
        fxmlCtrl.stopSimMenuItem.setDisable(false);
    }
}
