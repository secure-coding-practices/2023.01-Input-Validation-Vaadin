package test.com.example.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Testcontainers
public class TestcontainerDemoTest {

    public static DockerImageName POSTGRES_TEST_IMAGE = DockerImageName.parse("postgres:16.0");
    // jdbc:tc:postgresql:9.6.8:///databasename

    @Container
    private final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_TEST_IMAGE)
            .withDatabaseName("vaadin")
            .withUsername("foo")
            .withPassword("secret")
            .withInitScript("database/init_postgresql.sql");


    private DBConnectionProvider connectionProvider;

    @BeforeEach
    void setUp() {
        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @Test
    void test() {
        Assertions.assertTrue(postgres.isRunning());
        Connection connection = connectionProvider.getConnection();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT 1");
            resultSet.next();
            int resultSetInt = resultSet.getInt(1);
            Assertions.assertEquals(1,resultSetInt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}



