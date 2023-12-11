package com.example.application.views.inputvalidation;

import com.example.application.validation.ExternalValidation;
import com.example.application.validation.InputValidation;
import com.example.application.validation.ValidationResult;
import com.example.application.views.MainMenuLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Input Validation")
@Route(value = "input", layout = MainMenuLayout.class)
public class UserInputValidationView extends Composite<VerticalLayout> {

    private static final String USER = "sa";
    private static final String PASS = "";

    public UserInputValidationView() {
        getContent().setWidthFull();

        // Username pattern validation
        getContent().add(new H4("Username validation"));
        getContent().add(new Text("Try entering: ke$ha123"));
        TextField usernameField = new TextField("Username");
        usernameField.setPattern("[a-zA-Z0-9]*");
        getContent().add(usernameField);

        TextField email = new TextField("Email");
        email.setPattern(InputValidation.EMAIL_PATTERN);

        // ISBN Validation
        getContent().add(new H4("ISBN validation"));
        getContent().add(new Text("Try searching the following ISBN: 1484271785"));


        TextField isbnField = new TextField("ISBN");

        // Setting a pattern to validate ISBN-10 or ISBN-13 format
        isbnField.setPattern(InputValidation.ISBN_PATTERN);

        // Validation in event listeners
        Button validateButton = new Button("Validate using format", event -> {
            String isbn = isbnField.getValue();

            isbnField.setInvalid(false);
            if (InputValidation.validateISBN(isbn) == ValidationResult.OK) {
                Notification.show("Valid ISBN format");
            } else {
                Notification.show("Invalid ISBN format");
                isbnField.setInvalid(true);
            }
        });

        // Validation using external services
        Button validateButton2 = new Button("Validate using service", event -> {
            String isbn = isbnField.getValue();

            isbnField.setInvalid(false);
            ValidationResult result = ExternalValidation.validateISBN(isbn);
            if (result == ValidationResult.OK) {
                Notification.show("ISBN found");
            } else if (result == ValidationResult.EXTERNAL_VALIDATION_ERROR) {
                Notification.show("Failed to use external validation service");
                isbnField.setInvalid(true);
            } else {
                Notification.show("ISBN Not found");
                isbnField.setInvalid(true);
            }

        });
        getContent().add(isbnField, validateButton, validateButton2);


    }




}
