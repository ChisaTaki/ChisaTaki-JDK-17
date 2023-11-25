package dev.kurumidisciples.chisataki.secretsanta.time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class SantaClock {

    private static final String GET_TIME = "SELECT time FROM santaTime";
    private static final String SET_TIME = "INSERT INTO santaTime (time) VALUES (?)";

    private static final Logger LOGGER = LoggerFactory.getLogger(SantaClock.class);

    static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

     static {
        createClock();
    }


    public static boolean isTimeSet(){
        try(PreparedStatement statement = Database.createStatement(GET_TIME)){
            try(ResultSet set = statement.executeQuery()){
                return set.next();
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when retrieving table", e);
            return false;
        }
    }

    public static void setTime(long time){
        try(PreparedStatement statement = Database.createStatement(SET_TIME)){
            statement.setLong(1, time);
            statement.executeUpdate();
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when inserting into the table", e);
        }
    }

    public static long getTime(){
        try(PreparedStatement statement = Database.createStatement(GET_TIME)){
            try(ResultSet set = statement.executeQuery()){
                if(set.next()){
                    return set.getLong("time");
                } else {
                    return -1;
                }
            }
        } catch (SQLException | InitializationException e) {
            LOGGER.error("An error occurred in SantaDatabaseUtils when retrieving table", e);
            return -1;
        }
    }

   
   public static void createClock() {
            executor.scheduleAtFixedRate(() -> {
                long currentTime = System.currentTimeMillis() / 1000;
                long expirationTime = getTime();

                if (currentTime > 0 && currentTime <= expirationTime) {
                    // Time has expired, execute the method that has yet to be implemented
                }
            }, 0, 1, TimeUnit.SECONDS);

    }

    public static void start(){
        return;
    }
}
