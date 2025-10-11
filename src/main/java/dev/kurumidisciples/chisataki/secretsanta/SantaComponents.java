package dev.kurumidisciples.chisataki.secretsanta;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.modals.Modal;

public class SantaComponents {
    

    public static Modal createModal() {
        TextInput preferredGift = TextInput.create("preferred", TextInputStyle.SHORT)
        .setPlaceholder("Please keep it digital!")
        .setRequiredRange(5, 150)
        .setRequired(true)
        .build();


        TextInput chisaTaki = TextInput.create("chisataki", TextInputStyle.SHORT)
        .setPlaceholder("Chisato, Takina, or both?")
        .setMaxLength(10)
        .setRequired(true)
        .build();

        Modal form = Modal.create("modal:secret-santa", "Secret Santa Form")
        .addComponents(
            Label.of(
             "Lets know what you perfer as a gift.",
             preferredGift
            ),
            Label.of(
             "Which one is your favorite?",
             chisaTaki
            )
        )
        .build();
        return form;
    }

    public static Button createButton(){
        return Button.secondary("santa-button", "Secret Santa Form").withEmoji(Emoji.fromUnicode("🎅"));
    }
   
}
