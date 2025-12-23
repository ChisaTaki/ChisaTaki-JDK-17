package dev.kurumidisciples.chisataki.introduction;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class IntroductionUsers implements GenericDatabaseTable {
    
    @Override
    public String getTableName() {
        return "introduction_users";
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
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS introduction_users ("
           + "user_id BIGINT NOT NULL,"
           + "introduction_message_id BIGINT,"
           + "PRIMARY KEY (user_id)"
           +")";
    }

    @Override
    public java.util.HashMap<String, Integer> getDefinedColumns() {
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("user_id", java.sql.Types.BIGINT);
        columns.put("introduction_message_id", java.sql.Types.BIGINT);
        return columns;
    }
}
