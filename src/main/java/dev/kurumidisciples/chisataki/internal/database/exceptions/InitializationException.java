package dev.kurumidisciples.chisataki.internal.database.exceptions;

import java.sql.SQLException;

import dev.kurumidisciples.chisataki.internal.database.Database;

/**
 * Is thrown when the database is used before it is initialized.
 * This exception is thrown by the Database class.
 * @author Hacking Pancakez
 * @see dev.kurumidisciples.chisataki.internal.database.Database
 */
public class InitializationException extends Exception{
    
    private final Database database;

    public InitializationException(String message, Database database) {
        super(message);
        this.database = database;
    }

    public InitializationException(String messsage){
        super(messsage);
        this.database = null;
    }

    public Database getDatabase() {
        return database;
    }
}
