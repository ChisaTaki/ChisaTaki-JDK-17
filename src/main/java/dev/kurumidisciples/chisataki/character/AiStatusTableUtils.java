package dev.kurumidisciples.chisataki.character;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiStatusTableUtils {
    
    private final static String SELECT_AI_STATUS = "SELECT enabled FROM aistatus WHERE guild_id = ?";
    private final static String INSERT_AI_STATUS = "INSERT INTO aistatus (guild_id, enabled) VALUES (?, ?)";
    private final static String UPDATE_AI_STATUS = "UPDATE aistatus SET enabled = ? WHERE guild_id = ?";
    private final static String TOGGLE_AI_STATUS = "UPDATE aistatus SET enabled = !enabled WHERE guild_id = ?";
    private final static String SELECT_LIMIT = "SELECT `limit` FROM aistatus WHERE guild_id = ?";
    private final static String UPDATE_LIMIT = "UPDATE aistatus SET `limit` = ? WHERE guild_id = ?";
    private final static String DROP_ALL_USERS = "DELETE FROM aiusage";
    private final static String DROP_USER = "DELETE FROM aiusage WHERE user_id = ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(AiStatusTableUtils.class);


    public static boolean selectAiStatus(long guildId){
        try(PreparedStatement statement = Database.createStatement(SELECT_AI_STATUS)){
            statement.setLong(1, guildId);
            ResultSet set = statement.executeQuery();
            if(set.next()){
                return set.getBoolean("enabled");
            }
            return false;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select ai status for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }

    public static boolean insertAiStatus(long guildId, boolean enabled){
        try(PreparedStatement statement = Database.createStatement(INSERT_AI_STATUS)){
            statement.setLong(1, guildId);
            statement.setBoolean(2, enabled);
            statement.execute();
            return true;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert ai status for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }

    public static boolean updateAiStatus(long guildId, boolean enabled){
        try(PreparedStatement statement = Database.createStatement(UPDATE_AI_STATUS)){
            statement.setBoolean(1, enabled);
            statement.setLong(2, guildId);
            statement.execute();
            return true;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update ai status for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }

    public static boolean toogleAiStatus(long guildId){
        try(PreparedStatement statement = Database.createStatement(TOGGLE_AI_STATUS)){
            statement.setLong(1, guildId);
            statement.execute();
            return true;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to toggle ai status for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }

    public static int selectLimit(long guildId){
        try(PreparedStatement statement = Database.createStatement(SELECT_LIMIT)){
            statement.setLong(1, guildId);
            ResultSet set = statement.executeQuery();
            if(set.next()){
                return set.getInt("limit");
            }
            return -1;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select ai limit for guild {} for reason: {}", guildId, e.getMessage());
            return -1;
        }
    }

    public static boolean updateLimit(long guildId, int limit){
        try(PreparedStatement statement = Database.createStatement(UPDATE_LIMIT)){
            statement.setInt(1, limit);
            statement.setLong(2, guildId);
            statement.execute();
            return true;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update ai limit for guild {} for reason: {}", guildId, e.getMessage());
            return false;
        }
    }

    public static boolean resetAllUsers(){
        try(PreparedStatement statement = Database.createStatement(DROP_ALL_USERS)){
            statement.execute();
            LOGGER.info("All users have been reset");
            return true;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to reset all users for reason: {}", e.getMessage());
            return false;
        }
    }

    public static boolean resetUser(long userId){
        try(PreparedStatement statement = Database.createStatement(DROP_USER)){
            statement.setLong(1, userId);
            statement.execute();
            LOGGER.info("User {} has been reset", userId);
            return true;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to reset user {} for reason: {}", userId, e.getMessage());
            return false;
        }
    }
}
