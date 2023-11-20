package dev.kurumidisciples.chisataki.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import net.dv8tion.jda.api.entities.User;

public class CooldownUtils {
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
	public static LocalDateTime getPreviousDateTime(String filePath, User user) {
		JsonObject usersCooldown = FileUtils.getFileContent(filePath);
		JsonNumber jsonNumber = usersCooldown.getJsonNumber(user.getId());
		
		Long epochSecond = jsonNumber != null ? jsonNumber.longValue() : null;
		return TimeUtils.getLocalDateTimeFromEpochSecond(epochSecond);
	}
	
	/**
	 * This function assumes the json file has the following structure:
	 * { <User Id1>: <timeInEpochSecond1>, <User Id2>: <timeInEpochSecond2> }
	 */
	public static void updateUsersCooldown(String filePath, User user) {
	    JsonObject usersCooldown = FileUtils.getFileContent(filePath);
	    JsonObjectBuilder builder = Json.createObjectBuilder(usersCooldown);
	    builder.add(user.getId(), TimeUtils.getCurrentEpochSecond());
	    JsonObject updatedJson = builder.build();
	    
	    FileUtils.updateFileContent(filePath, updatedJson);
	}
}