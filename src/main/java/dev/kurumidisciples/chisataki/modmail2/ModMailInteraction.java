package dev.kurumidisciples.chisataki.modmail2;

import java.awt.Color;
import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;



public class ModMailInteraction extends ListenerAdapter {
    
    /**
     * Listens for the user to press the ticket openning button
     */
    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event){
        if (event.getComponentId().equals("button:modmail")){
            event.replyModal(getModMailModal()).queue(); // Displays the modal for the user
        }
    }

    /**
     * Handles the modal menu we just created.
     */
    @SuppressWarnings("null")
    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event){
        if (!event.getModalId().equals("modal:modmail")) return;

        event.deferReply(true).queue(); // Acknowledge the modal interaction

        TextChannel templateChannel = event.getGuild().getTextChannelById(1011966579610755102L);
    }


    private static Modal getModMailModal(){
        // Create a text input for the user to enter their message
        TextInput messageInput = TextInput.create("message", "Your Message for the Staff", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Type your message here...")
                .setMinLength(1)
                .setMaxLength(2000)
                .build();

        // Create a modal with the text input
        return Modal.create("modal:modmail", "Contact Staff")
                .addActionRow(messageInput)
                .build();
    }
}
