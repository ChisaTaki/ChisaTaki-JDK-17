package dev.kurumidisciples.chisataki.quotes;

public class QuoteSettings {
    
    private final String guildId;
    private final boolean boosterOnly;
    private final boolean enabled;
    private final int height;
    private final int width;


    public QuoteSettings(String guildId, boolean boosterOnly, boolean enabled, int height, int width) {
        this.guildId = guildId;
        this.boosterOnly = boosterOnly;
        this.enabled = enabled;
        this.height = height;
        this.width = width;
    }

    public String getGuildId() {
        return guildId;
    }

    public boolean isBoosterOnly() {
        return boosterOnly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
