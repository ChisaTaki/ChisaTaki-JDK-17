package dev.kurumidisciples.chisataki.enums;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum NumberEnums{
    ZERO("0️⃣"),
    ONE("1️⃣"),
    TWO("2️⃣"),
    THREE("3️⃣"),
    FOUR("4️⃣"),
    FIVE("5️⃣"),
    SIX("6️⃣"),
    SEVEN("7️⃣"),
    EIGHT("8️⃣"),
    NINE("9️⃣");
    

    public String emojiUnicodeString;

    private NumberEnums(String emojiUnicodeString){
        this.emojiUnicodeString = emojiUnicodeString;
    }

    public String getUnicode(){
        return this.emojiUnicodeString;
    }

    public Emoji getEmoji(){
        return Emoji.fromUnicode(this.emojiUnicodeString);
    }

    public static NumberEnums getEnumFromInt(int i){
        switch(i){
            case 0:
                return ZERO;
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            case 7:
                return SEVEN;
            case 8:
                return EIGHT;
            case 9:
                return NINE;
        }
        return null;
    }
}
