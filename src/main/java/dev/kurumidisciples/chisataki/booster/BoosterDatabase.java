package dev.kurumidisciples.chisataki.booster;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class BoosterDatabase implements GenericDatabaseTable {
    

    @Override
    public String getTableName() {
        return "boosters";
    }

    @Override
    public String getPrimaryKey(){
        return "user_id";
    }

    @Override
    public Integer getPrimaryKeyType(){
        return java.sql.Types.BIGINT;
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns(){
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("user_id", java.sql.Types.BIGINT);
        columns.put("role_id", java.sql.Types.BIGINT);
        return columns;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS boosters ("
           + "user_id BIGINT NOT NULL,"
           + "role_id BIGINT,"
           + "PRIMARY KEY (user_id)"
           +")";
    }
}
