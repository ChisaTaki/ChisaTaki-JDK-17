package dev.kurumidisciples.chisataki.character.expressions;

import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum TakinaExpressions {
    
    TAKINA_HAPPY("tHappy", "<:derpeyes:1295096273577447485>"),
    TAKINA_BLUSH("tBlush", "<:takinablush:1294807910639206421>"),
    TAKINA_SAD("tSad", "<:takinacry:1294808353947783268>"),
    TAKINA_HUH("tHuh", "<:TakinaProcessing:1014189860204073072>"),
    TAKINA_ANGRY("tAngry", "<:takinamad:1294808805816926228>"),
    TAKINA_CONFUSED("tConfused", "<:TakinaConcern:1044611069680832623>"),
    TAKINA_SMUG("tSmug", "<:TakinaSmug:1057655170886013018>"),
    TAKINA_THINKING("tThinking", "<:takinathink:1294810202855702609>");


    private final String key;
    private final String emoji;

    TakinaExpressions(String key, String emoji) {
        this.key = key;
        this.emoji = emoji;
    }

    public String getKey() {
        return key;
    }

    public CustomEmoji getEmoji() {
        return Emoji.fromFormatted(emoji).asCustom();
    }

    public static TakinaExpressions getExpression(String key) {
        for (TakinaExpressions expression : values()) {
            if (expression.getKey().equals(key)) {
                return expression;
            }
        }
        return null;
    }
}
