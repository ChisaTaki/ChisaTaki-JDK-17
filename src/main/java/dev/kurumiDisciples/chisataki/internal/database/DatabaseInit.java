package dev.kurumiDisciples.chisataki.internal.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to initialize the database.
 * The database used is a SQL database and is connected to the sparkedhost database platform.
 * Sharding is not supported at the moment.
 * @author Hacking Pancakez
 * @see java.sql.Connection
 */
public class DatabaseInit {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInit.class);

    public static Connection createConnection() {
        try(Connection connection = DriverManager.getConnection(DatabaseValues.getDatabaseUrl(), DatabaseValues.getDatabaseUsername(), DatabaseValues.getDatabasePassword())) {
            return connection;
        } catch (SQLException | NullPointerException e) {
            LOGGER.error("Failed to create connection to database.", e);
            LOGGER.info("Database could not be initialized. Shutting down bot.");
            System.exit(0);
        }
        return null;
    }
}
