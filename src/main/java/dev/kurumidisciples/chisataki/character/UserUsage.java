package dev.kurumidisciples.chisataki.character;

public class UserUsage {
    

    private final long userId;
    private long lastMessage;
    private int messageCount;
    private boolean reachedLimitOfDay;
    private String aiThreadId;

    public UserUsage(long userId, long lastMessage, int messageCount, boolean reachedLimitOfDay, String aiThreadId) {
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.messageCount = messageCount;
        this.reachedLimitOfDay = reachedLimitOfDay;
        this.aiThreadId = aiThreadId;
    }

    public long getUserId() {
        return userId;
    }

    public String getAiThreadId() {
        return aiThreadId;
    }

    public long getLastMessage() {
        return lastMessage;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public boolean hasReachedLimitOfDay(long guildId) {
        int limit = AiStatusTableUtils.selectLimit(guildId);
        return messageCount >= limit;
    }
    
    public boolean getReachedLimitOfDayVariable(){ 
        return reachedLimitOfDay;
    }

    public void setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setAiThreadId(String aiThreadId) {
        this.aiThreadId = aiThreadId;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setReachedLimitOfDay(boolean reachedLimitOfDay) {
        this.reachedLimitOfDay = reachedLimitOfDay;
    }

    public void incrementMessageCount(){
        this.messageCount++;
    }
}
