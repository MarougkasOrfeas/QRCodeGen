package gr.omaro.qr_generator_cli;

import gr.omaro.qr_generator_cli.service.GenerateCommand;
import picocli.CommandLine;

/**
 * CLI entry point for the QR generator tool.
 * <p>
 * Uses Picocli to run the {@link GenerateCommand}.
 */
public class CliMain {

    /**
     * Main method to start the CLI application.
     *
     * @param args command-line arguments passed from the terminal.
     */
    public static void main(String[] args) {
        // Executes the command and exits with appropriate status code
        int exitCode = new CommandLine(new GenerateCommand()).execute(args);
        System.exit(exitCode);
    }
}
