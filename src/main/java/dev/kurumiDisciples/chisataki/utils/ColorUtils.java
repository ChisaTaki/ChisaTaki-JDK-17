package dev.kurumiDisciples.chisataki.utils;

import java.awt.Color;
import java.util.regex.Pattern;

public class ColorUtils {
	public static final String DEFAULT_HEX = "#1E1F22";
	public static final Color PURPLE = new Color(144, 96, 233);
	
	private static final String HEX_FORMAT = "^#[0-9a-fA-F]{6}";
	
	public static boolean isValidHexCode(String hexCode) {
		return Pattern.matches(HEX_FORMAT, hexCode);
	}
}