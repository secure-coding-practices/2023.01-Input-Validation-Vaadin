package com.example.application.validation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class ExternalValidation extends InputValidation {


    private static final int TIMEOUT = 5000;

    /** Validation function for ISBN using external service lookup.
     *
     * @param isbn String to validate.
     * @return ValidationResult.OK if String is valid ISBN, ValidationResult.INVALID_ISBN or EXTERNAL_VALIDATION_ERROR otherwise
     */
    public static ValidationResult validateISBN(String isbn) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data").openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT);
            return new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    .lines().collect(Collectors.joining())
                    .equals("{}") ? ValidationResult.INVALID_ISBN : ValidationResult.OK;
        } catch (Exception e) {
            return ValidationResult.EXTERNAL_VALIDATION_ERROR;
        }
    }

}
