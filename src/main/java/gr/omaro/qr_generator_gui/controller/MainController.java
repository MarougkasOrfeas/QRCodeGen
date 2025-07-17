package gr.omaro.qr_generator_gui.controller;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.exception.QRException;
import gr.omaro.qr_generator.service.QRService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * JavaFX controller for the QR Generator GUI.
 * <p>
 * Handles user input and triggers QR code generation using {@link QRService}.
 */
@SuppressWarnings("unused")
public class MainController {
    /**
     * Text field where the user enters the input text or URL.
     */
    @FXML private TextField inputField;
    /**
     * Text field to set the desired QR code width.
     */
    @FXML private TextField widthField;
    /**
     * Text field to set the desired QR code height.
     */
    @FXML private TextField heightField;
    /**
     * Label to display status messages (e.g., success or errors).
     */
    @FXML private Label statusLabel;

    /**
     * Called when the "Generate" button is clicked.
     * <p>
     * Validates input, handles errors, and generates the QR code.
     */
    @FXML
    private void onGenerate() {
        String input = inputField.getText();
        String widthText = widthField.getText();
        String heightText = heightField.getText();
        // Input validation
        if (input == null || input.isBlank()) {
            statusLabel.setText("Input is required.");
            return;
        }

        try {
            // Use default size (300x300) if no size provided
            int width = (widthText == null || widthText.isBlank()) ? 300 : Integer.parseInt(widthText);
            int height = (heightText == null || heightText.isBlank()) ? 300 : Integer.parseInt(heightText);

            QRService qrService = new QRService();
            String filePath = qrService.generate(input, width, height, null);
            // Show success message
            statusLabel.setText("QR Code saved at: " + filePath);
        } catch (NumberFormatException e) {
            statusLabel.setText("Width/Height must be valid numbers.");
        } catch (ConfigExeption | QRException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
