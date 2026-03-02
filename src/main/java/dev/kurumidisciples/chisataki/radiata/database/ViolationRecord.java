package dev.kurumidisciples.chisataki.radiata.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ViolationRecord {
    
    private int id;
    private String userId;
    private String imageUrl;
    private String moderationResult;
    private String timestamp;

    public ViolationRecord(int id, String userId, String imageUrl, String moderationResult, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.moderationResult = moderationResult;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getModerationResult() {
        return moderationResult;
    }

    public String getTimestamp() {
        return timestamp;
    }


    protected static ViolationRecord fromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String userId = resultSet.getString("user_id");
        String imageUrl = resultSet.getString("image_url");
        String moderationResult = resultSet.getString("moderation_result");
        String timestamp = resultSet.getString("timestamp");
        return new ViolationRecord(id, userId, imageUrl, moderationResult, timestamp);
    }
}
