package dev.kurumidisciples.chisataki.quotes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class QuoteSettingsUtils {
    

    private static final String UPDATE_BOOSTER_ONLY = "UPDATE quote_settings SET booster_only = ? WHERE guild_id = ?;";
    private static final String UPDATE_ENABLED = "UPDATE quote_settings SET enabled = ? WHERE guild_id = ?;";
    private static final String UPDATE_HEIGHT = "UPDATE quote_settings SET height = ? WHERE guild_id = ?;";
    private static final String UPDATE_WIDTH = "UPDATE quote_settings SET width = ? WHERE guild_id = ?;";
    private static final String SELECT_SETTINGS = "SELECT * FROM quote_settings WHERE guild_id = ?;";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteSettingsUtils.class);


    public static QuoteSettings updateBoosterOnly(String guildId, boolean boosterOnly){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BOOSTER_ONLY)) {
            statement.setBoolean(1, boosterOnly);
            statement.setString(2, guildId);
            statement.executeUpdate();
            return selectSettings(guildId);
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update booster only", e);
        }
        return null;
    }

    public static QuoteSettings selectSettings(String guildId){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SETTINGS)) {
            statement.setString(1, guildId);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new QuoteSettings(guildId, resultSet.getBoolean("booster_only"), resultSet.getBoolean("enabled"), resultSet.getInt("height"), resultSet.getInt("width"));
                }
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select settings", e);
        }
        return null;
    }

    public static QuoteSettings updateEnabled(String guildId, boolean enabled){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ENABLED)) {
            statement.setBoolean(1, enabled);
            statement.setString(2, guildId);
            statement.executeUpdate();
            return selectSettings(guildId);
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update enabled", e);
        }
        return null;
    }

    public static QuoteSettings updateHeight(String guildId, int height){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_HEIGHT)) {
            statement.setInt(1, height);
            statement.setString(2, guildId);
            statement.executeUpdate();
            return selectSettings(guildId);
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update height", e);
        }
        return null;
    }

    public static QuoteSettings updateWidth(String guildId, int width){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_WIDTH)) {
            statement.setInt(1, width);
            statement.setString(2, guildId);
            statement.executeUpdate();
            return selectSettings(guildId);
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update width", e);
        }
        return null;
    }
}
