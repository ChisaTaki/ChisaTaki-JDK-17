package dev.kurumidisciples.chisataki.secretsanta;

import java.sql.Types;
import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class Pairings implements GenericDatabaseTable {
    
    @Override
    public String getTableName() {
        return "pairings";
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns() {
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("santa_id", Types.BIGINT);
        columns.put("target_id", Types.BIGINT);
        return columns;
    }

    @Override
    public String getPrimaryKey() {
        return "santa_id";
    }

    @Override
    public Integer getPrimaryKeyType() {
        return Types.BIGINT;
    }

    @Override
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS pairings (santa_id BIGINT NOT NULL, target_id BIGINT NOT NULL, PRIMARY KEY (santa_id))";
    }
}
