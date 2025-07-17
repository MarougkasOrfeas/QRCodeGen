package gr.omaro.qr_generator.exception;

import lombok.experimental.StandardException;

/**
 * Exception thrown when something goes wrong during QR code generation.
 * <p>
 * This includes invalid input, IO problems, or QR library errors.
 */
@StandardException
public class QRException extends Exception{
}
