package gr.omaro.qr_generator_gui.service;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import gr.omaro.qr_generator.util.ConfigUtil;
import gr.omaro.qr_generator_gui.exception.DriveUploadException;
import lombok.extern.slf4j.Slf4j;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * Abstract base class for working with Google Drive API.
 * <p>
 * Handles authorization and setup of the Drive service.
 */
@Slf4j
public abstract class AbstractDriveService {
    /**
     * Directory used to store OAuth2 tokens locally.
     */
    protected static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * JSON parser used by the Google API.
     */
    protected static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /**
     * The initialized Drive service instance.
     */
    protected Drive driveService;

    /**
     * Initializes the Drive service on construction.
     *
     * @throws DriveUploadException if configuration or authorization fails.
     */
    protected AbstractDriveService() throws DriveUploadException {
        try {
            // Load config if not already loaded
            ConfigUtil.initialize();
            this.driveService = initializeDriveService();
        } catch (Exception e) {
            log.error("Failed to initialize Drive service", e);
            throw new DriveUploadException("Drive service initialization failed", e);
        }
    }

    /**
     * Initializes and authorizes the Drive service.
     *
     * @return an authorized Drive service instance.
     * @throws Exception if authorization or config loading fails.
     */
    private Drive initializeDriveService() throws Exception {
        log.debug("Loading client secret...");
        // Load client secret from configured path
        try (InputStream in = new FileInputStream(ConfigUtil.getClientSecretPath())) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singleton(DriveScopes.DRIVE_FILE)
            )
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            log.debug("Authorizing user via local server receiver...");
            return new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
            ).setApplicationName("QRCodeUploader").build();
        }
    }
}
