package org.josegrangetto.application;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.chart.RadarChartMode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static final double TILE_WIDTH  = 400;
    private static final double TILE_HEIGHT = 400;

    private Tile gForceTile;
    private ChartData frontData, leftData, rearData, rightData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        frontData = new ChartData("Front", 0);
        leftData  = new ChartData("Left", 0);
        rearData  = new ChartData("Rear", 0);
        rightData = new ChartData("Right", 0);

        gForceTile = TileBuilder.create()
                .skinType(Tile.SkinType.RADAR_CHART)
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .minValue(0)
                .maxValue(2)
                .title("Fuerzas G")
                .unit("G")
                .radarChartMode(RadarChartMode.SECTOR)
                .gradientStops(
                        new Stop(0.0, Color.web("#000000")),
                        new Stop(0.5, Color.web("#ef6050")),
                        new Stop(1.0, Color.web("#ef0000"))
                )
                .chartData(frontData, leftData, rearData, rightData)
                .animated(true)
                .build();

        StackPane root = new StackPane(gForceTile);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, TILE_WIDTH, TILE_HEIGHT);

        stage.setTitle("G-Force Radar Chart Demo");
        stage.setScene(scene);
        stage.show();

        simulateGForceData();
    }


    private void simulateGForceData() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            double gX = Math.random() * 2;
            double gY = Math.random() * 2;

            frontData.setValue(0);
            leftData.setValue(0);
            rearData.setValue(0);
            rightData.setValue(0);

            if (gY >= gX && gY >= (2 - gX)) {
                frontData.setValue(gY);
            } else if (gX >= gY && gX >= (2 - gY)) {
                rightData.setValue(gX);
            } else if ((2 - gY) >= gX && (2 - gY) >= (2 - gX)) {
                rearData.setValue(2 - gY);
            } else {
                leftData.setValue(2 - gX);
            }

            double resultantG = Math.sqrt(gX * gX + gY * gY);
            gForceTile.setValue(resultantG);

        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
