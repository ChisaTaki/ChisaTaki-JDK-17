package dev.kurumidisciples.chisataki.commands.slash;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ShutdownCommand extends SlashCommand {

    public ShutdownCommand() {
        super("shutdown", "Shuts down the bot.", Permission.MANAGE_SERVER);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.getJDA().openPrivateChannelById(360241951804620800L).queue(privateChannel -> {
            privateChannel.sendMessage("Shutdown command executed by " + event.getUser().getAsTag()).queue();
        });
        event.reply("Shutting down...").setEphemeral(true).queue();
        event.getJDA().shutdown();
        System.exit(0);
    }
    
}
