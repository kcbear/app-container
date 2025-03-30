package org.example;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Profile("test")
@TestPropertySource("classpath:application-test.yml")
@SpringBootTest(classes = OracleTest.TestConfig.class)

public class OracleTest {
    // Create Oracle container using Testcontainers
//    private static final OracleContainer oracleContainer = new OracleContainer(DockerImageName.parse("gvenzl/oracle-xe:21-slim"))
    private static final OracleContainer oracleContainer = new OracleContainer(DockerImageName.parse("gvenzl/oracle-free:23.6-slim-faststart")
            .asCompatibleSubstituteFor("gvenzl/oracle-xe"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword")
            .withStartupTimeout(Duration.of(5, ChronoUnit.MINUTES));
//            .withNetworkMode("host")
//            .withExposedPorts(33556);

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    public static void setUp() {
        // Start Oracle container before running tests
        oracleContainer.start();

        // Optionally, wait for the container to be fully initialized
//        oracleContainer.waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());
    }

    @AfterAll
    public static void tearDown() {
        if (oracleContainer != null && oracleContainer.isRunning()) {
            oracleContainer.stop();
        }
    }

    @Test
    public void testOracleConnection() throws SQLException {

        String ip = oracleContainer.getHost();
        Integer port = oracleContainer.getMappedPort(8080);
        System.out.println("Container running on " + ip + ":" + port);
        System.out.println("##### oracleContainer.getJdbcUrl(): " + oracleContainer.getJdbcUrl());
        // Get database connection
        try (Connection connection = dataSource.getConnection()) {
            // Execute a simple query to test the connection
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT 1 FROM DUAL");
            assertTrue(resultSet.next(), "The connection to the Oracle database was successful.");
        }
    }

    // Inner static class for test configuration
    @TestConfiguration
    public static class TestConfig {

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");

            System.out.println("##### oracleContainer.getJdbcUrl(): " + oracleContainer.getJdbcUrl());
            dataSource.setUrl(oracleContainer.getJdbcUrl());
            dataSource.setUsername(oracleContainer.getUsername());
            dataSource.setPassword(oracleContainer.getPassword());
            return dataSource;
        }
    }
}
