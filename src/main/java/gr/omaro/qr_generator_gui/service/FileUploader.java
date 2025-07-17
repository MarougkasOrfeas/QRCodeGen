package gr.omaro.qr_generator_gui.service;

import gr.omaro.qr_generator_gui.exception.DriveUploadException;

import java.io.File;

/**
 * Interface for uploading files to remote services.
 */
public interface FileUploader {

    /**
     * Uploads a PDF file to a remote service and returns a shareable link.
     *
     * @param file the PDF file to upload.
     * @return the shareable URL of the uploaded file.
     * @throws DriveUploadException if the upload fails.
     */
    String uploadPDF(File file) throws DriveUploadException;
}
