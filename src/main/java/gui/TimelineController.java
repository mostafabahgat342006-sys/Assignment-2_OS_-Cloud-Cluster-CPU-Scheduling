package gui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.TimelineSegment;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller responsible for drawing the Gantt-style execution timeline
 * under the Midnight Control Center visual identity.
 */
public class TimelineController {

    private final Pane paneTimeline;
    private final Label lblPlaceholder;
    private final HBox legendContainer;

    // Midnight Control Center Process Palette
    private static final String[] PROCESS_COLORS = {
            "#4F8CFF", // Electric Blue (P1)
            "#42D6AE", // Mint Green (P2)
            "#9875FF", // Violet (P3)
            "#FFB454", // Amber (P4)
            "#FF6B6B", // Coral (P5)
            "#00F2FE", // Cyan (P6)
            "#F43F5E", // Rose Pink (P7)
            "#84CC16"  // Lime (P8)
    };

    // Context Switch styling
    private static final String CS_BG_COLOR = "#1B2740";
    private static final String CS_BORDER_COLOR = "#64748B";
    private static final String CS_TEXT_COLOR = "#94A3B8";

    // Axis styling
    private static final String AXIS_LINE_COLOR = "#293650";
    private static final String AXIS_TICK_COLOR = "#475569";
    private static final String AXIS_LABEL_COLOR = "#94A3B8";

    public TimelineController(Pane paneTimeline, Label lblPlaceholder, HBox legendContainer) {
        this.paneTimeline = paneTimeline;
        this.lblPlaceholder = lblPlaceholder;
        this.legendContainer = legendContainer;
    }

    public void clear() {
        paneTimeline.getChildren().clear();
        paneTimeline.setVisible(false);
        lblPlaceholder.setVisible(true);
        legendContainer.getChildren().clear();
    }

    public void drawTimeline(List<TimelineSegment> segments, int totalTime) {
        if (segments == null || segments.isEmpty() || totalTime <= 0) {
            clear();
            return;
        }

        paneTimeline.getChildren().clear();
        paneTimeline.setVisible(true);
        lblPlaceholder.setVisible(false);
        legendContainer.getChildren().clear();

        // Assign colors to unique processes
        Map<String, String> colorMap = new HashMap<>();
        Set<String> uniqueProcesses = new LinkedHashSet<>();
        boolean hasContextSwitch = false;

        int colorIdx = 0;
        for (TimelineSegment seg : segments) {
            if (seg.isContextSwitch()) {
                hasContextSwitch = true;
            } else {
                String pid = seg.getProcessId();
                if (!colorMap.containsKey(pid)) {
                    colorMap.put(pid, PROCESS_COLORS[colorIdx % PROCESS_COLORS.length]);
                    colorIdx++;
                }
                uniqueProcesses.add(pid);
            }
        }

        // Layout constants
        double leftMargin = 30.0;
        double blockY = 20.0;
        double blockHeight = 52.0;
        double axisY = blockY + blockHeight;

        // Dynamic scale
        double availableWidth = 720.0;
        double pixelsPerUnit = Math.max(38.0, availableWidth / totalTime);
        double totalWidth = leftMargin + (totalTime * pixelsPerUnit) + 40.0;
        double totalHeight = 125.0;

        paneTimeline.setPrefWidth(totalWidth);
        paneTimeline.setMinWidth(totalWidth);
        paneTimeline.setPrefHeight(totalHeight);

        // Draw segments
        for (TimelineSegment seg : segments) {
            int duration = seg.getEndTime() - seg.getStartTime();
            double startX = leftMargin + (seg.getStartTime() * pixelsPerUnit);
            double width = duration * pixelsPerUnit;

            StackPane block = new StackPane();
            block.setLayoutX(startX);
            block.setLayoutY(blockY);
            block.setPrefWidth(width);
            block.setPrefHeight(blockHeight);
            block.setCursor(Cursor.HAND);

            Rectangle rect = new Rectangle(width, blockHeight);
            rect.setArcWidth(6.0);
            rect.setArcHeight(6.0);

            Label label = new Label();
            label.setAlignment(Pos.CENTER);

            if (seg.isContextSwitch()) {
                rect.setFill(Color.web(CS_BG_COLOR));
                rect.setStroke(Color.web(CS_BORDER_COLOR));
                rect.getStrokeDashArray().addAll(4.0, 4.0);
                rect.setStrokeWidth(1.2);

                label.setText("CS");
                label.setStyle("-fx-text-fill: " + CS_TEXT_COLOR + "; -fx-font-size: 11px; -fx-font-weight: bold;");

                Tooltip tooltip = new Tooltip(
                        "Context Switch\nStart: " + seg.getStartTime() + " | End: " + seg.getEndTime() + "\nDuration: "
                                + duration + " unit" + (duration > 1 ? "s" : ""));
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(block, tooltip);
            } else {
                String hex = colorMap.getOrDefault(seg.getProcessId(), "#4F8CFF");
                rect.setFill(Color.web(hex));
                rect.setStroke(Color.web(hex).darker());
                rect.setStrokeWidth(0.8);

                label.setText(seg.getProcessId());
                label.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px; -fx-font-weight: bold;");

                Tooltip tooltip = new Tooltip(
                        "Process: " + seg.getProcessId() + "\nStart: " + seg.getStartTime() + " | End: "
                                + seg.getEndTime() + "\nDuration: " + duration + " unit" + (duration > 1 ? "s" : ""));
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(block, tooltip);
            }

            block.getChildren().addAll(rect, label);
            paneTimeline.getChildren().add(block);
        }

        // Draw horizontal axis line
        Line axisLine = new Line(leftMargin, axisY, leftMargin + (totalTime * pixelsPerUnit), axisY);
        axisLine.setStroke(Color.web(AXIS_LINE_COLOR));
        axisLine.setStrokeWidth(1.5);
        paneTimeline.getChildren().add(axisLine);

        // Draw ticks and numbers
        for (int t = 0; t <= totalTime; t++) {
            double tickX = leftMargin + (t * pixelsPerUnit);

            Line tick = new Line(tickX, axisY, tickX, axisY + 6.0);
            tick.setStroke(Color.web(AXIS_TICK_COLOR));
            tick.setStrokeWidth(1.0);
            paneTimeline.getChildren().add(tick);

            Label lblTime = new Label(String.valueOf(t));
            lblTime.setStyle("-fx-font-size: 11px; -fx-font-weight: 500; -fx-text-fill: " + AXIS_LABEL_COLOR + ";");
            lblTime.setLayoutX(tickX - 6.0);
            lblTime.setLayoutY(axisY + 8.0);
            paneTimeline.getChildren().add(lblTime);
        }

        // Populate Legend
        for (String pid : uniqueProcesses) {
            String hex = colorMap.get(pid);

            HBox item = new HBox(6.0);
            item.setAlignment(Pos.CENTER_LEFT);

            Rectangle badge = new Rectangle(14.0, 14.0);
            badge.setFill(Color.web(hex));
            badge.setArcWidth(3.0);
            badge.setArcHeight(3.0);

            Label lbl = new Label(pid);
            lbl.getStyleClass().add("legend-label");

            item.getChildren().addAll(badge, lbl);
            legendContainer.getChildren().add(item);
        }

        if (hasContextSwitch) {
            HBox csItem = new HBox(6.0);
            csItem.setAlignment(Pos.CENTER_LEFT);

            Rectangle csBadge = new Rectangle(14.0, 14.0);
            csBadge.setFill(Color.web(CS_BG_COLOR));
            csBadge.setStroke(Color.web(CS_BORDER_COLOR));
            csBadge.getStrokeDashArray().addAll(2.0, 2.0);
            csBadge.setArcWidth(3.0);
            csBadge.setArcHeight(3.0);

            Label csLbl = new Label("Context switch");
            csLbl.getStyleClass().add("legend-label");

            csItem.getChildren().addAll(csBadge, csLbl);
            legendContainer.getChildren().add(csItem);
        }
    }
}
