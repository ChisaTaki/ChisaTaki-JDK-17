package dev.kurumidisciples.chisataki.character;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class GlobalThread implements GenericDatabaseTable {
    

    @Override
    public String getTableName() {
        return "globalthread";
    }

    @Override
    public String getPrimaryKey(){
        return "guild_id";
    }

    @Override
    public Integer getPrimaryKeyType(){
        return java.sql.Types.BIGINT;
    }

    @Override
    public java.util.HashMap<String, Integer> getDefinedColumns(){
        java.util.HashMap<String, Integer> columns = new java.util.HashMap<>();
        columns.put(getPrimaryKey(), getPrimaryKeyType());
        columns.put("thread_id", java.sql.Types.VARCHAR);
        return columns;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " ("
            + getPrimaryKey() + " BIGINT PRIMARY KEY,"
            + "thread_id VARCHAR(255)"
            + ")";
    }
}
