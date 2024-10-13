package dev.kurumidisciples.chisataki.character.expressions;

import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum ChisatoExpressions {
    
    CHISATO_HAPPY("cHappy", "<:ChisatoDerpStare:1058894734321516656>"),
    ChISATO_BLUSH("cBlush", "<:ChisatoBlush:1015335488162386002>"),
    CHISATO_SAD("cSad", "<:chisatoderpcry:1294858332095381584>"),
    CHISATO_HUH("cHuh", "<:ChisatoFumoStare:1070126289027088516>"),
    CHISATO_ANGRY("cAngry", "<:ChisatoAngry:1060392060747796540>"),
    CHISATO_CONFUSED("cConfused", "<:ChisatoNani:1191378288308932699>"),
    CHISATO_SMUG("cSmug", "<:ChisatoSmug:1057116491260108841>"),
    CHISATO_THINKING("cThinking", "<:ChisatoThink:1015337819037765722>");

    private final String key;
    private final String emoji;

    ChisatoExpressions(String key, String emoji) {
        this.key = key;
        this.emoji = emoji;
    }


    public String getKey() {
        return key;
    }

    public CustomEmoji getEmoji() {
        return Emoji.fromFormatted(emoji).asCustom();
    }

    public static ChisatoExpressions getExpression(String key) {
        for (ChisatoExpressions expression : values()) {
            if (expression.getKey().equals(key)) {
                return expression;
            }
        }
        return null;
    }
}
