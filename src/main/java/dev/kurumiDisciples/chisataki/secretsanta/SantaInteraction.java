package dev.kurumidisciples.chisataki.secretsanta;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SantaInteraction extends ListenerAdapter {

    private static final ExecutorService commandExecutor = Executors.newCachedThreadPool();
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        commandExecutor.execute(() -> {
            if(event.getComponentId().equals("santa-button")){
                event.replyModal(ModalUtils.createModal()).queue();
            }
        });
    }
}
