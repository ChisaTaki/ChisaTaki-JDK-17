package dev.kurumiDisciples.chisataki.listeners;

import java.sql.Types;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;
import dev.kurumiDisciples.chisataki.shrine.ShrineInteractionFactory;
import dev.kurumiDisciples.chisataki.shrine.ShrineInteractionHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ShrineInteraction extends ListenerAdapter implements GenericDatabaseTable{

    private static final ExecutorService shrineExecutor = Executors.newCachedThreadPool();

    public void onMessageReceived(MessageReceivedEvent event) {
        shrineExecutor.submit(() -> handleShrineMessageReceived(event));
    }

    private void handleShrineMessageReceived(MessageReceivedEvent event) {
        ShrineInteractionHandler shrineHandler = ShrineInteractionFactory
                .getShrineInteractionHandler(event.getChannel().getId());
        if (shrineHandler != null) {
            shrineHandler.handleShrineInteraction(event);
        }
    }
    
    @Override
    public String getTableName() {
        return "shrine_interactions";
    }

    @Override
    public HashMap<String, Integer> getDefinedColumns(){
        HashMap<String, Integer> columns = new HashMap<>();
        columns.put("takina_shrine", Types.BIGINT);
        columns.put("chisato_shrine", Types.BIGINT);
        return columns;
    }

    
}