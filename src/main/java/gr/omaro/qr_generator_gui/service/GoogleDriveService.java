package gr.omaro.qr_generator_gui.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.util.ConfigUtil;
import gr.omaro.qr_generator_gui.enums.SupportedFileType;
import gr.omaro.qr_generator_gui.exception.DriveUploadException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Collections;
import java.util.Objects;

/**
 * Concrete implementation of {@link FileUploader} that uploads files to Google Drive.
 */
@Slf4j
public class GoogleDriveService extends AbstractDriveService implements FileUploader{

    /**
     * Constructs the service and initializes Google Drive API.
     *
     * @throws DriveUploadException if setup fails.
     */
    public GoogleDriveService() throws DriveUploadException {
        super();
    }

    /**
     * Uploads a PDF file to Google Drive and returns a viewable link.
     *
     * @param pdfFile the PDF file to upload.
     * @return public Google Drive link to the uploaded file.
     * @throws DriveUploadException if the upload fails.
     */
    @Override
    public String uploadPDF(java.io.File pdfFile) throws DriveUploadException {
        Objects.requireNonNull(pdfFile, "File cannot be null");
        log.info("Uploading file: {}", pdfFile.getName());

        try {
            // Set metadata like file name and target folder
            File fileMetadata = new File();
            fileMetadata.setName(pdfFile.getName());
            fileMetadata.setParents(Collections.singletonList(ConfigUtil.getGoogleDriveFolderId()));
            // Define content type and wrap file
            FileContent mediaContent = new FileContent(SupportedFileType.PDF.getMimeType(), pdfFile);
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            // Construct URL
            String fileUrl = String.format("https://drive.google.com/file/d/%s/view", uploadedFile.getId());
            log.info("Upload successful: {}", fileUrl);
            return fileUrl;
        } catch (IOException | ConfigExeption e) {
            log.error("Upload failed", e);
            throw new DriveUploadException("Failed to upload PDF to Google Drive", e);
        }
    }
}
