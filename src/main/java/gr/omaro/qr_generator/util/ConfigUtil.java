package gr.omaro.qr_generator.util;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator_gui.enums.SupportedFileType;
import lombok.experimental.UtilityClass;
import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Utility class to load and read configuration values from a properties file.
 * <p>
 * This class loads settings for the application such as output path,
 * Google Drive config, accepted file types, and more.
 */
@UtilityClass
public class ConfigUtil {

    /**
     * Properties object that holds all loaded config values.
     */
    private static final Properties properties = new Properties();

    /**
     * Initializes configuration by trying to load from external path or internal resource.
     *
     * @throws ConfigExeption if config cannot be loaded.
     */
    public static void initialize() throws ConfigExeption {
        // Try to get the config file path from system property or environment variable
        String externalPath = System.getProperty("QR_CONFIG_PATH", System.getenv("QR_CONFIG_PATH"));

        try (InputStream input = getInputStream(externalPath)) {
            if (input == null) {
                throw new ConfigExeption("Unable to load config from external or internal source.");
            }
            // Load all properties from the config file
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigExeption("Failed to load config.properties", e);
        }
    }

    /**
     * Returns an input stream for the config file.
     * Tries external path first, then falls back to internal resource.
     *
     * @param externalPath the external file path to check.
     * @return InputStream of the config file.
     * @throws FileNotFoundException if external file is not found.
     */
    private static InputStream getInputStream(String externalPath) throws FileNotFoundException {
        if (externalPath != null && !externalPath.isBlank()) {
            File externalFile = new File(externalPath);
            if (externalFile.exists() && externalFile.isFile()) {
                return new FileInputStream(externalFile);
            }
        }
        // Load internal config file (from resources)
        return ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties");
    }

    /**
     * Gets the output directory path where files will be saved.
     *
     * @return output path as String.
     * @throws ConfigExeption if the value is missing or empty.
     */
    public static String getOutputPath() throws ConfigExeption{
        String path = properties.getProperty("output.path");
        if (path == null || path.isBlank()) {
            throw new ConfigExeption("Property 'output.path' is missing or empty in config.properties");
        }
        return path;
    }

    /**
     * Gets the folder ID used for uploading to Google Drive.
     *
     * @return Google Drive folder ID.
     * @throws ConfigExeption if the value is missing or empty.
     */
    public static String getGoogleDriveFolderId() throws ConfigExeption {
        String folderId = properties.getProperty("google.drive.folder.id");
        if (folderId == null || folderId.isBlank()) {
            throw new ConfigExeption("Property 'google.drive.folder.id' is missing or empty in config.properties");
        }
        return folderId;
    }

    /**
     * Gets the path to the Google client secret JSON file.
     *
     * @return path to client secret file.
     * @throws ConfigExeption if the value is missing or empty.
     */
    public static String getClientSecretPath() throws ConfigExeption {
        String path = properties.getProperty("google.drive.client.secret.path");
        if (path == null || path.isBlank()) {
            throw new ConfigExeption("Property 'google.drive.client.secret.path' is missing or empty in config.properties");
        }
        return path;
    }

    /**
     * Checks if UUID should be used as the file name.
     *
     * @return true if UUID file names are enabled, false otherwise.
     */
    public static boolean useUuidFileName() {
        return Boolean.parseBoolean(properties.getProperty("filename.use.uuid", "false"));
    }

    /**
     * Gets the maximum file size allowed for upload, in bytes.
     *
     * @return maximum upload file size.
     * @throws ConfigExeption if the value is not a valid number.
     */
    public static long getMaxUploadFileSizeBytes() throws ConfigExeption {
        String value = properties.getProperty("max.upload.file.size.bytes", "5242880");
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConfigExeption("Invalid number for 'max.upload.file.size.bytes'", e);
        }
    }

    /**
     * Gets the set of accepted file types from the config.
     *
     * @return Set of supported file types.
     * @throws ConfigExeption if any listed type is not supported.
     */
    public static Set<SupportedFileType> getAcceptedFileTypes() throws ConfigExeption {
        String types = properties.getProperty("accepted.file.types", "pdf");
        Set<SupportedFileType> accepted = new HashSet<>();

        for (String typeStr : types.split(",")) {
            SupportedFileType type = SupportedFileType.fromExtension(typeStr.trim().toLowerCase());
            if (type == null) {
                throw new ConfigExeption("Unsupported file type in config: " + typeStr);
            }
            accepted.add(type);
        }

        return accepted;
    }

}
