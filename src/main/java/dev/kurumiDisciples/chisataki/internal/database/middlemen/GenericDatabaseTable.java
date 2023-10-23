package dev.kurumiDisciples.chisataki.internal.database.middlemen;

/**
 * This class is used to represent a database table.
 * It is used to store the table name.
 * 
 * This class is abstract and is used as a base class for all database tables.
 * @author Hacking Pancakez
 */
public abstract class GenericDatabaseTable {
    
    private final String tableName;
    
    public GenericDatabaseTable(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return tableName;
    }
}
