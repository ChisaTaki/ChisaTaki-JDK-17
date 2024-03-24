package dev.kurumidisciples.chisataki.quotes.quota;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class QuoteQuotaDatabase implements GenericDatabaseTable {
    
    @Override
    public String getTableName() {
        return "quote_quota";
    }

    @Override
    public String getPrimaryKey() {
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
        columns.put("timestamp", java.sql.Types.TIMESTAMP);
        return columns;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS quote_quota ("
            + "user_id BIGINT NOT NULL,"
            + "timestamp TIMESTAMP,"
            + "PRIMARY KEY (user_id)"
            + ")";
    }
}
