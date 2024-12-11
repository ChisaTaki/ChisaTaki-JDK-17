package dev.kurumidisciples.chisataki.secretsanta;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumidisciples.chisataki.secretsanta.time.SantaClock;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SantaInteraction extends ListenerAdapter {

    private static final ExecutorService commandExecutor = Executors.newCachedThreadPool();
    
        @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        commandExecutor.execute(() -> {
            String componentId = event.getComponentId();

            if ("santa-button".equals(componentId)) {
                if (SantaDatabaseUtils.isUserRegistered(event.getUser().getIdLong())) {
                    // If the interaction is within a day of the set time, disallow withdrawing
                    if (SantaClock.isTimeSet() && SantaClock.isWithinDay(SantaClock.getTime())) {
                        event.reply("You're already registered! If you would like to withdraw past the deadline, please contact a staff member through <#1010080963927232573>.")
                            .setEphemeral(true)
                            .queue();
                    } else {
                        event.reply("You're already registered! If you’d like to completely withdraw, please confirm below.")
                            .setEphemeral(true)
                            .setActionRow(Button.danger("button:santa-withdraw", "Withdraw"))
                            .queue();
                    }
                } else {
                    // User not registered, show the registration modal
                    event.replyModal(SantaComponents.createModal()).queue();
                }
            } else if ("button:santa-withdraw".equals(componentId)) {
                // User confirms withdrawal
                SantaDatabaseUtils.deleteUser(event.getUser().getIdLong());
                event.reply("You have been withdrawn from the Secret Santa event.")
                    .setEphemeral(true)
                    .queue();
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
