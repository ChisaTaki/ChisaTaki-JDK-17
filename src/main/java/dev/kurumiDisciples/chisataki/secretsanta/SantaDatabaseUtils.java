package dev.kurumidisciples.chisataki.secretsanta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class SantaDatabaseUtils {
    
    private static final String SELECT_USER = "SELECT * FROM secretsanta WHERE user_id = ?";
    private static final String INSERT_USER = "INSERT INTO secretsanta (user_id, preferred_gift, comments, chisataki) VALUES (?, ?, ?, ?)";
    private static final String COUNT_AMOUNT_OF_USERS = "SELECT COUNT(*) FROM secretsanta";
    private static final String ALL_USERS = "SELECT * FROM secretsanta";
    private static final String REMOVE_USER = "DELETE FROM secretsanta WHERE user_id = ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(SantaDatabaseUtils.class);


    public static boolean isUserRegistered(long userId){
       try (PreparedStatement statement = Database.createStatement(SELECT_USER)) {
            statement.setLong(1, userId);

            try (ResultSet set = statement.executeQuery()) {
                return set.next();
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when retrieving table", e);
            return false;
        }
    }

    /* Only comments can be null */
    public static void insertUser(long userId, String preferredGift, @Nullable String comments, String chisataki){
        try (PreparedStatement statement = Database.createStatement(INSERT_USER)) {
            statement.setLong(1, userId);
            statement.setString(2, preferredGift);
            statement.setString(3, comments);
            statement.setString(4, chisataki);

            statement.executeUpdate();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when inserting into the table", e);
        }
    }

    public static void insertUser(SantaStruct santaStruct){
        insertUser(santaStruct.getUserId(), santaStruct.getPreferredGift(), santaStruct.getComments(), santaStruct.getChisataki());
    }

    public static int countAmountOfUsers(){
        try (PreparedStatement statement = Database.createStatement(COUNT_AMOUNT_OF_USERS)) {
            try (ResultSet set = statement.executeQuery()) {
                return set.getInt(1);
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when counting table. 0 has been returned.", e);
            return 0;
        }
    }

    public static List<SantaStruct> getAllUsers(){
        try (PreparedStatement statement = Database.createStatement(ALL_USERS)) {
            try (ResultSet set = statement.executeQuery()) {
                return SantaStruct.fromResultSet(set);
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when retrieving table", e);
            return null;
        }
    }

    public static void removeUser(long userId){
        try (PreparedStatement statement = Database.createStatement(REMOVE_USER)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when removing from table", e);
        }
    }
    
}
