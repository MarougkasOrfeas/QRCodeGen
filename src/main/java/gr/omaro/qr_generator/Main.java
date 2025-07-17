package gr.omaro.qr_generator;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.exception.QRException;
import gr.omaro.qr_generator.service.QRService;
import lombok.extern.slf4j.Slf4j;
import java.util.Scanner;

/**
 * Main class to run the QR Code generator from command line.
 * <p>
 * Prompts the user for input and generates a QR code image using {@link QRService}.
 */
@Slf4j
public class Main {

    /**
     * Scanner used to read user input from the console.
     */
    private static final Scanner in = new Scanner(System.in);

    /**
     * Main entry point for the QR code generator application.
     *
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            String input = promptForInput();
            // If input is empty or invalid, terminate
            if (input == null || input.isEmpty()){
                return;
            }
            QRService qrService = new QRService();
            String filePath = qrService.generate(input);
            log.info("QR Code generated successfully at: {}", filePath);
        } catch (ConfigExeption e){
            log.error("Configuration exception: {}", e.getMessage());
        } catch (QRException e) {
            log.error("QR Code exception: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
        }
    }

    /**
     * Prompts the user to enter a URL to encode into a QR code.
     *
     * @return the trimmed input string, or null if invalid or empty.
     */
    private static String promptForInput() {
        log.info("Enter URL to encode:");
        String input = in.nextLine().trim();

        if (input.isEmpty()) {
            log.error("Input cannot be empty.");
            return null;
        }

        // Basic validation: check if input starts with http or https
        if (!input.matches("^(https?://).*")) {
            log.warn("Input doesn't look like a valid URL (missing http/https). Proceeding anyway.");
            return null;
        }
        return input;
    }
}
