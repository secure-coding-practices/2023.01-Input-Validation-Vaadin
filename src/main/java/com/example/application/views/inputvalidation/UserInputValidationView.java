package com.example.application.views.inputvalidation;

import com.example.application.views.MainMenuLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.stream.Collectors;

@PageTitle("Input Validation")
@Route(value = "hello", layout = MainMenuLayout.class)
public class UserInputValidationView extends Composite<VerticalLayout> {

    private static final String USER = "sa";
    private static final String PASS = "";
    public static final String ISBN_PATTERN = "(97[89][-–—]?)?(\\d{1,5}[-–—]?\\d{1,7}[-–—]?\\d{1,7}[-–—]?\\d{1,2}|\\d{1,7}[-–—]?\\d{1,7}[-–—]?\\d{1,7}[-–—]?\\d{1,7})";

    public UserInputValidationView() {
        getContent().setWidthFull();

        // ISBN Validation
        getContent().add(new H2("ISBN validation"));
        getContent().add(new Text("Try entering: ke$ha123"));
        TextField usernameField = new TextField("Username");
        usernameField.setPattern("[a-zA-Z0-9]*");
        usernameField.getDefaultValidator();
        getContent().add(usernameField);


        // ISBN Validation
        getContent().add(new H2("ISBN validation"));
        getContent().add(new Text("Try searching the following ISBN: 1484271785"));


        TextField isbnField = new TextField("ISBN");

        // Setting a pattern to validate ISBN-10 or ISBN-13 format
        isbnField.setPattern(ISBN_PATTERN);

        // Validation using events
        Button validateButton = new Button("Validate using format", event -> {
            String isbn = isbnField.getValue();

            isbnField.setInvalid(false);
            if (isValidISBNPattern(isbn)) {
                Notification.show("Valid ISBN format");
            } else {
                Notification.show("Invalid ISBN format");
                isbnField.setInvalid(true);
            }
        });

        Button validateButton2 = new Button("Validate using service", event -> {
            String isbn = isbnField.getValue();

            isbnField.setInvalid(false);
            if (isValidISBN(isbn)) {
                Notification.show("Valid ISBN");
            } else {
                Notification.show("Invalid ISBN");
                isbnField.setInvalid(true);
            }
        });
        getContent().add(isbnField, validateButton, validateButton2);


    }

    /** Validation function for ISBN using pattern.
     *
     * @param isbn String to validate.
     * @return true if String is valid ISBN format, false otherwise
     */
    private boolean isValidISBNPattern(String isbn) {
        // First we just check the pattern
        return isbn.matches(ISBN_PATTERN);
    }

    /** Validation function for ISBN using external service.
     *
     * @param isbn String to validate.
     * @return true if String is valid ISBN, false otherwise
     */
    private boolean isValidISBN(String isbn) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data").openConnection();
            conn.setRequestMethod("GET");

            return !new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    .lines().collect(Collectors.joining())
                    .equals("{}");
        } catch (Exception e) {
            return false;
        }
    }

}
