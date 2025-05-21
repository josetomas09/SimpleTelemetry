package org.josegrangetto.controllers;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.RadarChartMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    //  Temperatura del sensor
    @FXML
    private StackPane radialDistributionTile; // 1
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        Tile radialDistributionTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.RADIAL_DISTRIBUTION)
                .title("Temperatura")
                .text("Temperatura?")
                .minValue(0)
                .maxValue(200)
                .lowerThreshold(70)
                .threshold(140)
                .tickLabelDecimals(2)
                .decimals(2)
                .barColor(Color.GREEN)
                .build();

        radialDistributionTile.getChildren().add(radialDistributionTile1);

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

        Tile smoothedChartTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.SMOOTHED_CHART)
                .title("SmoothedChart Tile")
                .chartType(Tile.ChartType.AREA)
                .animated(true)
                .smoothing(true)
                .tooltipTimeout(1000)
                .build();

        smoothedChartTile.getChildren().add(smoothedChartTile1);

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
