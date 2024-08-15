package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AssKickCommand extends SlashCommand {

    public AssKickCommand() {
        super("buttkick", "give someone a playful kick in the butt!");
        super.options = List.of(new OptionData(OptionType.USER, "user", "The user to kick in the butt", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
    }
}
