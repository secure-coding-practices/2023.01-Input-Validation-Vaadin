package com.example.application.views.inputvalidation;

import com.example.application.views.MainMenuLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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

import static org.postgresql.jdbc.EscapedFunctions.USER;



@PageTitle("SQL Validation")
@Route(value = "sql", layout = MainMenuLayout.class)
public class SQLValidation extends Composite<VerticalLayout> {


    private static final String DB_URL = "";
    private static final String USER = "";
    private static final String PASS = "";

    public SQLValidation() {
        getContent().setWidthFull();

        // SQL Injection sample
        getContent().add(new H3("SQL Injection"));
        getContent().add(new Text("Try searching with the following string: %'; DROP TABLE users; --"));

        TextField searchField = new TextField("Search");
        Button searchButton = new Button("Unsafe Search", event -> {
            String searchTerm = searchField.getValue();

            String unsafeSql = "SELECT * FROM users WHERE name LIKE '%" + searchTerm + "%'";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement(unsafeSql)) {
                // Execute SQL unsafe query
                Notification.show("Executing SQL: "+ unsafeSql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button safesearchButton = new Button("Safe Search", event -> {
            String searchTerm = searchField.getValue();
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name LIKE ?")) {
                 stmt.setString(1, "%" + searchTerm + "%");
                // Execute SQL query safely
                Notification.show("Executing %s".formatted(stmt));
                stmt.execute();
            } catch (SQLException e) {
                Notification.show("SQL failed".formatted(e.getMessage()));
            }
        });
        getContent().add(searchField, searchButton, safesearchButton);
    }

}
