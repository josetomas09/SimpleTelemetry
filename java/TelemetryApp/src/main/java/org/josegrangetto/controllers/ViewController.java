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
import org.josegrangetto.model.Telemetry;
import org.josegrangetto.services.SupportedBaudRate;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @FXML
    private Label labelDateTime;


    private Tile tempGaugeTile;
    private Tile gaugeSparkLine1;
    private Tile radarChartTile1;
    //private RadarChartData forward, backward, right, left;



    private final CommController comm = new CommController();
    private volatile boolean stop = false;

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

        TimeNow();

        // #################### END - NAV BAR ####################

        // Temperature Tile
        tempGaugeTile = TileBuilder.create()
                .skinType(Tile.SkinType.BAR_GAUGE)
                .minValue(0)
                .maxValue(50)
                .startFromZero(true)
                .threshold(30)
                .thresholdVisible(true)
                .title("Temperatura")
                .unit("°C")
                .text("Temperatura del sensor")
                .animated(true)
                .build();
        barGaugeTile.getChildren().add(tempGaugeTile);


        readTemperatureData();

        /*
        // G Force Tile
        Tile radarChartTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.RADAR_CHART)
                .radarChartMode(RadarChartMode.SECTOR)
                .title("Fuerzas G")
                .unit("G")
                .maxValue(2)
                .prefSize(300, 300)
                .build();

        RadarChartData forward = new RadarChartData("Forward", 0);
        RadarChartData backward = new RadarChartData("Backward", 0);
        RadarChartData right = new RadarChartData("Right", 0);
        RadarChartData left = new RadarChartData("Left", 0);

        radarChartTile1.addRadarChartData(forward);
        radarChartTile1.addRadarChartData(backward);
        radarChartTile1.addRadarChartData(right);
        radarChartTile1.addRadarChartData(left);


        RadarCharSector.getChildren().add(radarChartTile1);

         */
        // Timeline Tile
        Tile countdownTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.COUNTDOWN_TIMER)
                .title("CountDownTimer")
                .barColor(Color.AQUA)
                .build();
        countdownTile.getChildren().add(countdownTile1);

        // medir aceleracion en eje y
        gaugeSparkLine1 = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE_SPARK_LINE)
                .title("Aceleración Longitudinal (eje Y)")
                .animated(true)
                .textVisible(false)
                .averagingPeriod(25)
                .autoReferenceValue(true)
                .barColor(Tile.YELLOW_ORANGE)
                .build();

        gaugeSparkLineTile.getChildren().add(gaugeSparkLine1);
        readAccelerationData();

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
                stop = true;
                System.out.println("Cerrando puerto serial...");
                comm.closePort();
            });
        });
        // ######################################################


    }

    //#################### Get DateTime #######################
    private void TimeNow(){
        Thread t = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy | hh:mm:ss");
            while (!stop) {
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
                final String timenow = sdf.format(new Date());
                Platform.runLater(() -> {
                    labelDateTime.setText(timenow);
                });
            }
        });
        t.start();
    }
    // ######################################################


    private void readTemperatureData() {
        Thread t = new Thread(() -> {
            while (!stop) {
                try {
                    synchronized (comm) {
                        Telemetry telemetry = comm.data;
                        if (telemetry != null) {
                            float temp = telemetry.temp;
                            Platform.runLater(() -> tempGaugeTile.setValue(temp));
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }


    private void readAccelerationData() {
        Thread t = new Thread(() -> {
            while (!stop) {
                try {
                    synchronized (comm) {
                        Telemetry telemetry = comm.data;
                        if (telemetry != null) {
                            float ay = telemetry.aY; // eje Y → aceleración longitudinal
                            Platform.runLater(() -> gaugeSparkLine1.setValue(ay));
                        }
                    }
                    Thread.sleep(100); // cada 100 ms (más rápido que la temperatura)
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /*
    // G forces displays and orientation
    private void updateGForces(float ax, float ay) {
        Platform.runLater(() -> {
            forward.setValue(ax > 0 ? ax : 0);
            backward.setValue(ax < 0 ? -ax : 0);
            right.setValue(ay > 0 ? ay : 0);
            left.setValue(ay < 0 ? -ay : 0);
        });
    }

     */



}
