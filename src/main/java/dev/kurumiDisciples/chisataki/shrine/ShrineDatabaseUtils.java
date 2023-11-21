package dev.kurumidisciples.chisataki.shrine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class ShrineDatabaseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShrineDatabaseUtils.class);

    private static final String CHISATO_SHRINE_NAME = "chisato";
    private static final String TAKINA_SHRINE_NAME = "takina";


    private static final String UPDATE_COUNT = "UPDATE shrines SET count = count + 1 WHERE name = ?;";


    private static final String GET_COUNT = "SELECT count FROM shrines WHERE name = ?;";



    public static int getChisatoShrineCount() {
        return getShrineCount(CHISATO_SHRINE_NAME);
    }

    public static int getTakinaShrineCount() {
        return getShrineCount(TAKINA_SHRINE_NAME);
    }

    public static void incrementChisatoShrineCount() {
        incrementShrineCount(CHISATO_SHRINE_NAME);
    }

    public static void incrementTakinaShrineCount() {
        incrementShrineCount(TAKINA_SHRINE_NAME);
    }

    private static int getShrineCount(String shrineName) {
        try {
            Connection connection = Database.getConnection();

            int count = 0;
            PreparedStatement statement = connection.prepareStatement(GET_COUNT);
            statement.setString(1, shrineName);
            if (statement.execute()) {
                statement.getResultSet().next();
                count = statement.getResultSet().getInt("count");
            }
            statement.close();
            return count;
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to get shrine count", e);
            return -1;
        }
    }

    public static void incrementShrineCount(String shrineName) {
        try {
            Connection connection = Database.getConnection();

            PreparedStatement statement = connection.prepareStatement(UPDATE_COUNT);
            statement.setString(1, shrineName);
            statement.execute();
            statement.close();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to increment shrine count", e);
        }
    }
}
