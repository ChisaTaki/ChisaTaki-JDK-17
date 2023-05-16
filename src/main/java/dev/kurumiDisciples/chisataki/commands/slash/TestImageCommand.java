package dev.kurumiDisciples.chisataki.commands.slash;

import dev.kurumiDisciples.chisataki.listeners.WelcomeInteraction;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

public class TestImageCommand extends SlashCommand {

	public TestImageCommand() {
		super("test-image", "image generation test", Permission.VIEW_AUDIT_LOGS);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		try {
			int guildSize = event.getGuild().getMembers().size();
			FileUpload welcomeGif = FileUpload.fromData(WelcomeInteraction.createWelcomeGif(event.getMember()), "welcome.gif");
			
			event.getHook().editOriginal("Hello " + event.getMember().getAsMention() + "!")
							.setEmbeds(WelcomeInteraction.buildEmbed(guildSize))
							.setFiles(welcomeGif).queue(); 
		} catch (Exception e) {
			event.getHook().editOriginal("Generation Failed. See console").queue();
			e.printStackTrace();
		}
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
