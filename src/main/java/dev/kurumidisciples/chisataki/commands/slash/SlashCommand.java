package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import dev.kurumidisciples.chisataki.commands.CommandWrapper;
import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class SlashCommand extends CommandWrapper {
	
	protected List<SubcommandData> subcommands;
	
	public SlashCommand(String name, String description) {
		super(Type.SLASH, name);
		this.description = description;
	}

	public SlashCommand(String name, String description, Permission permission) {
		this(name, description);
		this.permission = DefaultMemberPermissions.enabledFor(permission);
	}
	
	@Override
	public CommandData build() {
		SlashCommandData commandData = Commands.slash(this.name, this.description).setGuildOnly(true);
		
		if (CollectionUtils.isNotEmpty(this.subcommands)) {
			commandData.addSubcommands(this.subcommands);
		}
		
		if (CollectionUtils.isNotEmpty(this.options)) {
			commandData.addOptions(this.options);
		}
		
		if (this.permission != null) {
			commandData.setDefaultPermissions(this.permission);
		}
		
		return commandData;
	}

	public abstract void execute(SlashCommandInteractionEvent event);
	
	/**
	 * Additional logic to determine if the command in question should be executed in the listener.
	 * Note: Default behaviour may be overriden.
	 */
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		String channelId = event.getChannel().getId();
		return ChannelEnum.areSlashCommandsAllowed(channelId);
	}

	/**
	 * Error message to display when {@link #isAllowed(SlashCommandInteractionEvent)} is false
	 */
	public String getErrorMessage() {
		return "This command can only be used in " + ChannelEnum.CHISATAKI.getAsMention();
	}

	/**
	 * Error message to display when member selected is in the ignore list
	 */
	public String getIgnoreMessage() {
		return "> The member you selected wishes not to be pinged by other members.\n> You may pick someone else and try again.";
	}
}
