package gr.omaro.qr_generator_cli.service;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.exception.QRException;
import gr.omaro.qr_generator.service.QRService;
import gr.omaro.qr_generator_cli.commands.QROptions;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Picocli command implementation for generating QR codes.
 * <p>
 * Uses the provided options to create and save a QR code image.
 */
@Slf4j
@CommandLine.Command(
        name = "qrgen",
        version = "qrgen 1.0.0",
        mixinStandardHelpOptions = true,
        description = "Generates QR codes from URLs or text input."
)
public class GenerateCommand implements Callable<Integer> {

    /**
     * Injected CLI options like input, width, height, output directory.
     */
    @SuppressWarnings("unused")
    @CommandLine.Mixin
    private QROptions options;

    /**
     * Executes the QR generation logic.
     *
     * @return exit code: 0 = success, 1 = known error, 2 = unexpected error
     */
    @Override
    public Integer call() {
        try {
            if (options.getInput() == null || options.getInput().isBlank()) {
                log.error("Input cannot be empty.");
                return 1;
            }

            QRService qrService = new QRService();
            // Call service with width/height/output if provided
            String filePath = qrService.generate(
                    options.getInput(),
                    options.getWidth(),
                    options.getHeight(),
                    options.getOutputDir()
            );
            log.info("QR Code generated at: {}", filePath);
            return 0;
        } catch (ConfigExeption | QRException e) {
            log.error("Error: {}", e.getMessage());
            return 1;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return 2;
        }
    }
}
