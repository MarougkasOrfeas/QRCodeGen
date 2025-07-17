package gr.omaro.qr_generator_gui.ui;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.exception.QRException;
import gr.omaro.qr_generator_gui.exception.DriveUploadException;
import gr.omaro.qr_generator_gui.service.QRService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

/**
 * Main UI class for the QR Code Generator desktop application.
 * <p>
 * Allows users to generate QR codes or upload PDF files to Google Drive.
 */
public class QRAppUI {

    /**
     * Input field for URL or text to encode.
     */
    private TextField inputField;
    /**
     * Input field for QR code width.
     */
    private TextField widthField;
    /**
     * Input field for QR code height.
     */
    private TextField heightField;
    /**
     * Label to display status messages to the user.
     */
    private Label statusLabel;

    /**
     * Default dimension value used if none is provided.
     */
    private static final int DEFAULT_VALUE = 300;

    /**
     * Initializes and displays the JavaFX UI.
     *
     * @param stage the primary window.
     */
    public void start(Stage stage) {
        StackPane  root = createRootLayout();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        stage.setTitle("QR Code Generator");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Builds the main UI layout.
     *
     * @return the root pane.
     */
    private StackPane createRootLayout() {
        Label header = createHeader();

        inputField = createTextField("Enter URL or text");
        inputField.setPrefWidth(400);

        widthField = createDimensionField("Width");
        widthField.setPrefWidth(80);

        heightField = createDimensionField("Height");
        heightField.setPrefWidth(80);

        statusLabel = createStatusLabel();
        Button generateButton = createGenerateButton();
        Button uploadButton = createUploadButton();

        HBox inputRow = new HBox(10, inputField, widthField, heightField, generateButton, uploadButton);
        inputRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, header, inputRow, statusLabel);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(content);
        root.getStyleClass().add("root");
        return root;
    }

    /**
     * Creates the main header label for the UI.
     *
     * @return a styled {@link Label} with title text.
     */
    private Label createHeader() {
        Label header = new Label("QR Code Generator");
        header.getStyleClass().add("header");
        return header;
    }

    /**
     * Creates a general-purpose text input field.
     *
     * @param prompt the placeholder text to show.
     * @return a styled {@link TextField}.
     */
    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("url-field");
        return field;
    }

    /**
     * Creates a text input field for dimension (width or height).
     *
     * @param prompt the placeholder text (e.g. "Width", "Height").
     * @return a styled {@link TextField} for numeric input.
     */
    private TextField createDimensionField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("dimension-field");
        return field;
    }

    /**
     * Creates the label used to show status or error messages to the user.
     *
     * @return a styled {@link Label}.
     */
    private Label createStatusLabel() {
        Label label = new Label();
        label.getStyleClass().add("status");
        return label;
    }

    /**
     * Creates the "Generate" button and binds its action to QR code generation.
     *
     * @return a styled {@link Button}.
     */
    private Button createGenerateButton() {
        Button button = new Button("Generate");
        button.getStyleClass().add("button");
        button.setOnAction(event -> handleGenerateAction());
        return button;
    }

    /**
     * Creates an Upload button and binds it to file chooser and upload logic.
     *
     * @return the configured upload button.
     */
    private Button createUploadButton() {
        Button uploadButton = new Button("Upload");
        uploadButton.getStyleClass().add("button");

        QRService qrService = createQrServiceSafely();
        if (qrService == null) return uploadButton;

        uploadButton.setOnAction(event -> {
            Stage stage = (Stage) inputField.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select PDF to Upload");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            handleUploadAction(stage, selectedFile, qrService);
        });

        return uploadButton;
    }

    /**
     * Handles the file upload process with visual overlay and background task.
     */
    private void handleUploadAction(Stage stage, File selectedFile, QRService qrService) {
        if (selectedFile == null) return;

        StackPane root = (StackPane) stage.getScene().getRoot();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        overlay.setPickOnBounds(true);
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(60, 60);
        overlay.getChildren().add(spinner);
        StackPane.setAlignment(spinner, Pos.CENTER);

        Platform.runLater(() -> root.getChildren().add(overlay));

        // Background upload task
        Task<Void> uploadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    String uploadedUrl = qrService.uploadPDF(selectedFile);
                    Platform.runLater(() -> {
                        root.getChildren().remove(overlay);
                        showToast(stage, "Uploaded successfully:\n" + uploadedUrl);
                    });
                } catch (DriveUploadException e) {
                    Platform.runLater(() -> {
                        root.getChildren().remove(overlay);
                        showToast(stage, "Upload failed: " + e.getMessage());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        root.getChildren().remove(overlay);
                        showToast(stage, "An error has occurred: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        new Thread(uploadTask).start();
    }

    /**
     * Initializes QRService with error handling.
     *
     * @return an instance or null if failed.
     */
    private QRService createQrServiceSafely() {
        try {
            return new QRService();
        } catch (Exception e) {
            showToast(null, "Failed to initialize QRService: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handles the generation of a QR code based on user input.
     */
    private void handleGenerateAction() {
        String input = inputField.getText();
        if (input == null || input.isBlank()) {
            statusLabel.setText("Input is required.");
            return;
        }

        try {
            int width = parseDimension(widthField.getText(), DEFAULT_VALUE);
            int height = parseDimension(heightField.getText(), DEFAULT_VALUE);

            gr.omaro.qr_generator.service.QRService qrService = new gr.omaro.qr_generator.service.QRService();
            String filePath = qrService.generate(input, width, height, null);
            showToast((Stage) inputField.getScene().getWindow(), "QR Code saved at:\n" + filePath);
            clearUserInputValues();
        } catch (NumberFormatException e) {
            statusLabel.setText("Width/Height must be valid numbers.");
        } catch (ConfigExeption | QRException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Parses a string dimension to an integer, or returns the default if blank/null.
     *
     * @param text the text to parse
     * @param defaultValue fallback value if text is blank
     * @return parsed integer or default
     */
    private int parseDimension(String text, int defaultValue) {
        if (text == null || text.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(text);
    }

    /**
     * Shows a temporary toast message in the corner of the app.
     *
     * @param stage   the current window stage.
     * @param message the message to show.
     */
    private void showToast(Stage stage, String message) {
        Label toastLabel = new Label(message);
        toastLabel.getStyleClass().add("toast-label");
        toastLabel.setFont(Font.font(14));

        StackPane toastContainer = new StackPane(toastLabel);
        if (stage == null || stage.getScene() == null) return;
        toastContainer.getStyleClass().add("toast-container");
        toastContainer.setOpacity(0);
        toastContainer.setMouseTransparent(true);

        StackPane.setAlignment(toastLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toastLabel, new Insets(0, 20, 20, 0));

        StackPane root = (StackPane) stage.getScene().getRoot();
        root.getChildren().add(toastContainer);

        FadeTransition fadeIn = getFadeTransition(toastContainer, root);
        fadeIn.play();
    }

    /**
     * Creates a fade-in and fade-out animation for toast messages.
     *
     * @param toastContainer the toast message container
     * @param root the root UI pane
     * @return the fade transition animation
     */
    private static FadeTransition getFadeTransition(StackPane toastContainer, StackPane root) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> root.getChildren().remove(toastContainer));
                fadeOut.play();
            });
            pause.play();
        });
        return fadeIn;
    }

    /**
     * Clears user input fields (input, width, height).
     */
    private void clearUserInputValues(){
        inputField.clear();
        widthField.clear();
        heightField.clear();
    }
}
