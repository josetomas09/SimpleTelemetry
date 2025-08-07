package org.josegrangetto.controllers;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.RadarChartMode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


import java.net.URL;
import java.util.ResourceBundle;


public class ViewController implements Initializable {

    //  Temperatura del sensor
    @FXML
    private StackPane barGaugeTile; // 1
    //  Medir cuanto tiempo paso
    @FXML
    private StackPane countdownTile; // 2
    //
    @FXML
    private StackPane gaugeSparkLineTile; // 3
    //  Gráfico de fuerzas G en tiempo real
    @FXML
    private StackPane RadarCharSector; // 4
    //  Mostrar (aX, aY) y Roll, Pitch
    @FXML
    private StackPane smoothedChartTile; // 5
    //  Análisis de cuánto y cuándo se curva el auto
    @FXML
    private StackPane cycleStepTile; // 6 (aX)


    @FXML
    private ComboBox<String> portComboBox;

    private final CommController comm = new CommController();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        portComboBox.getItems().addAll(comm.getAvailablePorts());
        //portComboBox.setItems(FXCollections.observableArrayList(comm.getAvailablePorts()));




        // Temperature Tile
        Tile tempGaugeTile = TileBuilder.create()
                .skinType(Tile.SkinType.BAR_GAUGE)
                .minValue(0)
                .maxValue(50)
                .startFromZero(true)
                .threshold(30)
                .thresholdVisible(true)
                .title("Temperatura")
                .unit("C")
                .text("Temperatura description")
                .animated(true)
                .build();
        barGaugeTile.getChildren().add(tempGaugeTile);

        // G Force Tile
        Tile radarChartTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.RADAR_CHART)
                .radarChartMode(RadarChartMode.SECTOR)
                .title("Fuerzas G")
                .unit("G")
                .maxValue(2)
                .value(60)
                .prefSize(300, 300)
                .build();
        RadarCharSector.getChildren().add(radarChartTile1);

        // Timeline Tile
        Tile countdownTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.COUNTDOWN_TIMER)
                .title("CountDownTimer")
                .barColor(Color.AQUA)
                .build();
        countdownTile.getChildren().add(countdownTile1);

        Tile gaugeSparkLine1 = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE_SPARK_LINE)
                .title("gaugeSparkLine Tile")
                .animated(true)
                .textVisible(false)
                .averagingPeriod(25)
                .autoReferenceValue(true)
                .barColor(Tile.YELLOW_ORANGE)
                .build();

        gaugeSparkLineTile.getChildren().add(gaugeSparkLine1);

        //  Show (aX, aY) y Roll, Pitch Tile
        Tile smoothedChartTile1 = TileBuilder.create()
                .prefWidth(1640)
                .prefHeight(300)
                .skinType(Tile.SkinType.SMOOTHED_CHART)
                .title("SmoothedChart Tile")
                .chartType(Tile.ChartType.AREA)
                .animated(true)
                .smoothing(true)
                .tooltipTimeout(1000)
                .build();
        smoothedChartTile.getChildren().add(smoothedChartTile1);

        // Car analysis Tile
        Tile cycleStepTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.CYCLE_STEP)
                .title("cycleStep Tile")
                .textVisible(false)
                .animated(true)
                .decimals(2)
                .build();
        cycleStepTile.getChildren().add(cycleStepTile1);

    }

}
