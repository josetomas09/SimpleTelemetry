package org.josegrangetto.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/josegrangetto/views/mainView.fxml"));
        Parent root = loader.load();

        stage.setTitle("TelemetryApp");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
