package gui;

import model.Process;
import model.SimulationResult;
import scheduler.ACSScheduler;
import scheduler.PreemptiveSJF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Bridge controller between GUI parameters and the scheduling algorithms.
 * Performs rigorous input validation and runs the simulation on fresh process
 * clones.
 */
public class SimulationController {

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static SimulationResult runSimulation(boolean isAcs,
            List<Process> inputProcesses,
            int quantum,
            int contextSwitch) throws ValidationException {
        return runSimulation(isAcs, inputProcesses, quantum, contextSwitch, 4);
    }

    public static SimulationResult runSimulation(boolean isAcs,
            List<Process> inputProcesses,
            int quantum,
            int contextSwitch,
            int agingStep) throws ValidationException {
        // Validate processes
        if (inputProcesses == null || inputProcesses.isEmpty()) {
            throw new ValidationException("Please enter at least one process to run the simulation.");
        }

        Set<String> seenIds = new HashSet<>();
        for (int i = 0; i < inputProcesses.size(); i++) {
            Process p = inputProcesses.get(i);
            String id = p.getProcessID();

            if (id == null || id.trim().isEmpty()) {
                throw new ValidationException("Process ID in row " + (i + 1) + " cannot be empty.");
            }
            id = id.trim();
            if (seenIds.contains(id)) {
                throw new ValidationException(
                        "Duplicate Process ID found: '" + id + "'. Each process must have a unique ID.");
            }
            seenIds.add(id);

            if (p.getArrivalTime() < 0) {
                throw new ValidationException("Process '" + id + "' has invalid Arrival Time (" + p.getArrivalTime()
                        + "). Must be 0 or greater.");
            }
            if (p.getBurstTime() <= 0) {
                throw new ValidationException("Process '" + id + "' has invalid Burst Time (" + p.getBurstTime()
                        + "). Must be greater than 0.");
            }
            if (p.getInitialPriority() <= 0) {
                throw new ValidationException("Process '" + id + "' has invalid Priority (" + p.getInitialPriority()
                        + "). Must be a positive integer greater than 0.");
            }
        }

        // Validate parameters
        if (quantum <= 0) {
            throw new ValidationException("Time Quantum (Q) must be greater than zero.");
        }
        if (contextSwitch < 0) {
            throw new ValidationException("Context Switching Time (CS) must be zero or greater.");
        }
        if (agingStep <= 0) {
            throw new ValidationException("Aging Step must be a positive integer greater than zero.");
        }

        // Clone processes for algorithm execution to prevent state corruption
        List<Process> clones = new ArrayList<>();
        for (Process p : inputProcesses) {
            clones.add(new Process(p));
        }

        if (isAcs) {
            return ACSScheduler.simulate(clones, quantum, contextSwitch, agingStep);
        } else {
            return PreemptiveSJF.simulate(clones, contextSwitch);
        }
    }
}
