package dev.kurumidisciples.chisataki.quotes;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class QuoteSettingsDatabase implements GenericDatabaseTable {
    
    @Override
    public String getTableName() {
        return "quote_settings";
    }

    @Override
    public String getPrimaryKey() {
        return "guild_id";
    }

    @Override
    public Integer getPrimaryKeyType() {
        return java.sql.Types.BIGINT;
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns(){
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("guild_id", java.sql.Types.BIGINT);
        columns.put("booster_only", java.sql.Types.BOOLEAN); // turns on the quote command for only boosters
        columns.put("enabled", java.sql.Types.BOOLEAN); // turns off the quote command for everyone
        columns.put("height", java.sql.Types.INTEGER); // defines the height of the generated quote image
        columns.put("width", java.sql.Types.INTEGER); // defines the width of the generated quote image
        return columns;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS quote_settings ("
            + "guild_id BIGINT NOT NULL,"
            + "booster_only BOOLEAN,"
            + "enabled BOOLEAN,"
            + "height INTEGER,"
            + "width INTEGER,"
            + "PRIMARY KEY (guild_id)"
            + ")";
    }
}
