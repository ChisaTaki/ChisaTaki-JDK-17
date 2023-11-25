package dev.kurumidisciples.chisataki.secretsanta;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ModalUtils {
    

    public static Modal createModal() {
        TextInput perferredGift = TextInput.create("perferred", "What is your perferred gift?", TextInputStyle.SHORT)
        .setPlaceholder("Cannot be worth more than $50 USD.")
        .setRequiredRange(5, 150)
        .setRequired(true)
        .build();

        TextInput commentsOrConcerns = TextInput.create("comments", "Any comments or concerns?", TextInputStyle.SHORT)
        .setPlaceholder("Have any questions? Let us know!")
        .setMaxLength(1000)
        .build();

        TextInput chisaTaki = TextInput.create("chisataki", "Chisato, Takina, or both?", TextInputStyle.SHORT)
        .setPlaceholder("Chisato, Takina, or both?")
        .setMaxLength(10)
        .setRequired(true)
        .build();

        Modal form = Modal.create("modal:secret-santa", "Secret Santa Form")
        .addActionRow(perferredGift, commentsOrConcerns, chisaTaki)
        .build();

        return form;
    }

    public static Button createButton(){
        return Button.primary("santa-button", "Secret Santa Form").withEmoji(Emoji.fromUnicode("🎅"));
    }
   
}
