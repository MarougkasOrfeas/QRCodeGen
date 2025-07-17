package gr.omaro.qr_generator_gui.enums;

import lombok.Getter;

import java.util.Locale;

/**
 * Enum representing supported file types for upload or validation.
 * <p>
 * Currently only supports PDF, but is extensible for future types.
 */
@Getter
public enum SupportedFileType {

    /**
     * PDF file type.
     */
    PDF("pdf", "application/pdf");

    private final String extension;
    private final String mimeType;

    /**
     * Constructs a SupportedFileType with given extension and MIME type.
     *
     * @param extension file extension (e.g., "pdf")
     * @param mimeType  MIME type (e.g., "application/pdf")
     */
    SupportedFileType(String extension, String mimeType) {
        this.extension = extension.toLowerCase(Locale.ROOT);
        this.mimeType = mimeType.toLowerCase(Locale.ROOT);
    }

    /**
     * Finds a file type from a full file name.
     *
     * @param fileName the file name to check (e.g., "document.pdf")
     * @return the matching SupportedFileType or null if not supported
     */
    public static SupportedFileType fromFileName(String fileName) {
        if (fileName == null) return null;
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        for (SupportedFileType type : values()) {
            if (type.getExtension().equals(ext)) return type;
        }
        return null;
    }

    /**
     * Finds a file type from just the extension.
     *
     * @param ext the file extension (e.g., "pdf")
     * @return the matching SupportedFileType or null if not supported
     */
    public static SupportedFileType fromExtension(String ext) {
        for (SupportedFileType type : values()) {
            if (type.extension.equalsIgnoreCase(ext)) return type;
        }
        return null;
    }
}
