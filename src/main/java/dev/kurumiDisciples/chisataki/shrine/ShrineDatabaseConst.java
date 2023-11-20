package dev.kurumiDisciples.chisataki.shrine;

import java.sql.Types;
import java.util.HashMap;

import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class ShrineDatabaseConst implements GenericDatabaseTable {
    
    @Override
    public String getTableName() {
        return "shrines";
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns(){
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("name", Types.VARCHAR);
        columns.put("count", Types.INTEGER);
        return columns;
    }

    @Override
    public String getPrimaryKey(){
        return "name";
    }

    @Override
    public Integer getPrimaryKeyType(){
        return Types.VARCHAR;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " ("
            + "name VARCHAR(255) NOT NULL,"
            + "count INT NOT NULL,"
            + "PRIMARY KEY (name)"
            + ");";
    }
}
