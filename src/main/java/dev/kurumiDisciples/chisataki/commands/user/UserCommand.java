package dev.kurumiDisciples.chisataki.commands.user;

import dev.kurumiDisciples.chisataki.commands.CommandWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class UserCommand extends CommandWrapper {
	public UserCommand(String name, Permission permission) {
		super(Type.USER, name);
		this.permission = DefaultMemberPermissions.enabledFor(permission);
	}

	@Override
	public CommandData build() {
		CommandData commandData = Commands.user(this.name).setGuildOnly(true);
		if (this.permission != null) {
			commandData.setDefaultPermissions(this.permission);
		}
		return commandData;
	}

	public abstract void execute(UserContextInteractionEvent event);
}