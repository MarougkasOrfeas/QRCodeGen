package gr.omaro.qr_generator.util;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Utility class for generating QR code images and saving them to disk.
 */
@UtilityClass
public class QRCodeGenerator {

    /**
     * Generates a QR code image from the given text and saves it to a file in PNG format.
     * <p>
     * This method checks if the input values are valid before generating the QR code.
     *
     * @param text     the content to encode in the QR code
     * @param width    width of the QR code image (must be > 0)
     * @param height   height of the QR code image (must be > 0)
     * @param filePath the file path where the QR code will be saved (must not be blank)
     * @throws WriterException if the QR code cannot be created
     * @throws IOException     if the file cannot be written
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        // Guard because is a public accessible method
        if(text != null && !text.isBlank() && width > 0 && height > 0 && filePath != null && !filePath.isBlank()){
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        }
    }
}
