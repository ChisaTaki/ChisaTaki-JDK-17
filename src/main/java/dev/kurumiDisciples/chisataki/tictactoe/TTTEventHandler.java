package dev.kurumiDisciples.chisataki.tictactoe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TTTEventHandler extends ListenerAdapter{
    
    private final static ExecutorService tttExecutor = Executors.newCachedThreadPool();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        tttExecutor.execute(() -> {
            if (event.getButton().getId().startsWith("TTT-")){
               event.deferEdit().queue();
               TTTGameSetup setup = TTTUtils.rebuildGameSetupFromButton(event, event.getButton().getId());
               
            }
        });
    }

    
}
