package dev.kurumidisciples.chisataki.character;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class UsageTable implements GenericDatabaseTable {


    @Override
    public String getTableName() {
        return "aiusage";
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
        columns.put(getPrimaryKey(), getPrimaryKeyType());
        columns.put("last_message", java.sql.Types.TIMESTAMP);
        columns.put("message_count", java.sql.Types.INTEGER);
        columns.put("reached_limit_of_day", java.sql.Types.BOOLEAN);
        columns.put("thread_id", java.sql.Types.VARCHAR);
        return columns;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " ("
            + getPrimaryKey() + " BIGINT PRIMARY KEY,"
            + "last_message TIMESTAMP,"
            + "message_count INTEGER,"
            + "reached_limit_of_day BOOLEAN,"
            + "thread_id VARCHAR(255)"
            + ")";
    }

}
