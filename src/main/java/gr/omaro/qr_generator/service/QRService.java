package gr.omaro.qr_generator.service;

import com.google.zxing.WriterException;
import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.exception.QRException;
import gr.omaro.qr_generator.util.ConfigUtil;
import gr.omaro.qr_generator.util.QRCodeGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service class for generating QR code images.
 * <p>
 * This class uses {@link ConfigUtil} for configuration and {@link QRCodeGenerator}
 * to generate QR code images.
 */
public class QRService {

    /**
     * Default width of the QR code image.
     */
    private static final int DEFAULT_WIDTH = 300;
    /**
     * Default height of the QR code image.
     */
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * Initializes the configuration and ensures the output directory exists.
     *
     * @return the configured output directory path.
     * @throws ConfigExeption if configuration fails or output directory can't be created.
     */
    private String checkConfigOutputPath() throws ConfigExeption {
        ConfigUtil.initialize();
        String outputDir = ConfigUtil.getOutputPath();

        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            throw new ConfigExeption("Failed to create output directory: " + outputDir, e);
        }

        return outputDir;
    }

    /**
     * Generates a QR code image with default size and config output path.
     * <p>
     * If the file name should use UUID (based on config), a random name will be used.
     *
     * @param input the text or URL to encode in the QR code.
     * @return the full path of the generated QR image file.
     * @throws QRException      if QR code generation fails.
     * @throws ConfigExeption   if configuration loading fails.
     */
    public String generate(String input) throws QRException, ConfigExeption {
        String outputDir = checkConfigOutputPath();

        if (input.startsWith("file:")) {
            throw new QRException("Local file paths are not supported. Please use a public URL.");
        }
        // if uuid is enabled on properties
        String fileName = ConfigUtil.useUuidFileName()
                ? UUID.randomUUID() + ".png"
                : sanitizeFileName(input);
        // Build full path for output file
        String fullPath = outputDir.endsWith(File.separator)
                ? outputDir + fileName
                : outputDir + File.separator + fileName;
        try {
            // Generate the QR code image
            QRCodeGenerator.generateQRCodeImage(input, DEFAULT_WIDTH, DEFAULT_HEIGHT, fullPath);
        } catch (IOException | WriterException e){
            throw new QRException("Error during generating QR Code.", e);
        }
        return fullPath;
    }

    /**
     * Generates a QR code image with custom dimensions and output path.
     *
     * @param input      the text or URL to encode.
     * @param width      desired width of the QR image.
     * @param height     desired height of the QR image.
     * @param outputPath optional output path; if null/blank, config path is used.
     * @return full path of the generated QR code image.
     * @throws QRException     if QR code generation fails.
     * @throws ConfigExeption  if configuration fails or output directory can't be created.
     */
    public String generate(String input, int width, int height, String outputPath)
            throws QRException, ConfigExeption {
        String outputDir = (outputPath != null && !outputPath.isBlank())
                ? outputPath
                : checkConfigOutputPath();

        if (input.startsWith("file:")) {
            throw new QRException("Local file paths are not supported. Please use a public URL.");
        }

        String fileName = sanitizeFileName(input);
        String fullPath = outputDir.endsWith(File.separator)
                ? outputDir + fileName
                : outputDir + File.separator + fileName;

        try {
            // Generate QR image with specified size
            QRCodeGenerator.generateQRCodeImage(input, width, height, fullPath);
        } catch (IOException | WriterException e) {
            throw new QRException("Error during generating QR Code.", e);
        }
        return fullPath;
    }

    /**
     * Converts input text into a safe file name by removing unsupported characters.
     *
     * @param input the input string to clean.
     * @return sanitized file name ending with .png.
     */
    private String sanitizeFileName(String input) {
        return input.replaceFirst("https?://", "")
                .replaceFirst("^www\\.", "").split("[./]")[0].replaceAll("[^a-zA-Z0-9_-]", "").concat(".png");
    }
}
