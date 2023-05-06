package dev.kurumiDisciples.chisataki.commands.slash;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

import dev.kurumiDisciples.chisataki.utils.FileUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class IgnoreCommand extends SlashCommand {
	private static final String FILE_PATH = "data/ignore.json";

	public IgnoreCommand() {
		super("ignore-me", "make the bot ignore you from its shenanigans");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		
		JsonArray updatedArray;
		String message;
		
		String memberId = event.getMember().getId();
		if (isMemberIgnored(memberId)) {
			updatedArray = removeMember(memberId);
			message = "You are now included in ChisaTaki's interactions";
		} else {
			updatedArray = insertMember(memberId);
			message = "You are now excluded from ChisaTaki's interactions";
		}

		JsonObject updatedJson = Json.createObjectBuilder().add("ignore", updatedArray).build();
		FileUtils.updateFileContent(FILE_PATH, updatedJson);

		event.getHook().editOriginal(message).queue();
	}
	
	public static boolean isMemberIgnored(String memberId){
		JsonArray ignoreList = getIgnoreList();
		for (int i = 0; i < ignoreList.size(); i++){
			if (ignoreList.getString(i).equals(memberId))
				return true;
		}
		return false;
	}

	private static JsonArray removeMember(String memberId) {
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		JsonArray list = getIgnoreList();
		
		for (int i = 0; i < list.size(); i++){
			if (!list.getString(i).equals(memberId))
				jsonArray.add(list.getString(i));
		}
		
		return jsonArray.build();
	}
	
	private static JsonArray insertMember(String memberId) {
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		JsonArray previousList = getIgnoreList();
		
		for (JsonValue value : previousList){
			jsonArray.add(value);
		}
		jsonArray.add(memberId);
		
		return jsonArray.build();
	}

	private static JsonArray getIgnoreList() {
		return FileUtils.getFileContentArray(FILE_PATH, "ignore");
	}
}
