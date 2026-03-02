package dev.kurumidisciples.chisataki.radiata.database;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

import java.sql.Types;
import java.util.HashMap;

public class ViolationRecordDatabase implements GenericDatabaseTable {

    @Override
    public String getTableName() {
        return "radiata_violation_records";
    }

    @Override
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS radiata_violation_records (" +
                "id SERIAL PRIMARY KEY," +
                "user_id VARCHAR(255) NOT NULL," +
                "image_url TEXT NOT NULL," +
                "moderation_result TEXT NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
    }

    @Override
    public String getPrimaryKey() {
        return "id";
    }

    @Override
    public Integer getPrimaryKeyType() {
        return Types.INTEGER;
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns() {
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("id", Types.INTEGER);
        columns.put("user_id", Types.VARCHAR);
        columns.put("image_url", Types.LONGVARCHAR);
        columns.put("moderation_result", Types.LONGVARCHAR);
        columns.put("timestamp", Types.TIMESTAMP);
        return columns;
    }
    
}
