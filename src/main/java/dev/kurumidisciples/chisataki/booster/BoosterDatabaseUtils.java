package dev.kurumidisciples.chisataki.booster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

/**
 * This class contains utility methods for interacting with the boosters table in the database.
 * @apiNote This Feature was meant to replace the booster bot in the server, as such it should not have any other features besides ones that mimic the other bot.
 * @author Hacking Pancakez
 * @author Shim-kun
 */
public class BoosterDatabaseUtils {
    
    private static final String INSERT_BOOSTER = "INSERT INTO boosters (user_id, role_id) VALUES (?, ?);";
    private static final String DELETE_BOOSTER = "DELETE FROM boosters WHERE user_id = ?;";
    private static final String SELECT_BOOSTER = "SELECT * FROM boosters WHERE user_id = ?;";
    private static final String SELECT_ALL_BOOSTERS = "SELECT * FROM boosters;";
    private static final String UPDATE_ROLE_ID = "UPDATE boosters SET role_id = ? WHERE user_id = ?;";

    private static final Logger LOGGER = LoggerFactory.getLogger(BoosterDatabaseUtils.class);

    public static Booster insertBooster(long userId, long roleid){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_BOOSTER)) {
            statement.setLong(1, userId);
            statement.setLong(2, roleid);
            statement.executeUpdate();
            return new Booster(userId, String.valueOf(roleid));
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert booster", e);
        }
        return null;
    }
       

    public static void deleteBooster(long userId){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BOOSTER)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to delete booster", e);
        }
    }

    public static boolean isBooster(long userId){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BOOSTER)) {
            statement.setLong(1, userId);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to check if user is a booster", e);
        }
        return false;
    }

    public static void updateRoleId(long userId, long roleId){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ROLE_ID)) {
            statement.setLong(1, roleId);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to update booster role", e);
        }
    }

    public static Booster getBooster(long userId){
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BOOSTER)) {
            statement.setLong(1, userId);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Booster(resultSet.getLong("user_id"), String.valueOf(resultSet.getLong("role_id")));
                }
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to get booster", e);
        }
        return null;
    }

    protected static void updateRoleId(Booster booster){
        updateRoleId(booster.getUserId(), Long.parseLong(booster.getRoleId()));
    }

    public static List<Booster> getAllBoosters(){
        List<Booster> boosters = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BOOSTERS)) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    boosters.add(new Booster(resultSet.getLong("user_id"), String.valueOf(resultSet.getLong("role_id"))));
                }
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to get all boosters", e);
        }
        return boosters;
    }
}
