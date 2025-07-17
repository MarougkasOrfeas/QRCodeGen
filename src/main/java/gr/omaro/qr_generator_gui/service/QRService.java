package gr.omaro.qr_generator_gui.service;

import gr.omaro.qr_generator.exception.ConfigExeption;
import gr.omaro.qr_generator.util.ConfigUtil;
import gr.omaro.qr_generator_gui.enums.SupportedFileType;
import gr.omaro.qr_generator_gui.exception.DriveUploadException;

import java.io.*;

/**
 * GUI-side QR service responsible for file validation and upload.
 */
public class QRService {

    /**
     * Uploader instance used to send files to Google Drive.
     */
    private final FileUploader uploader;

    /**
     * Initializes the QRService with a {@link GoogleDriveService}.
     *
     * @throws DriveUploadException if the service fails to initialize.
     */
    public QRService() throws DriveUploadException {
        this.uploader = new GoogleDriveService();
    }

    /**
     * Uploads a validated PDF file to Google Drive.
     *
     * @param pdfFile the PDF file to upload.
     * @return URL of the uploaded file.
     * @throws DriveUploadException if upload fails or file is invalid.
     */
    public String uploadPDF(File pdfFile) throws DriveUploadException {
        validateFileFromConfig(pdfFile);
        return uploader.uploadPDF(pdfFile);
    }

    /**
     * Validates file type and size based on configuration.
     *
     * @param file the file to validate.
     * @throws DriveUploadException if the file is not valid.
     */
    private void validateFileFromConfig(File file) throws DriveUploadException {
        if (file == null || !file.exists()) {
            throw new DriveUploadException("The file does not exist.");
        }

        try {
            // Check file extension
            SupportedFileType actualType = SupportedFileType.fromFileName(file.getName());
            if (actualType == null || !ConfigUtil.getAcceptedFileTypes().contains(actualType)) {
                throw new DriveUploadException("File type is not accepted.");
            }
            // Check file size
            long maxSize = ConfigUtil.getMaxUploadFileSizeBytes();
            if (file.length() > maxSize) {
                throw new DriveUploadException("File exceeds max size (" + maxSize + " bytes).");
            }

        } catch (ConfigExeption e) {
            throw new DriveUploadException("Failed to load validation config.", e);
        }
    }
}
