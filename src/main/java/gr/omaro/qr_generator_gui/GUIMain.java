package gr.omaro.qr_generator_gui;

import gr.omaro.qr_generator_gui.ui.QRAppUI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the QR Code Generator JavaFX GUI.
 */
public class GUIMain extends Application {

    /**
     * Starts the JavaFX application and launches the UI.
     *
     * @param stage the main window stage.
     */
    @Override
    public void start(Stage stage) {
        new QRAppUI().start(stage);
    }

    /**
     * Main method to launch the JavaFX app.
     *
     * @param args CLI arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
