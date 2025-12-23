package dev.kurumidisciples.chisataki.introduction;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntroductionUtils {

    private static final String IS_USER_INTRODUCED =
            "SELECT user_has_message(?) AS has_message;";

    private static final String INSERT_USER_INTRODUCTION =
            "INSERT INTO introduction_users (user_id, introduction_message_id) VALUES (?, ?);";

    private static final String UPDATE_USER_INTRODUCTION =
            "UPDATE introduction_users SET introduction_message_id = ? WHERE user_id = ?;";

    private static final String SELECT_USER_INTRODUCTION =
            "SELECT introduction_message_id FROM introduction_users WHERE user_id = ?;";

    private static final String REMOVE_USER_INTRODUCTION =
            "DELETE FROM introduction_users WHERE user_id = ?;";

    private static final Logger LOGGER = LoggerFactory.getLogger(IntroductionUtils.class);

    /*
     * Checks if a user has an introduction message set.
     * @param userId The ID of the user to check.
     */
    public static boolean isUserIntroduced(long userId) {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(IS_USER_INTRODUCED)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBoolean("has_message");
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Error checking if user {} has introduction message: ", userId, e);
            }
            return false;
        } catch (InitializationException | SQLException e) {
            LOGGER.error("Database connection error while checking if user {} has introduction message: ", userId, e);
            return false;
        }
    }

    /**
     * Insert or overwrite the user's introduction message.
     *
     * @param userId The user id.
     * @param introductionMessageId The message id for their introduction.
     * @return rows affected (1 on success), or -1 on failure
     */
    public static int insertUserIntroduction(long userId, long introductionMessageId) {
        try (Connection conn = Database.getConnection()) {

            // 1) Try UPDATE first
            try (PreparedStatement psUpdate = conn.prepareStatement(UPDATE_USER_INTRODUCTION)) {
                psUpdate.setLong(1, introductionMessageId);
                psUpdate.setLong(2, userId);

                int updated = psUpdate.executeUpdate();
                if (updated > 0) {
                    return updated; // overwritten existing entry
                }
            }

            // 2) If no row existed, INSERT it
            try (PreparedStatement psInsert = conn.prepareStatement(INSERT_USER_INTRODUCTION)) {
                psInsert.setLong(1, userId);
                psInsert.setLong(2, introductionMessageId);
                return psInsert.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.error("Error inserting/updating user {} introduction: ", userId, e);
            return -1;
        } catch (InitializationException e) {
            LOGGER.error("Database connection error while inserting/updating user {} introduction: ", userId, e);
            return -1;
        }
    }

    public static Long getUserIntroductionMessageId(long userId) {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SELECT_USER_INTRODUCTION)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("introduction_message_id");
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Error selecting user {} introduction message ID: ", userId, e);
            }
        } catch (InitializationException | SQLException e) {
            LOGGER.error("Database connection error while selecting user {} introduction message ID: ", userId, e);
        }
        return null;
    }

    public static boolean removeUserIntroduction(long userId) {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(REMOVE_USER_INTRODUCTION)) {
                ps.setLong(1, userId);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                LOGGER.error("Error removing user {} introduction: ", userId, e);
                return false;
            }
        } catch (InitializationException | SQLException e) {
            LOGGER.error("Database connection error while removing user {} introduction: ", userId, e);
            return false;
        }
    }
}
