package dev.kurumidisciples.chisataki.listeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.shrine.ShrineInteractionFactory;
import dev.kurumidisciples.chisataki.shrine.ShrineInteractionHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ShrineInteraction extends ListenerAdapter {

    private static final ExecutorService shrineExecutor = Executors.newCachedThreadPool();

    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        shrineExecutor.submit(() -> handleShrineMessageReceived(event));
    }

    private void handleShrineMessageReceived(MessageReceivedEvent event) {
        ShrineInteractionHandler shrineHandler = ShrineInteractionFactory
                .getShrineInteractionHandler(event.getChannel().getId());
        if (shrineHandler != null) {
            shrineHandler.handleShrineInteraction(event);
        }
    }
    
}