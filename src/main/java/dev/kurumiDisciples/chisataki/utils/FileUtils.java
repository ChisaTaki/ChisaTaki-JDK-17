package dev.kurumiDisciples.chisataki.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	private static final int MAX_RETRIES = 50;

	public static JsonObject getFileContent(String filePath) {
		int retryCount = 0;

		try {
			while (retryCount < MAX_RETRIES) {
				try (FileReader reader = new FileReader(filePath);
					JsonReader jsonReader = Json.createReader(reader)) {
					JsonObject jsonObject = jsonReader.readObject();
					if (jsonObject != null) {
						return jsonObject;
					}
				} catch (JsonException e) {
					logger.debug("Error reading " + filePath);
				} catch (IllegalStateException illegal) {
					logger.debug("Illegal State Caught - Retrying");
				} catch (FileNotFoundException e) {
					logger.debug("File Not Found - Retrying");
				}

				retryCount++;
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	public static JsonArray getFileContentArray(String filePath, String arrayName) {
		return getFileContent(filePath).getJsonArray(arrayName);
	}

	public static void updateFileContent(String filePath, JsonObject updatedJson) {
		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write(updatedJson.toString());
		} catch (IOException e) {
			logger.error("Error updating file content", e);
		}
	}

	public static void writeFile(String filePath, String content) {
		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write(content);
		} catch (IOException e) {
			logger.error("Error writing file", e);
		}
	}
}
