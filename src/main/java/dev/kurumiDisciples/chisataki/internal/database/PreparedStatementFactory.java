package dev.kurumiDisciples.chisataki.internal.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import dev.kurumiDisciples.chisataki.internal.database.exceptions.InitializationException;
import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

/**
 * This class is used to streamline the creation of PreparedStatement objects using the GenericDatabaseTable class as the backbone for the table involved in the query. 
 * This class requires that the Database has been initialized before it is used in any way since the Connection object is required to create the PreparedStatement object.
 * 
 * @author Hacking Pancakez
 * @see java.sql.PreparedStatement
 * @see java.sql.Connection
 * @see dev.kurumiDisciples.chisataki.internal.database.Database
 * @see dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable
 * @see dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericQueryInterface
 */
public class PreparedStatementFactory {
    
    /**
     * Create preparedstatement to retrieve data from the database.
     * @param table The table to retrieve data from.
     * @return
     * @throws InitializationException
     * @throws SQLException
     */
    public static PreparedStatement create(String sql) throws InitializationException, SQLException{
        return Database.getConnection().prepareStatement(sql);
    }
}
