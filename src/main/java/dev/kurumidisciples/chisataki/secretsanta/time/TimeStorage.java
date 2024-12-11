package dev.kurumidisciples.chisataki.secretsanta.time;

import java.sql.Types;
import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class TimeStorage implements GenericDatabaseTable{
    
    @Override
    public String getTableName() {
        return "santaTime";
    }

    @Override
    public String getPrimaryKey() {
        return null;
    }

    @Override
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS santaTime ("
            + "time BIGINT NOT NULL"
            + ")";
    }

    @Override
    public Integer getPrimaryKeyType() {
        return null;
    }

    @Override
    public HashMap<String,Integer> getDefinedColumns() {
        HashMap<String, Integer> columns = new HashMap<>();
            columns.put("time", Types.BIGINT);
        return columns;
    }
}
