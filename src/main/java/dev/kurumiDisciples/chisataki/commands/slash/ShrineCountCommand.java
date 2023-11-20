package dev.kurumidisciples.chisataki.commands.slash;

import dev.kurumidisciples.chisataki.enums.EmojiEnum;
import dev.kurumidisciples.chisataki.enums.FilePathEnum;
import dev.kurumidisciples.chisataki.utils.FileUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ShrineCountCommand extends SlashCommand {

	public ShrineCountCommand() {
		super("count", "display the current shrine count", Permission.VIEW_AUDIT_LOGS);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		 event.deferReply(true).queue();

         int chisatoCount = FileUtils.getFileContent(FilePathEnum.CHISATOHEART.getFilePath()).getInt("count");
         int takinaCount = FileUtils.getFileContent(FilePathEnum.SAKANA.getFilePath()).getInt("count");

         String message = String.format("%s - **%d**\n%s - **%d**", EmojiEnum.CHISATO_HEART.getAsText(), chisatoCount,
                 EmojiEnum.SAKANA.getAsText(), takinaCount);
         event.getHook().editOriginal(message).queue();
	}

	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return true;
	}

	@Override
	public String getErrorMessage() {
		throw new UnsupportedOperationException();
	}
}
