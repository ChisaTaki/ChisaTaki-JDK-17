package dev.kurumidisciples.chisataki.commands.slash;

import dev.kurumidisciples.chisataki.listeners.WelcomeInteraction;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SuppressWarnings("null")
public class TestImageCommand extends SlashCommand {

	final static Logger logger = LoggerFactory.getLogger(TestImageCommand.class);

	public TestImageCommand() {
		super("test-image", "image generation test", Permission.VIEW_AUDIT_LOGS);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		try {
			int guildSize = event.getGuild().getMembers().size();
			
			event.getHook().sendMessageComponents(WelcomeInteraction.getWelcomeContainer(event.getMember(), guildSize)).useComponentsV2().queue();
		} catch (IOException e) {
			logger.error("Failed to create welcome message for " + event.getMember().getUser().getName(), e);
			event.getHook().editOriginal("Generation Failed. See console").queue();
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
