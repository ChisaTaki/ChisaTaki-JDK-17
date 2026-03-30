package dev.kurumidisciples.chisataki.games.connect4.enums;

import dev.kurumidisciples.chisataki.enums.EmojiEnum;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

public enum ConnectPieces {

   
    TAKINA(EmojiEnum.SAKANA.getAsText()),
    CHISATO(EmojiEnum.CHINANAGO.getAsText());

    private String text;
    private ConnectPieces(String formattedString){
        this.text = formattedString;
    }

    public Emoji getEmoji(){
        return Emoji.fromFormatted(text);
    }
}
