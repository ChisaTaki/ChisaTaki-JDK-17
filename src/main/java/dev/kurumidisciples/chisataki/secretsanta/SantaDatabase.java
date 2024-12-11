package dev.kurumidisciples.chisataki.secretsanta;

import java.sql.Types;
import java.util.HashMap;

import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class SantaDatabase implements GenericDatabaseTable{
    

    @Override
    public Integer getPrimaryKeyType() {
        return Types.BIGINT;
    }

    @Override
    public String getTableName() {
        return "secretsanta";
    }

    @Override
    public String getPrimaryKey() {
        return "user_id";
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns(){
        HashMap<String, Integer> columns = new HashMap<>();
            columns.put("user_id", Types.BIGINT);
            columns.put("preferred_gift", Types.VARCHAR);
            columns.put("chisataki", Types.VARCHAR);
        return columns;
    }

    @Override
    public String getTableSchema(){
        return "CREATE TABLE IF NOT EXISTS secretsanta ("
            + "user_id BIGINT NOT NULL,"
            + "preferred_gift VARCHAR(150) NOT NULL,"
            + "chisataki VARCHAR(10) NOT NULL,"
            + "PRIMARY KEY (user_id)"
            + ")";
    } 
}
