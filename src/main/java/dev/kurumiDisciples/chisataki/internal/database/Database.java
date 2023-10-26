package dev.kurumiDisciples.chisataki.internal.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumiDisciples.chisataki.internal.database.exceptions.InitializationException;
import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

/**
 * This class holds all database objects and methods.
 * The database used is a SQL database and is connected to the sparkedhost database platform.
 * Database is a global class and is used by all other classes.
 * @apiNote Feature still in development and may not work as expected
 * @author Hacking Pancakez
 */
public class Database {
    

    private static Connection connection = null; // The connection to the database.
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class); // The logger used to log errors and warnings.

    static {
        init();
        createTables(TableCollection.getTables());
    }

    /**
     * This method is used to initialize the database.
     * The database used is a SQL database and is connected to the sparkedhost database platform.
     * Sharding is not supported at the moment.
     */
    private static void init() {
        if (connection == null){
            connection = DatabaseInit.createConnection();
        } else {
            LOGGER.warn("Database already initialized. Skipping init.");
        }
    }
    
    /**
     * This method is used to create the tables in the database.
     * @param tables - A list of GenericDatabaseTable objects that represent the tables to be created. Generally grabbed from TableCollection.
     */
    private static void createTables(List<GenericDatabaseTable> tables){
        LOGGER.info("Creating tables...");
        DatabaseInit.createTables(tables);
        LOGGER.info("Tables created or were already created.");
    }
    
    /**
     * This method is used to shutdown the database. This method should be called when the bot is shutting down.
     * This method closes the connection to the database.
     */
    public static void shutdown(){
        try {
            LOGGER.info("Closing connection to database...");
            connection.close();
            LOGGER.info("Connection to database closed successfully.");
        } catch (SQLException e) {
            LOGGER.error("Failed to close connection to database.", e);
        }
    }
    /**
     * This method is used to get the connection to the database. This connection is global and should be used by all other classes.
     * This is so that the database is not overloaded with connections.
     * @throws InitializationException - If the database is not initialized.
     * @return The connection to the database.
     */
    public static Connection getConnection() throws InitializationException {
        if (connection == null){
            throw new InitializationException("Database not initialized.");
        }
        return connection;
    }

    /**
     * Create PreparedStatement to manipulate/retrieve/insert data into the database.
     * @param sql The sql statement to create the preparedstatement from.
     * @return The preparedstatement created from the sql statement.
     * @throws InitializationException If the database is not initialized.
     * @throws SQLException If the preparedstatement could not be created.
     */
    public static PreparedStatement createStatement(String sql) throws InitializationException, SQLException {
        return PreparedStatementFactory.create(sql);
    }
}
