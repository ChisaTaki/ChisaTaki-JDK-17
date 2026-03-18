package dev.kurumidisciples.chisataki.radiata.database;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.PreparedStatementFactory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViolationRecordUtils {
    
    static final Logger logger = LoggerFactory.getLogger(ViolationRecordUtils.class);

    static final String INSERT_VIOLATION_RECORD = "INSERT INTO radiata_violation_records (user_id, image_url, moderation_result) VALUES (?, ?, ?);";
    static final String SELECT_VIOLATION_RECORDS_BY_USER = "SELECT * FROM radiata_violation_records WHERE user_id = ?;";


    public static boolean insertViolationRecord(MessageReceivedEvent event, String imageUrl, String moderationResult) {
        try (PreparedStatement statement = Database.createStatement(INSERT_VIOLATION_RECORD)) {
            statement.setString(1, event.getAuthor().getId());
            statement.setString(2, imageUrl);
            statement.setString(3, moderationResult);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("Failed to insert violation record for user {}[{}]", event.getAuthor().getName(), event.getAuthor().getId(), e);
            return false;
        }
    }

    public static ViolationRecord getViolationRecordsByUser(String userId) {
        try (PreparedStatement statement = Database.createStatement(SELECT_VIOLATION_RECORDS_BY_USER)) {
            statement.setString(1, userId);
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return ViolationRecord.fromResultSet(resultSet);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve violation records for user with ID {}", userId, e);
            return null;
        }
        return null;
    }
}
