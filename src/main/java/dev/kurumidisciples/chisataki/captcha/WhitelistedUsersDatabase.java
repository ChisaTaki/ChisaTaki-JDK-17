package dev.kurumidisciples.chisataki.captcha;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class WhitelistedUsersDatabase implements GenericDatabaseTable {
    

    @Override
    public String getTableName() {
        return "whitelisted_users";
    }

    @Override
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS whitelisted_users ("
                + "user_id BIGINT PRIMARY KEY"
                + ")";
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
    public HashMap<String, Integer> getDefinedColumns() {
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("user_id", java.sql.Types.BIGINT);
        return columns;
    }
}
