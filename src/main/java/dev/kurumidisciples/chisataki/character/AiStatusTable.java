package dev.kurumidisciples.chisataki.character;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

import java.util.HashMap;

public class AiStatusTable implements GenericDatabaseTable {
    
    @Override
    public String getTableName() {
        return "aistatus";
    }

    @Override
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS aistatus ("
                + "guild_id BIGINT PRIMARY KEY,"
                + "enabled BOOLEAN NOT NULL,"
                + "`limit` INTEGER"
                + ")";
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
    public HashMap<String, Integer> getDefinedColumns() {
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("guild_id", java.sql.Types.BIGINT);
        columns.put("enabled", java.sql.Types.BOOLEAN);
        columns.put("limit", java.sql.Types.INTEGER);
        return columns;
    }

}
