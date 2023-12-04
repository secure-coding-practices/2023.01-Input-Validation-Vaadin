package com.example.application.validation;

public class InputValidation {


    public static final String ISBN_PATTERN = "(97[89][-–—]?)?(\\d{1,5}[-–—]?\\d{1,7}[-–—]?\\d{1,7}[-–—]?\\d{1,2}|\\d{1,7}[-–—]?\\d{1,7}[-–—]?\\d{1,7}[-–—]?\\d{1,7})";
    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\.\\-+])+@([a-zA-Z0-9-]+\\.)+[a-zA-Z0-9-]{2,}$";

    public static ValidationResult validateStringLength(String myString, int minLength, int maxLength) {
        if (myString == null) {
            return ValidationResult.STRING_IS_NULL;
        }

        int length = myString.length();

        if (length < minLength) {
            return ValidationResult.STRING_TOO_SHORT;
        }
        if (length > maxLength) {
            return ValidationResult.STRING_TOO_LONG;
        }
        return ValidationResult.OK;
    }

    public static ValidationResult validateNull(String myString) {
        return myString == null ? ValidationResult.STRING_IS_NULL : ValidationResult.OK;
    }

    public static ValidationResult validatePattern(String myString, String pattern) {
        if (myString == null) {
            return ValidationResult.STRING_IS_NULL;
        }
        return myString.matches(pattern) ? ValidationResult.OK : ValidationResult.STRING_PATTERN_MISMATCH;
    }

    public static ValidationResult validateISBN(String isbn) {
        if (isbn == null) {
            return ValidationResult.INVALID_ISBN_FORMAT;
        }
        isbn = isbn.replace("-", ""); // Remove hyphens if present
        if (!isbn.matches(ISBN_PATTERN)) {
            return ValidationResult.INVALID_ISBN_FORMAT;
        }
        return isbn.length() == 10 ? validateISBN10Checksum(isbn) : validateISBN13Checksum(isbn);
    }

    private static ValidationResult validateISBN10Checksum(String isbn) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = isbn.charAt(i) - '0';
            if (0 > digit || 9 < digit) {
                return ValidationResult.INVALID_ISBN_FORMAT;
            }
            sum += (digit * (10 - i));
        }
        char lastChar = isbn.charAt(9);
        if (lastChar != 'X' && (lastChar < '0' || lastChar > '9')) {
            return ValidationResult.INVALID_ISBN_FORMAT;
        }
        sum += (lastChar == 'X') ? 10 : (lastChar - '0');

        return (sum % 11 == 0) ? ValidationResult.OK : ValidationResult.INVALID_ISBN;
    }

    private static ValidationResult validateISBN13Checksum(String isbn) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            if (0 > digit || 9 < digit) {
                return ValidationResult.INVALID_ISBN_FORMAT;
            }
            sum += (digit * ((i % 2 == 0) ? 1 : 3));
        }
        int checksum = 10 - (sum % 10);
        if (checksum == 10) {
            checksum = 0;
        }
        return (checksum == isbn.charAt(12) - '0') ? ValidationResult.OK : ValidationResult.INVALID_ISBN;
    }
}

