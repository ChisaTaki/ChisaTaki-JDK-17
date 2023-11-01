package dev.kurumiDisciples.chisataki.internal.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumiDisciples.chisataki.internal.database.exceptions.InitializationException;
import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

/**
 * This class is used to initialize the database.
 * The database used is a SQL database and is connected to the sparkedhost database platform.
 * This class also creates the tables for the database
 * Sharding is not supported at the moment.
 * @author Hacking Pancakez
 * @see java.sql.Connection
 */
public class DatabaseInit {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInit.class);

    /**
     * This method is used to initialize the database.
     * @return The connection to the database.
     */
    protected static Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DatabaseValues.getDatabaseUrl(), DatabaseValues.getDatabaseUsername(), DatabaseValues.getDatabasePassword());
            LOGGER.info("Connection to database established successfully.");
        } catch (SQLException | NullPointerException e) {
            LOGGER.error("Failed to create connection to database.", e);
            LOGGER.info("Database could not be initialized. Shutting down bot.");
            System.exit(0);
        }
        return connection;
    }
    
    /**
     * This method is used to create the tables in the database if they do not already exist, if they do exist, they are not created.
     * @param tables The tables to create.
     */
    protected static void createTables(List<GenericDatabaseTable> tables){
        try(Connection connection = Database.getConnection()) {
            for (GenericDatabaseTable table : tables){
                    LOGGER.info("Creating table: " + table.getTableName());
                    createTable(connection, table);
            }
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to create tables.", e);
            LOGGER.info("Database could not be initialized. Shutting down bot.");
            System.exit(0);
        }
    }
    /**
     * This method is used to check if a table exists in the database. This method is used by the DatabaseInit class. This method should not be used by any other class.
     * @param table The table to check.
     * @return True if the table exists, false if it does not.
     * @throws SQLException If the table could not be checked.
     * @throws InitializationException If the database is not initialized.
     */
    private static boolean tableExists(GenericDatabaseTable table) throws SQLException, InitializationException {
        try(Connection connection = Database.getConnection()) {
            boolean exists = connection.getMetaData().getTables(null, null, table.getTableName(), null).next();
            if (exists) {
                LOGGER.info("Table exists: " + table.getTableName());
            } else {
                LOGGER.info("Table does not exist: " + table.getTableName());
            }
            return exists;
        }
    }
    /**
     * This method is used to create a table in the database. This method is used by the DatabaseInit class. This method should not be used by any other class. 
     * @param table The table to create.
     * @throws SQLException If the table could not be created.
     * @throws InitializationException If the database is not initialized.
     * @see dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable
     */
    private static void createTable(Connection connection, GenericDatabaseTable table) throws SQLException, InitializationException {
        try {
            if (connection == null || connection.isClosed()) {
                LOGGER.debug("Connection to database is closed. Reopening...");
                connection = Database.getConnection();
            }
            connection.prepareStatement(table.getTableSchema()).execute();
            LOGGER.info("Table created: " + table.getTableName());
        }
        catch (SQLNonTransientConnectionException e ){
            LOGGER.warn("Connection to database is closed. Retrying...");
            connection = createConnection();
            createTable(connection, table);
        }
        catch (SQLException e){
            LOGGER.error("Failed to create table: " + table.getTableName(), e);
            throw e;
        }
    }
}
