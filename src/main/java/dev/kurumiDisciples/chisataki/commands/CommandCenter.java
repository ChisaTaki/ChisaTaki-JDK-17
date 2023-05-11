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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandCenter extends ListenerAdapter {
    private ExecutorService commandExecutor = Executors.newCachedThreadPool();

	private Map<String, SlashCommand> slashCommandsMap = new HashMap<>();
	private Map<String, UserCommand> userCommandsMap = new HashMap<>();
	private Map<String, MessageCommand> messageCommandsMap = new HashMap<>();

	public CommandCenter() {
		wrapCommands(CommandBuilder.buildSlashCommands());
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
		
		// To refactor:
//		jda.updateCommands()
//		.addCommands(
//				Commands.message("Delete Bot Message")
//				.setGuildOnly(true)
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				Commands.slash("rps", "play rock paper scissors")
//				.setGuildOnly(true)
//				.addSubcommands(List.of(
//						new SubcommandData("singleplayer", "play with the bot"),
//						new SubcommandData("multiplayer", "play with other members")
//						.addOption(OptionType.USER, "challenge",
//								"user to play with", true))),
//				Commands.slash("test-send", "nothing here")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS))
//				.setGuildOnly(true),
//
//				Commands.user("Remove Chisato's Solider Role")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				Commands.user("Remove Takina's Sakana Role")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//
//				Commands.user("Give User Shareholder Role")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				Commands.slash("test-image", "image generation test")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				Commands
//				.slash("start-music",
//						"calls the bot to start playing music on file")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				Commands.slash("stop-music", "calls the bot to stop playing music"))
//		.queue();
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