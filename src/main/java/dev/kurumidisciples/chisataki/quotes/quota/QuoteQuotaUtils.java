package dev.kurumidisciples.chisataki.quotes.quota;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class QuoteQuotaUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteQuotaUtils.class);

    private static final String INSERT_QUOTA = "INSERT INTO quote_quota (user_id, timestamp) VALUES (?, ?) ON DUPLICATE KEY UPDATE timestamp = ?;";
    private static final String SELECT_TIMESTAMP = "SELECT timestamp FROM quote_quota WHERE user_id = ?;";
    

    public static void insertQuota(long userId, long timestamp){
        try (var connection = Database.getConnection();
             var statement = connection.prepareStatement(INSERT_QUOTA)) {
            statement.setLong(1, userId);
            statement.setTimestamp(2, new Timestamp(timestamp));
            statement.setTimestamp(3, new Timestamp(timestamp));
            statement.executeUpdate();
            LOGGER.info("Inserted quota for user {}", userId);
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert quota", e);
        }
    }

    public static Timestamp selectTimestamp(long userId){
        try (var connection = Database.getConnection();
             var statement = connection.prepareStatement(SELECT_TIMESTAMP)) {
            statement.setLong(1, userId);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LOGGER.info("Selected timestamp for user {}", userId);
                    return resultSet.getTimestamp("timestamp");
                }
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select timestamp", e);
        }
        return null;
    }
}
