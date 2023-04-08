package dev.kurumiDisciples.chisataki.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumiDisciples.chisataki.commands.message.MessageCommand;
import dev.kurumiDisciples.chisataki.commands.slash.PercentageBuilder;
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
		wrapCommands(PercentageBuilder.buildCommands());
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
//				Commands
//				.slash("embed", "create an embed message (mod only)")
//				/* the channel option is requried for the embed command to work
//				 */
//				.addOption(OptionType.CHANNEL, "channel",
//						"the channel to post to", /* required */ true)
//
//				.addOption(OptionType.STRING, "title", "set the title")
//				.addOption(
//						OptionType.STRING, "description", "set the description")
//				.addOption(OptionType.STRING, "author", "set the author")
//				.addOption(OptionType.STRING, "footer", "set the footer")
//				.addOption(
//						OptionType.INTEGER, "color", "int code for the color")
//				/* This sets it so that only moderators can see and use this
//                   command */
//				/* Normal members will never be able to use this what so ever */
//				/* Perm needed to see command = MODERATE_MEMBERS */
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.CREATE_PRIVATE_THREADS)),
//				/* Adding the gif command here because why not */
//				Commands
//				.slash("gif", /* description for the command */ "send a gif")
//				.addOption(OptionType.USER, "hug", "user to hug"),
//				Commands.message("Delete Bot Message")
//				.setGuildOnly(true)
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				Commands.slash("count", "display the current shrine count")
//				.setGuildOnly(true)
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS)),
//				/* A work in progress slash command for rock paper scissors */
//				Commands.slash("rps", "play rock paper scissors")
//				.setGuildOnly(true)
//				.addSubcommands(List.of(
//						new SubcommandData("singleplayer", "play with the bot"),
//						new SubcommandData("multiplayer", "play with other members")
//						.addOption(OptionType.USER, "challenge",
//								"user to play with", true))),
//				Commands
//				.slash(
//						"ignore-me", "make the bot ignore you from its shenanigans")
//				.setGuildOnly(true),
//				Commands.slash("test-send", "nothing here")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS))
//				.setGuildOnly(true),
//
//				Commands.slash("debug", "brings up debugging info for the bot")
//				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
//						Permission.VIEW_AUDIT_LOGS))
//				.addSubcommands(List.of(new SubcommandData("performance",
//						"brings up performance stats"),
//						new SubcommandData("cache", "view the system cache")
//						.addOption(OptionType.STRING, "message-id",
//								"grab message id"))),
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
//				Commands.slash("cuddle", "cuddle someone")
//				.addOption(OptionType.USER, "user", "person to cuddle",
//						/* required */ true)
//				.setGuildOnly(true),
//				Commands.slash("bite", "bite someone")
//				.addOption(OptionType.USER, "user", "person to bite",
//						/* required */ true),
//				Commands.slash("kiss", "kiss someone")
//				.addOption(OptionType.USER, "user", "person to kiss",
//						/* required */ true)
//				.setGuildOnly(true),
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