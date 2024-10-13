package dev.kurumidisciples.chisataki.character;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class UsageTableUtils {
    


    private final static String SELECT_USER_FROM_USAGE = "SELECT * FROM aiusage WHERE user_id = ?";
    private final static String INSERT_USER_TO_USAGE = "INSERT INTO aiusage (user_id, last_message, message_count, reached_limit_of_day, thread_id) VALUES (?, ?, ?, ?, ?)";
    private final static String UPDATE_USER_IN_USAGE = "UPDATE aiusage SET last_message = ?, message_count = ?, reached_limit_of_day = ?, thread_id = ? WHERE user_id = ?";
    private final static String SET_ALL_USERS_TO_NOT_REACHED_LIMIT = "UPDATE aiusage SET reached_limit_of_day = false";
    private final static String SELECT_USER_THREADID = "SELECT thread_id FROM aiusage WHERE user_id = ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(UsageTableUtils.class);



    // the user id will be their discord id
    public static UserUsage selectUserUsage(long userId) {
        try {
            PreparedStatement statement = Database.createStatement(SELECT_USER_FROM_USAGE);
            statement.setLong(1, userId);
            ResultSet set = statement.executeQuery();
    
            if (set.next()) {
                return new UserUsage(
                    set.getLong("user_id"),
                    set.getTimestamp("last_message").getTime(),
                    set.getInt("message_count"),
                    set.getBoolean("reached_limit_of_day"),
                    set.getString("thread_id")
                );
            }
            return null;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select user usage for user {} for reason: {}", userId, e.getMessage());
            return null;
        }
    }
    

    public static boolean insertUserUsage(UserUsage usage) {
        try {
            PreparedStatement statement = Database.createStatement(INSERT_USER_TO_USAGE);
            statement.setLong(1, usage.getUserId());
            statement.setTimestamp(2, new java.sql.Timestamp(usage.getLastMessage()));
            statement.setInt(3, usage.getMessageCount());
            statement.setBoolean(4, false);
            statement.setString(5, usage.getAiThreadId());
            return statement.executeUpdate() > 0;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert user usage for user {} for reason: {}", usage.getUserId(), e.getMessage());
            return false;
        }
    }
    

    public static boolean doesUserUsageExist(long userId){
        return selectUserUsage(userId) != null;
    }

    public static boolean updateUserUsage(UserUsage usage, long guildId) {
        try {
            PreparedStatement statement = Database.createStatement(UPDATE_USER_IN_USAGE);
            statement.setTimestamp(1, new java.sql.Timestamp(usage.getLastMessage()));
            statement.setInt(2, usage.getMessageCount());
            statement.setBoolean(3, usage.hasReachedLimitOfDay(guildId));
            // Swap these two lines:
            statement.setString(4, usage.getAiThreadId());  // Now matches thread_id (String)
            statement.setLong(5, usage.getUserId());        // Now matches user_id (Long)
            return statement.executeUpdate() > 0;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update user usage for user {} for reason: {}", usage.getUserId(), e.getMessage());
            return false;
        }
    }
    
    

    public static void resetAllUserUsages(){
        try{
            PreparedStatement statement = Database.createStatement(SET_ALL_USERS_TO_NOT_REACHED_LIMIT);
            statement.executeUpdate();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to reset all user usages for reason: {}", e.getMessage());
        }
    }

    public static String selectUserThreadId(long userId){
        try {
            PreparedStatement statement = Database.createStatement(SELECT_USER_THREADID);
            statement.setLong(1, userId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getString("thread_id");
            }
            return null;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select user thread id for user {} for reason: {}", userId, e.getMessage());
            return null;
        }
    }


    public boolean hasReachedLimitOfDay(long userId, long guildId){
        UserUsage usage = selectUserUsage(userId);
        if(usage == null){
            return false;
        }
        return usage.hasReachedLimitOfDay(guildId);
    }
}
