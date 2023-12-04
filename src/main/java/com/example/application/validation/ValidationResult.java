package com.example.application.validation;

/** Validation result.
 * This enum is used to indicate the results of a validation.
 */
public enum ValidationResult {
    OK, VALIDATION_ERROR, EXTERNAL_VALIDATION_ERROR,
    /* String Validation */
    STRING_TOO_LONG, STRING_TOO_SHORT, STRING_IS_NULL, STRING_PATTERN_MISMATCH,
    /* ISBN Validation */
    INVALID_ISBN_FORMAT, INVALID_ISBN,
    /* Image, pdf and binary validation */
    INVALID_JPEG, INVALID_GIF, INVALID_PNG, INVALID_PDF, INVALID_SHA256_CHECKSUM
}
