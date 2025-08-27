package org.josegrangetto.controllers;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.RadarChartMode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.josegrangetto.services.SupportedBaudRate;


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


    // ######################################
    @FXML
    public Label labelConnection;

    @FXML
    private ComboBox<String> portComboBox;

    @FXML
    public Button calBtn;


    private final CommController comm = new CommController();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // ###################### NAV BAR ########################
        portComboBox.getItems().addAll(comm.getAvailablePorts());

        portComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldPort, newPort) -> {
            if (newPort != null) {
                boolean isOpen = comm.openPort(newPort, SupportedBaudRate.RATE_115200.getSpeed(), 8, 1, 0);
                if (isOpen) {
                    System.out.println("Puerto " + newPort + " abierto correctamente.");
                } else {
                    System.out.println("Error al abrir el puerto " + newPort);
                }
            }
        });

        calBtn.setOnAction(e -> {
            comm.sendData((byte) 0x06);
            System.out.println("Boton Calibrar presionado");
        });


        // #################### END - NAV BAR ####################

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



        //#################### Close port #######################
        Platform.runLater(() -> {
            Stage stage = (Stage) calBtn.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("Cerrando puerto serial...");
                comm.closePort();
            });
        });
        // ######################################################


    }

}
