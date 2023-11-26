package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumidisciples.chisataki.secretsanta.SantaComponents;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SecretSantaCommand extends SlashCommand{
    
    public SecretSantaCommand() {
        super("secretsanta", "Send Secret Santa message to members", Permission.MODERATE_MEMBERS);
        this.options = List.of(
            new OptionData(OptionType.STRING, "message", "message to send", true),
            new OptionData(OptionType.CHANNEL, "channel", "channel to send message to", true),
            new OptionData(OptionType.INTEGER, "time", "Unix time to send the secret santa message at", true)
        );
    }



    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        
        String message = event.getOption("message").getAsString();

        event.getOption("channel").getAsChannel().asTextChannel()
        .sendMessage(message).setActionRow(SantaComponents.createButton()).queue();
    }

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        return true;
    }
}
