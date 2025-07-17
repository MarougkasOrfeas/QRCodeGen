package gr.omaro.qr_generator_cli.commands;

import lombok.Getter;
import picocli.CommandLine;

/**
 * Command-line options for the QR code generator.
 * <p>
 * Uses Picocli to parse arguments like input text, width, height, and output directory.
 */
@SuppressWarnings({"FieldMayBeFinal","FieldCanBeLocal", "unused"})
@Getter
public class QROptions {

    /**
     * The input text or URL to encode into a QR code.
     */
    @CommandLine.Parameters(index = "0", description = "The URL or text to encode as QR code.")
    private String input;
    /**
     * Width of the QR code image in pixels.
     */
    @CommandLine.Option(names = {"-w", "--width"}, description = "QR code width in pixels. Default: ${DEFAULT-VALUE}")
    private int width = 300;
    /**
     * Height of the QR code image in pixels.
     */
    @CommandLine.Option(names = {"-h", "--height"}, description = "QR code height in pixels. Default: ${DEFAULT-VALUE}")
    private int height = 300;
    /**
     * Optional output directory to save the QR code image.
     */
    @CommandLine.Option(names = {"-o", "--output"}, description = "Override output directory.")
    private String outputDir;
}
