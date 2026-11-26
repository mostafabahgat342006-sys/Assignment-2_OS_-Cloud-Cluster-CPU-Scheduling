package model;

/**
 * Model representing a row in the official 3-column Heuristic Demotion Matrix:
 * | Time | Process | Tier |
 */
public class MatrixRowModel {

    private final int time;
    private final String process;
    private final String tier;

    public MatrixRowModel(int time, String process, String tier) {
        this.time = time;
        this.process = (process != null) ? process : "";
        this.tier = (tier != null) ? tier : "";
    }

    public int getTime() {
        return time;
    }

    public String getProcess() {
        return process;
    }

    public String getTier() {
        return tier;
    }
}
