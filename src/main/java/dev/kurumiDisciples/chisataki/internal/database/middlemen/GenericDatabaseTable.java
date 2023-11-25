package dev.kurumidisciples.chisataki.internal.database.middlemen;

import java.util.HashMap;

/**
 * This class is used to represent a database table.
 * It is used to store the table name.
 * It is important because it is used to create the tables when the database is initalized.
 * 
 * This class is abstract and is used as a base class for all database tables.
 * @author Hacking Pancakez
 */
public interface GenericDatabaseTable {
    
    /**
     * This method is used to get the name of the table.
     * @return the table name.
     */
    public abstract String getTableName();

    /**
     * This method is used to get the columns of the table. Where String represents the name of the column and Types represents the type of the column.
     * @return A HashMap containing the columns of the table.
     */
    public abstract HashMap<String, Integer> getDefinedColumns();

    /**
     * This method is used to get the primary key of the table.
     * @return The primary key of the table.
     */
    public abstract String getPrimaryKey();

    /**
     * This method is used to get the type of the primary key of the table.
     * @return The type of the primary key of the table.
     */
    public abstract Integer getPrimaryKeyType();

    public abstract String getTableSchema();
}
