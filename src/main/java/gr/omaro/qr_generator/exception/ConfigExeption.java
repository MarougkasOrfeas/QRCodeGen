package gr.omaro.qr_generator.exception;

import lombok.experimental.StandardException;

/**
 * Exception thrown when there is a problem with the application configuration.
 * <p>
 * This can happen if the config file is missing, unreadable, or contains invalid values.
 */
@StandardException
public class ConfigExeption extends Exception{
}
