package dev.kurumidisciples.chisataki.secretsanta;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class SantaInteraction extends ListenerAdapter {

    private static final ExecutorService commandExecutor = Executors.newCachedThreadPool();
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        commandExecutor.execute(() -> {
            if(event.getComponentId().equals("santa-button")){
                if (SantaDatabaseUtils.isUserRegistered(event.getUser().getIdLong())) {
                    event.reply("You're already registered! If you like to completely withdraw, please contact <@360241951804620800>.").setEphemeral(true).queue();
                } else {
                    event.replyModal(SantaComponents.createModal()).queue();
                }
            }
        });
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event){
        commandExecutor.execute(() -> {
            if(event.getInteraction().getModalId().equals("modal:secret-santa")){
                event.deferReply(true).queue();
                List<ModalMapping> mapping = event.getInteraction().getValues();

                String preferredGift = mapping.get(0).getAsString();
                String chisaTaki = mapping.get(1).getAsString();

                SantaStruct register = new SantaStruct(event.getUser().getIdLong(), preferredGift, chisaTaki);

                SantaDatabaseUtils.insertUser(register);

                event.getHook().editOriginal("You're responses have been recorded! Thank you for participating in Secret Santa!").queue();
            }
        });
    }
}
