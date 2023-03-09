package dev.kurumiDisciples.chisataki.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class RuntimeUtils {

	private static final String FORMAT = "%s %s";
	private static final long MEGABYTE_FACTOR = 1024L * 1024L;
	private static final DecimalFormat ROUNDED_DOUBLE_DECIMALFORMAT;
	private static final String MIB = "MiB";

	static {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		ROUNDED_DOUBLE_DECIMALFORMAT = new DecimalFormat("####0.00", otherSymbols);
		ROUNDED_DOUBLE_DECIMALFORMAT.setGroupingUsed(false);
	}

	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static long getUsedMemory() {
		return getMaxMemory() - getFreeMemory();
	}

	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public static String getTotalMemoryInMiB() {
		double totalMiB = bytesToMiB(getTotalMemory());
		return String.format(FORMAT, ROUNDED_DOUBLE_DECIMALFORMAT.format(totalMiB), MIB);
	}

	public static String getFreeMemoryInMiB() {
		double freeMiB = bytesToMiB(getFreeMemory());
		return String.format(FORMAT, ROUNDED_DOUBLE_DECIMALFORMAT.format(freeMiB), MIB);
	}

	public static String getUsedMemoryInMiB() {
		double usedMiB = bytesToMiB(getUsedMemory());
		return String.format(FORMAT, ROUNDED_DOUBLE_DECIMALFORMAT.format(usedMiB), MIB);
	}

	public static String getMaxMemoryInMiB() {
		double maxMiB = bytesToMiB(getMaxMemory());
		return String.format(FORMAT, ROUNDED_DOUBLE_DECIMALFORMAT.format(maxMiB), MIB);
	}

	public static double getPercentageUsed() {
		return ((double) getUsedMemory() / getMaxMemory()) * 100;
	}

	public static String getPercentageUsedFormatted() {
		double usedPercentage = getPercentageUsed();
		return ROUNDED_DOUBLE_DECIMALFORMAT.format(usedPercentage) + "%";
	}

	private static double bytesToMiB(long bytes) {
		return ((double) bytes / MEGABYTE_FACTOR);
	}

	public static String getSystemInformation() {
		return String.format("SystemInfo=Current heap:%s; Used:%s; Free:%s; Maximum Heap:%s; Percentage Used:%s",
				getTotalMemoryInMiB(),
				getUsedMemoryInMiB(),
				getFreeMemoryInMiB(),
				getMaxMemoryInMiB(),
				getPercentageUsedFormatted());
	}
}