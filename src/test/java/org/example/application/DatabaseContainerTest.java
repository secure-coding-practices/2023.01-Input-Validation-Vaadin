package org.example.application;

import org.junit.jupiter.api.*;
import org.postgresql.util.PSQLException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Testcontainers
public class DatabaseContainerTest {

    public static DockerImageName POSTGRES_TEST_IMAGE = DockerImageName.parse("postgres:16.0");

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_TEST_IMAGE)
            .withDatabaseName("vaadin")
            .withUsername("vaadin")
            .withPassword("secret")
            .withInitScript("database/init_postgresql.sql");


    @BeforeAll
    static void setUp() {

    }

    @AfterAll
    static void cleaup() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.stop();
        }
    }

    @Test
    void testSafeSql() {
        Assertions.assertTrue(postgreSQLContainer.isRunning());
        Connection connection = getConnection();
        try {
            String sql = "SELECT username FROM accounts where user_id = 1";
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            resultSet.next();
            String username = resultSet.getString(1);
            Assertions.assertEquals("user1",username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUnsafeSql() {
        Assertions.assertTrue(postgreSQLContainer.isRunning());
        Connection connection = getConnection();
        try {
            String userId = "1'; UPDATE account_roles SET role_id=0 WHERE user_id=1; --";
            String sql = "SELECT username FROM accounts where user_id = '"+userId+"';";
            try {
                connection.createStatement().executeQuery(sql);
                Assertions.fail("Should throw a PSQLException.");
            } catch (PSQLException e) {
                // There should be exception, because of multple queries at once
                Assertions.assertEquals("Multiple ResultSets were returned by the query.", e.getMessage());
            }

            // Check that we have created escalation of privileges
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT role_id FROM account_roles where user_id=1");
            resultSet.next();
            int role = resultSet.getInt(1);
            Assertions.assertEquals(0, role, "User should have admin privileges");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Connection getConnection() {
        return getConnection(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword());
    }

    public static Connection getConnection(String url, String username, String password) {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}



