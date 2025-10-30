package org.josegrangetto.controllers;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.RadarChartMode;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import org.josegrangetto.model.Telemetry;
import org.josegrangetto.services.SupportedBaudRate;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;


public class ViewController implements Initializable {


    //  Temperatura del sensor ✅
    @FXML
    private StackPane barGaugeTile; // 1
    //  Medir cuanto tiempo paso ✅
    @FXML
    private StackPane countdownTile; // 2
    // Aceleracion en eje y ✅
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
    private ChartData[] gSectors; // Array para 12 sectores


    private final CommController comm = new CommController();
    private volatile boolean stop = false;
    private volatile int maxTemp;

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
        readTemperatureData();

        tempGaugeTile = TileBuilder.create()
                .skinType(Tile.SkinType.BAR_GAUGE)
                .minValue(0)
                .maxValue(50)
                .startFromZero(true)
                .threshold(maxTemp)
                .thresholdVisible(true)
                .title("Temperatura")
                .unit("°C")
                .text("Temperatura del sensor")
                .animated(true)
                .build();
        barGaugeTile.getChildren().add(tempGaugeTile);


        // G Force Tile
        readGForceData();

        gSectors = new ChartData[12];
        for (int i = 0; i < 12; i++) {
            gSectors[i] = new ChartData(String.format("%d°", i * 30), 0);
        }

        Tile radarChartTile1 = TileBuilder.create()
                .skinType(Tile.SkinType.RADAR_CHART)
                .radarChartMode(RadarChartMode.SECTOR)
                .title("Fuerzas G Angulares")
                .unit("G")
                .minValue(0)
                .maxValue(2.0) // Máximo de 2 Gs
                .prefSize(300, 300)
                .chartData(gSectors) // Pasar el array de 12 ChartData
                .animated(true)
                .build();

        RadarCharSector.getChildren().add(radarChartTile1);

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
                .unit("g")
                .minValue(-2.0)
                .maxValue(2.0)
                .textVisible(false)
                .averagingPeriod(25)
                .autoReferenceValue(true)
                .barColor(Tile.YELLOW_ORANGE)
                .sectionsVisible(true)
                .highlightSections(true)
                .strokeWithGradient(true)
                .fixedYScale(true)
                // --- DEFINICIÓN DE SECCIONES ---
                .sections(
                        new eu.hansolo.tilesfx.Section(-2.0, -1.5, Tile.LIGHT_RED),
                        new eu.hansolo.tilesfx.Section(-1.5, -0.5, Tile.YELLOW),
                        new eu.hansolo.tilesfx.Section(-0.5, 0.5, Tile.LIGHT_GREEN),
                        new eu.hansolo.tilesfx.Section(0.5, 1.5, Tile.YELLOW),
                        new eu.hansolo.tilesfx.Section(1.5, 2.0, Tile.LIGHT_RED)
                )
                // --- DEFINICIÓN DE GRADIENTES PARA LA LÍNEA ---
                .gradientStops(
                        new Stop(0.0, Tile.LIGHT_RED),
                        new Stop(0.125, Tile.LIGHT_RED),
                        new Stop(0.125, Tile.YELLOW),
                        new Stop(0.375, Tile.YELLOW),
                        new Stop(0.375, Tile.LIGHT_GREEN),
                        new Stop(0.625, Tile.LIGHT_GREEN),
                        new Stop(0.625, Tile.YELLOW),
                        new Stop(0.875, Tile.YELLOW),
                        new Stop(0.875, Tile.LIGHT_RED),
                        new Stop(1.0, Tile.LIGHT_RED)
                )
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
    private void TimeNow() {
        Thread t = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy | hh:mm:ss");
            while (!stop) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
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
                            int currentTemp = (int) telemetry.temp;

                            if (currentTemp > maxTemp) {
                                maxTemp = currentTemp;

                                Platform.runLater(() -> {
                                    tempGaugeTile.setThreshold(maxTemp);
                                });
                            }
                            Platform.runLater(() -> tempGaugeTile.setValue(currentTemp));
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
                        Platform.runLater(() -> gaugeSparkLine1.setValue(telemetry.aZ)); // a telemetry.aY
                    }
                    Thread.sleep(100); // cada 100 ms
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateGForcesAngular(float ax, float ay) {
        Platform.runLater(() -> {
            // 1. Calcular Magnitud (R) y Ángulo (Theta)
            double magnitude = Math.sqrt(ax * ax + ay * ay);

            // Calcular ángulo en radianes y convertir a grados (-180 a 180)
            double angleRad = Math.atan2(ay, ax);
            double angleDeg = Math.toDegrees(angleRad);

            // Normalizar el ángulo a un rango de 0 a 360 grados (necesario para la indexación)
            // [Convención: 0° suele ser hacia adelante (X+), 90° derecha (Y+)]
            if (angleDeg < 0) {
                angleDeg += 360;
            }

            // 2. Determinar el Índice del Sector (0 a 11)
            // Cada sector cubre 30 grados. El índice se calcula como floor(ángulo / 30).
            int sectorIndex = (int) Math.floor(angleDeg / 30.0);

            // Asegurar que el índice esté dentro del rango [4]
            sectorIndex = sectorIndex % 12;

            // 3. Actualizar Solo el Sector Activo

            for (int i = 0; i < 12; i++) {
                if (i == sectorIndex) {
                    // Si la magnitud supera el máximo (2.0), limitarla a 2.0 para el gráfico
                    gSectors[i].setValue(Math.min(magnitude, 2.0));
                } else {
                    // Desactivar todos los demás sectores
                    gSectors[i].setValue(0.0);
                }
            }
        });
    }

    private void readGForceData() {
        Thread t = new Thread(() -> {
            while (!stop) {
                try {
                    synchronized (comm) {
                        Telemetry telemetry = comm.data;
                        if (telemetry != null) {
                            float ax = telemetry.aX;
                            float ay = telemetry.aY;

                            // Llamar al nuevo método angular en el FXAT
                            updateGForcesAngular(ax, ay);
                        }
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }


}
