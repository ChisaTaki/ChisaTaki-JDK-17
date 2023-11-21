package dev.kurumidisciples.chisataki.commands.slash;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;
import dev.kurumidisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class IgnoreCommand extends SlashCommand implements GenericDatabaseTable {
	private static final String FILE_PATH = "data/ignore.json";

	private static final String GET_ALL_IGNORED_USERS = "SELECT * FROM ignore_list WHERE guild_id = ?";
	private static final String SELECT_IGNORED_USERS = "SELECT * FROM ignore_list WHERE guild_id = ? AND member_id = ?";
	private static final String INSERT_IGNORED_USERS = "INSERT INTO ignore_list (guild_id, member_id) VALUES (?, ?)";
	private static final String REMOVE_IGNORED_USER = "DELETE FROM ignore_list WHERE guild_id = ? AND member_id = ?";

	private static final Logger LOGGER = LoggerFactory.getLogger(IgnoreCommand.class);

	public IgnoreCommand() {
		super("ignore-me", "make the bot ignore you from its shenanigans");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		
		String message;
		
		String memberId = event.getMember().getId();
		if (isMemberIgnored(event.getGuild().getIdLong(), Long.parseLong(memberId))) {
			removeMember(event.getGuild().getIdLong(), Long.parseLong(memberId));
			message = "You are now included in ChisaTaki's interactions";
		} else {
			insertMember(event.getGuild().getIdLong(), Long.parseLong(memberId));
			message = "You are now excluded from ChisaTaki's interactions";
		}


		//JsonObject updatedJson = Json.createObjectBuilder().add("ignore", updatedArray).build();
		//FileUtils.updateFileContent(FILE_PATH, updatedJson);

		event.getHook().editOriginal(message).queue();
	}

	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return true;
	}
	
	/* FIX ME!!!!!!!!!! */
	public static boolean isMemberIgnored(long guildId, long memberId){
		try(PreparedStatement statement = Database.createStatement(SELECT_IGNORED_USERS)){
			statement.setLong(1, guildId);
			statement.setLong(2, memberId);

			ResultSet set = statement.executeQuery();
			return set.next();
		}
		catch (SQLException | InitializationException e){
			LOGGER.error("An error occured in IgnoreCommand when retriving table", e);
			return false;
		}
	}

	public static boolean isMemberIgnored(String memberId){
		/* Assume ChisaTaki server */
		return isMemberIgnored(1010078628761055234L, Long.parseLong(memberId));
	}
	private static void removeMember(long guildId, long memberId) {
		try(PreparedStatement statement = Database.createStatement(REMOVE_IGNORED_USER)){
			statement.setLong(1, guildId);
			statement.setLong(2, memberId);
			statement.executeUpdate();
			LOGGER.info("Removed member {} from ignore list", memberId);
		}
		catch (SQLException | InitializationException e){
			LOGGER.error("An error occured in IgnoreCommand when removing from table", e);
		}
	}
	
	private static void insertMember(long guildId, long memberId) {
		try(PreparedStatement statement = Database.createStatement(INSERT_IGNORED_USERS)){
			statement.setLong(1, guildId);
			statement.setLong(2, memberId);
			statement.executeUpdate();
		}
		catch (SQLException | InitializationException e){
			LOGGER.error("An error occured in IgnoreCommand when inserting into table", e);
		}
	}

	private static List<String> getIgnoreList() {
		List<String> list = new ArrayList<>();
		try(PreparedStatement statement = Database.createStatement(GET_ALL_IGNORED_USERS)){
			/* Assume ChisaTaki server */
			statement.setLong(1, 1010078628761055234L);
			ResultSet set = statement.executeQuery();
			while (set.next()){
				list.add(set.getString("member_id"));
			}
			return list;
		}
		catch (SQLException | InitializationException e){
			LOGGER.error("An error occured in IgnoreCommand when retriving table", e);
			return null;
		}
	}

	@Override
	public String getPrimaryKey(){
		return null;
	}

	@Override
	public String getTableName(){
		return "ignore_list";
	}

	@Override
	public HashMap<String, Integer> getDefinedColumns(){
		HashMap<String, Integer> columns = new HashMap<>();
		columns.put("guild_id", Types.VARCHAR);
		columns.put("member_id", Types.VARCHAR);
		return columns;
	}

	@Override
	public Integer getPrimaryKeyType(){
		return null;
	}

	@Override
	public String getTableSchema(){
		return "CREATE TABLE IF NOT EXISTS ignore_list (guild_id VARCHAR(20) NOT NULL, member_id VARCHAR(20) NOT NULL)";
	}
}
