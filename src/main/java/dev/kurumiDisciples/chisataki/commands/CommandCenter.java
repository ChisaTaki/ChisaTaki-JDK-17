package dev.kurumiDisciples.chisataki.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumiDisciples.chisataki.commands.message.MessageCommand;
import dev.kurumiDisciples.chisataki.commands.slash.SlashCommand;
import dev.kurumiDisciples.chisataki.commands.user.UserCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandCenter extends ListenerAdapter {
    private ExecutorService commandExecutor = Executors.newCachedThreadPool();

	private Map<String, SlashCommand> slashCommandsMap = new HashMap<>();
	private Map<String, UserCommand> userCommandsMap = new HashMap<>();
	private Map<String, MessageCommand> messageCommandsMap = new HashMap<>();

	public CommandCenter() {
		wrapCommands(CommandBuilder.buildSlashCommands());
		wrapCommands(CommandBuilder.buildUserCommands());
	}

	private void wrapCommands(CommandWrapper ...commands) {
		for (CommandWrapper command : commands) {
			if (command instanceof SlashCommand) {
				slashCommandsMap.put(command.getName(), (SlashCommand) command);
			} else if (command instanceof UserCommand) {
				userCommandsMap.put(command.getName(), (UserCommand) command);
			} else if (command instanceof MessageCommand) {
				messageCommandsMap.put(command.getName(), (MessageCommand) command);
			}
		}

	}

	private List<CommandData> unwrapCommands() {
		List<CommandData> commands = new ArrayList<>();

		for (CommandWrapper command : slashCommandsMap.values()) {
			commands.add(command.build());
		}

		for (CommandWrapper command : userCommandsMap.values()) {
			commands.add(command.build());
		}

		for (CommandWrapper command : messageCommandsMap.values()) {
			commands.add(command.build());
		}

		return commands;
	}

	public void addCommands(JDA jda) {
		jda.updateCommands().addCommands(unwrapCommands()).queue();
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		commandExecutor.execute(() -> handleSlashInteraction(event));
	}
	
	@Override
	public void onUserContextInteraction(UserContextInteractionEvent event) {
		commandExecutor.execute(() -> handleUserContextInteraction(event));
	}

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		commandExecutor.execute(() -> handleMessageContextInteraction(event));
	}

	private void handleSlashInteraction(SlashCommandInteractionEvent event) {
		SlashCommand command = slashCommandsMap.get(event.getName());
		if (command == null) {
			return;
		}
		
		if (!command.isAllowed(event)) {
			event.reply(command.getErrorMessage()).setEphemeral(true).queue();
			return;
		}
		
		command.execute(event);
	}
	
	private void handleUserContextInteraction(UserContextInteractionEvent event) {
		UserCommand command = userCommandsMap.get(event.getName());
		if (command == null) {
			return;
		}
		command.execute(event);
	}

	private void handleMessageContextInteraction(MessageContextInteractionEvent event) {
		MessageCommand command = messageCommandsMap.get(event.getName());
		if (command == null) {
			return;
		}
		command.execute(event);
	}
}