package dev.kurumidisciples.chisataki.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;
import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;
import net.dv8tion.jda.api.entities.User;

public class CooldownUtils implements GenericDatabaseTable {

	private static final Logger LOGGER = LoggerFactory.getLogger(CooldownUtils.class);

	public static final String INSERT_USER_TIMESTAMP = "INSERT INTO cooldowns (user_id, timestamp) VALUES (?, ?);";
	public static final String UPDATE_USER_TIMESTAMP = "UPDATE cooldowns SET timestamp = ? WHERE user_id = ?;";
	public static final String SELECT_USER_TIMESTAMP = "SELECT * FROM cooldowns WHERE user_id = ?;";

	public static long DEFAULT_MINS = 60;
	
	public static boolean hasCooldownElapsed(LocalDateTime previousDateTime) {
		return hasCooldownElapsed(previousDateTime, CooldownUtils.DEFAULT_MINS);
	}
	
	public static boolean hasCooldownElapsed(LocalDateTime previousDateTime, long cooldownInMins) {
		if (previousDateTime == null) {
			return true;
		}
		
		long secondsElapsed = ChronoUnit.SECONDS.between(previousDateTime, LocalDateTime.now());
		int minutesElapsed = (int) (secondsElapsed / 60);
		return minutesElapsed >= cooldownInMins;
	}
	
	/**
	 * This function assumes the json file has the following structure:
	 * { <User Id1>: <timeInEpochSecond1>, <User Id2>: <timeInEpochSecond2> }
	 */
	/**
	 * Retrieves the previous timestamp for the given user.
	 *
	 * @param user The user to retrieve the previous timestamp for.
	 * @return The previous timestamp as a LocalDateTime object, or null if it couldn't be retrieved.
	 */
	public static LocalDateTime getPreviousDateTime(User user) {
		try (PreparedStatement statement = Database.createStatement(SELECT_USER_TIMESTAMP)) {
			statement.setLong(1, user.getIdLong());
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				LocalDateTime previousDateTime = LocalDateTime.ofEpochSecond(set.getLong("timestamp"), 0, ZoneOffset.UTC);
				set.close(); // Close the ResultSet after retrieving the timestamp value
				return previousDateTime;
			}
		} catch (SQLException | InitializationException e) {
			LOGGER.error("Failed to get previous timestamp for user {} for reason: {}", user.getId(), e.getMessage());
		}
		return null;
	}
	
	
	public static void updateUsersCooldown(User user) {
		//update value in database 
		//if user doesn't exist, insert new row
		//if user exists, update timestamp
			try (PreparedStatement statement = Database.createStatement(SELECT_USER_TIMESTAMP)) {
				statement.setLong(1, user.getIdLong());
				ResultSet set = statement.executeQuery();
				if (set.next()) {
					try (PreparedStatement updateStatement = Database.createStatement(UPDATE_USER_TIMESTAMP)) {
						updateStatement.setLong(1, user.getIdLong());
						updateStatement.setLong(2, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
						updateStatement.executeUpdate();
					}
				} else {
					try (PreparedStatement insertStatement = Database.createStatement(INSERT_USER_TIMESTAMP)) {
						insertStatement.setLong(1, user.getIdLong());
						insertStatement.setLong(2, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
						insertStatement.executeUpdate();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	@Override
	public String getTableName(){
		return "cooldowns";
	}

	@Override
	public HashMap<String, Integer> getDefinedColumns(){
		HashMap<String, Integer> columns = new HashMap<>();
		columns.put("user_id", Types.BIGINT);
		columns.put("timestamp", Types.BIGINT);
		return columns;
	}

	@Override
	public String getPrimaryKey(){
		return "user_id";
	}

	@Override
	public Integer getPrimaryKeyType(){
		return Types.BIGINT;
	}

	@Override
	public String getTableSchema(){
		return "CREATE TABLE IF NOT EXISTS cooldowns ("
			+ "user_id BIGINT NOT NULL,"
			+ "timestamp BIGINT NOT NULL,"
			+ "PRIMARY KEY (user_id)"
			+ ");";
	}
}