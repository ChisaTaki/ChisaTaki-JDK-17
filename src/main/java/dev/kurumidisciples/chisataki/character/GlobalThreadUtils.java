package dev.kurumidisciples.chisataki.character;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;
import dev.kurumidisciples.chisataki.internal.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GlobalThreadUtils {
    

    private static final String GET_THREAD_ID_FROM_GUILD = "SELECT thread_id FROM globalthread WHERE guild_id = ?";
    private static final String INSERT_GUILD_THREAD = "INSERT INTO globalthread (guild_id, thread_id) VALUES (?, ?)";
    private static final String UPDATE_GUILD_THREAD = "UPDATE globalthread SET thread_id = ? WHERE guild_id = ?";

    private static Logger logger = LoggerFactory.getLogger(GlobalThreadUtils.class);


    public static String getThreadIdFromGuild(long guildId) {
        try {
            PreparedStatement statement = Database.createStatement(GET_THREAD_ID_FROM_GUILD);
            statement.setLong(1, guildId);
            java.sql.ResultSet set = statement.executeQuery();
    
            if (set.next()) {
                return set.getString("thread_id");
            }
            return null;
        } catch (SQLException | InitializationException e) {
            logger.error("Failed to get thread id for guild {} for reason: {}", guildId, e.getMessage());
            return null;
        }
    }

    public static boolean insertGuildThread(long guildId, String threadId) {
        try {
            PreparedStatement statement = Database.createStatement(INSERT_GUILD_THREAD);
            statement.setLong(1, guildId);
            statement.setString(2, threadId);
            return statement.executeUpdate() > 0;
        } catch (SQLException | InitializationException e) {
            logger.error("Failed to insert guild thread for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }

    public static boolean updateGuildThread(long guildId, String threadId) {
        try {
            PreparedStatement statement = Database.createStatement(UPDATE_GUILD_THREAD);
            statement.setString(1, threadId);
            statement.setLong(2, guildId);
            return statement.executeUpdate() > 0;
        } catch (SQLException | InitializationException e) {
            logger.error("Failed to update guild thread for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }
}
