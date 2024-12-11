package dev.kurumidisciples.chisataki.secretsanta;

import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;

public class PairingsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PairingsUtil.class);
    
    private static final String INSERT_PAIRING = "INSERT INTO pairings (santa_id, target_id) VALUES (?, ?)";
    private static final String GET_PAIRING = "SELECT * FROM pairings WHERE santa_id = ?";


    public static void insertPairing(long santaId, long targetId){
        try(PreparedStatement statement = Database.createStatement(INSERT_PAIRING)){
            statement.setLong(1, santaId);
            statement.setLong(2, targetId);
            statement.execute();
        } catch (Exception e) {
            LOGGER.error("Failed to insert pairing into database", e);
        }
    }

    public static long getPairing(long santaId){
        try(PreparedStatement statement = Database.createStatement(GET_PAIRING)){
            statement.setLong(1, santaId);
            statement.execute();
            return statement.getResultSet().getLong("target_id");
        } catch (Exception e) {
            LOGGER.error("Failed to get pairing from database", e);
        }
        return -1;
    }
}
