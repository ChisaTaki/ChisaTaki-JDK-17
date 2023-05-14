package dev.kurumiDisciples.chisataki.commands.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumiDisciples.chisataki.commands.CommandWrapper;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class MessageCommand extends CommandWrapper {
    final static Logger logger = LoggerFactory.getLogger(MessageCommand.class);
    
	public MessageCommand(String name) {
		super(Type.MESSAGE, name);
	}

	@Override
	public CommandData build() {
		CommandData commandData = Commands.message(this.name).setGuildOnly(true);
		if (this.permission != null) {
			commandData.setDefaultPermissions(this.permission);
		}
		return commandData;
	}

	public abstract void execute(MessageContextInteractionEvent event);
}