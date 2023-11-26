package dev.kurumidisciples.chisataki.secretsanta;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class SantaComponents {
    

    public static Modal createModal() {
        TextInput preferredGift = TextInput.create("preferred", "What is your preferred gift?", TextInputStyle.SHORT)
        .setPlaceholder("Please keep it digital!")
        .setRequiredRange(5, 150)
        .setRequired(true)
        .build();


        TextInput chisaTaki = TextInput.create("chisataki", "Chisato, Takina, or both?", TextInputStyle.SHORT)
        .setPlaceholder("Chisato, Takina, or both?")
        .setMaxLength(10)
        .setRequired(true)
        .build();

        Modal form = Modal.create("modal:secret-santa", "Secret Santa Form")
        .addActionRow(preferredGift)
        .addActionRow(chisaTaki)
        .build();

        return form;
    }

    public static Button createButton(){
        return Button.secondary("santa-button", "Secret Santa Form").withEmoji(Emoji.fromUnicode("🎅"));
    }
   
}
