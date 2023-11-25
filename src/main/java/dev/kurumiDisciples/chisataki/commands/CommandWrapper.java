package dev.kurumidisciples.chisataki.commands;

import java.util.List;

import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class CommandWrapper {
	
	protected String name;
	protected String description;
	protected Type commandType;
	protected List<OptionData> options;
	protected DefaultMemberPermissions permission;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Type getCommandType() {
		return commandType;
	}
	
	public List<OptionData> getOptions() {
		return options;
	}

	public DefaultMemberPermissions getPermission() {
		return permission;
	}

	protected CommandWrapper(Type commandType, String name) {
		this.commandType = commandType;
		this.name = name;
	}

	public abstract CommandData build();
}