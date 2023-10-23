package dev.kurumiDisciples.chisataki.internal.database;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumiDisciples.chisataki.internal.database.exceptions.InitializationException;

/**
 * This class holds all database objects and methods.
 * The database used is a SQL database and is connected to the sparkedhost database platform.
 * Database is a global class and is used by all other classes.
 * @author Hacking Pancakez
 */
public class Database {
    

    private static Connection connection = null; // The connection to the database.
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class); // The logger used to log errors and warnings.


    /**
     * This method is used to initialize the database.
     * The database used is a SQL database and is connected to the sparkedhost database platform.
     * Sharding is not supported at the moment.
     */
    public static void init() {
        if (connection == null){
            connection = DatabaseInit.createConnection();
        } else {
            LOGGER.warn("Database already initialized. Skipping init.");
        }
    }

    /**
     * This method is used to get the connection to the database.
     * @throws InitializationException If the database is not initialized.
     * @return The connection to the database.
     */
    public Connection getConnection() throws InitializationException {
        if (connection == null){
            throw new InitializationException("Database not initialized.", this);
        }
        return connection;
    }


}
