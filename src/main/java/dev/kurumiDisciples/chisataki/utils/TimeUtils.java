package dev.kurumidisciples.chisataki.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtils {
	
	public static long getCurrentEpochSecond() {
		return Instant.now().getEpochSecond();
	}
	
	public static LocalDateTime getLocalDateTimeFromEpochSecond(Long epochSecond) {
		if (epochSecond == null) {
			return null;
		}
		
		Instant instant = Instant.ofEpochSecond(epochSecond);
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
	
	public static long getUnixTimeIn10Minutes(long epochSecond) {
		return epochSecond + (10 * 60);
	}
}