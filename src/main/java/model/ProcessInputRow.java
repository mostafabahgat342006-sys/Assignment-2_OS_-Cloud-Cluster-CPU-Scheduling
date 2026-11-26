package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Observable model for the editable Process input TableView in the sidebar.
 */
public class ProcessInputRow {

    private final StringProperty processID;
    private final IntegerProperty arrivalTime;
    private final IntegerProperty burstTime;
    private final IntegerProperty initialPriority;

    public ProcessInputRow(String processID, int arrivalTime, int burstTime, int initialPriority) {
        this.processID = new SimpleStringProperty(processID);
        this.arrivalTime = new SimpleIntegerProperty(arrivalTime);
        this.burstTime = new SimpleIntegerProperty(burstTime);
        this.initialPriority = new SimpleIntegerProperty(initialPriority);
    }

    public String getProcessID() {
        return processID.get();
    }

    public void setProcessID(String value) {
        this.processID.set(value);
    }

    public StringProperty processIDProperty() {
        return processID;
    }

    public int getArrivalTime() {
        return arrivalTime.get();
    }

    public void setArrivalTime(int value) {
        this.arrivalTime.set(value);
    }

    public IntegerProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime.get();
    }

    public void setBurstTime(int value) {
        this.burstTime.set(value);
    }

    public IntegerProperty burstTimeProperty() {
        return burstTime;
    }

    public int getInitialPriority() {
        return initialPriority.get();
    }

    public void setInitialPriority(int value) {
        this.initialPriority.set(value);
    }

    public IntegerProperty initialPriorityProperty() {
        return initialPriority;
    }

    public Process toProcess() {
        return new Process(getProcessID(), getArrivalTime(), getBurstTime(), getInitialPriority());
    }
}
