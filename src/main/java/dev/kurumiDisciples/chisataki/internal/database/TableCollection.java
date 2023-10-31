package dev.kurumiDisciples.chisataki.internal.database;

import java.util.List;

import dev.kurumiDisciples.chisataki.commands.slash.IgnoreCommand;
import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class TableCollection {
    
    public static List<GenericDatabaseTable> getTables(){
        return List.of(
            new IgnoreCommand()
        );
    }
}
