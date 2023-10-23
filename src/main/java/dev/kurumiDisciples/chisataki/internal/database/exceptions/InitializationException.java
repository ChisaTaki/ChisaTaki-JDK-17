package dev.kurumiDisciples.chisataki.internal.database.exceptions;

import dev.kurumiDisciples.chisataki.internal.database.Database;

/**
 * Is thrown when the database is used before it is initialized.
 * This exception is thrown by the Database class.
 * @author Hacking Pancakez
 * @see dev.kurumiDisciples.chisataki.internal.database.Database
 */
public class InitializationException extends Exception{
    
    private final Database database;

    public InitializationException(String message, Database database) {
        super(message);
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }
}
