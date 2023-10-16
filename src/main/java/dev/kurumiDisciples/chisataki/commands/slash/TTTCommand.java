package dev.kurumiDisciples.chisataki.commands.slash;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class TTTCommand extends SlashCommand {
    
    public TTTCommand() {
        super("tic-tac-toe", "play tic tac toe");
        this.subcommands = List.of(
            new SubcommandData("multiplayer", "Request a match with another member").addOption(OptionType.USER, "opponent", "The opponent to challenge", true)
        );
    }


    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if (event.getSubcommandName().equals("multiplayer")){
            //check if member is not in ingnore list
            
            OptionMapping opponentOption = event.getOption("opponent");
            if (IgnoreCommand.isMemberIgnored(opponentOption.getAsMember().getId())) {
               event.getHook().editOriginal("This member wishes not to be challenged by other members").queue();
               return;
            } else {
                event.getHook().editOriginal("This command is not available yet").queue();
            }
        }
    }
}
