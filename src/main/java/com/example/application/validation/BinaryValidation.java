package com.example.application.validation;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * Validation for binary data.
 * <p>
 * This class contains methods validating binary data.
 */
public class BinaryValidation {

    /**
     * Calculate the SHA-256 checksum of the given data.
     *
     * @param data Binary data to validate.
     * @return Checksum as hex String or null if failed.
     */
    public static String checksum(byte[] data) {
        if (data == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            // This is the same as any error
        }
        return null;
    }

    /**
     * Validate the checksum of the given data.
     *
     * @param data     Binary data to validate.
     * @param checksum Checksum to compare to.
     * @return ValidationResult.OK if checksum matches, ValidationResult.INVALID_SHA256_CHECKSUM otherwise
     */
    public static ValidationResult validateChecksum(byte[] data, String checksum) {
        String calculatedChecksum = checksum(data);
        if (calculatedChecksum != null) {
            return calculatedChecksum.equals(checksum) ? ValidationResult.OK : ValidationResult.INVALID_SHA256_CHECKSUM;
        }
        return ValidationResult.VALIDATION_ERROR;
    }

    /**
     * Check if the image is a PNG.
     * <p>
     * The first eight bytes of a PNG file contain the following Hex values:
     * <code>89 50 4e 47 0d 0a 1a 0a</code>
     * <a href="https://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG at Wikipedia</a>
     *
     * @param data Binary data to validate.
     * @return ValidationResult.VALUE_OK if String is valid PNG, ValidationResult.INVALID_PNG otherwise
     */
    public static ValidationResult isValidPNG(byte[] data) {
        try {
            if (!Arrays.equals(data, 0, 7, new BigInteger("89504e470d0a1a0a", 16).toByteArray(), 0, 7)) {
                return ValidationResult.INVALID_PNG;
            }
            return ValidationResult.OK;
        } catch (Exception e) {
            // Ignore: Treat the same as validation error
        }
        return ValidationResult.VALIDATION_ERROR;
    }

    /**
     * Check if the image is a JPEG.
     * <p>
     * JPEG image files begin with <code>FF D8</code> and end with <code>FF D9</code>.
     * <a href="https://en.wikipedia.org/wiki/JPEG_File_Interchange_Format">JPEG at Wikipedia</a>
     *
     * @param data Binary data to validate.
     * @return ValidationResult.VALUE_OK if String is valid JPEG, ValidationResult.INVALID_JPEG otherwise
     */
    public static ValidationResult isValidJPEG(byte[] data) {
        try {
            // Check the first 2 bytes:
            if (data == null || data.length < 2 || (data[0] & 0xff) != 0xff || (data[1] & 0xff) != 0xd8) {
                return ValidationResult.INVALID_JPEG;
            }
            // Check last 2 bytes:
            if ((data[data.length - 2] & 0xff) != 0xff || (data[data.length - 1] & 0xff) != 0xd9) {
                return ValidationResult.INVALID_JPEG;
            }
            return ValidationResult.OK;
        } catch (Exception e) {
            // Ignore: Treat the same as validation error
        }
        return ValidationResult.VALIDATION_ERROR;
    }

    public static ValidationResult isValidPDF(byte[] data) {
        if (data != null && data.length > 4 && data[0] == 0x25 && // %
                data[1] == 0x50 && // P
                data[2] == 0x44 && // D
                data[3] == 0x46 && // F
                data[4] == 0x2D) { // -

            // Check file terminator
            if (data[data.length - 6] == 0x25 && // %
                    data[data.length - 5] == 0x25 && // %
                    data[data.length - 4] == 0x45 && // E
                    data[data.length - 3] == 0x4F && // O
                    data[data.length - 2] == 0x46 && // F
                    data[data.length - 1] == 0x0A) { // EOL
                return ValidationResult.OK;
            }
        }
        return ValidationResult.INVALID_PDF;
    }

    /**
     * Check if the image is a valid GIF.
     * <p>
     * GIF files start with <code>GIF87a</code> or <code>GIF89a</code>.
     * <a href="http://www.onicos.com/staff/iz/formats/gif.html">GIF Format</a>
     */
    public ValidationResult isValidGIF(byte[] data) {
        try {
            //check 1st 3 bytes
            if (data[0] != 'G' || data[1] != 'I' || data[2] != 'F') {
                return ValidationResult.INVALID_GIF;
            }
            if (data[3] != '8' || !(data[4] == '7' || data[4] == '9') || data[5] != 'a') {
                return ValidationResult.INVALID_GIF;
            }
        } catch (Exception e) {
            // Ignore: Treat the same as validation error
        }
        return ValidationResult.VALIDATION_ERROR;
    }
}
