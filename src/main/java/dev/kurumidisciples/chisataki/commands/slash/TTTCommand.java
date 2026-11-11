package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumidisciples.chisataki.games.tictactoe.enums.TTTDifficulty;
import dev.kurumidisciples.chisataki.games.tictactoe.utils.TTTUtils;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

@SuppressWarnings("null")
public class TTTCommand extends SlashCommand {
    
    public TTTCommand() {
        super("tic-tac-toe", "play tic tac toe");
        this.subcommands = List.of(
            new SubcommandData("singleplayer", "Play against ChisaTaki"),
            new SubcommandData("multiplayer", "Request a match with another member").addOption(OptionType.USER, "opponent", "The opponent to challenge", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null || event.getMember() == null) {
            event.reply("This game can only be played in a server.").setEphemeral(true).queue();
            return;
        }

        if ("singleplayer".equals(event.getSubcommandName())) {
            event.reply("Choose a difficulty! You will always go first against ChisaTaki.")
                .setEphemeral(true)
                .setComponents(ActionRow.of(StringSelectMenu.create("menu:TTTDifficulty-"
                    + event.getUser().getId() + "-" + event.getJDA().getSelfUser().getId())
                    .setPlaceholder("Choose a difficulty")
                    .addOption("Easy", TTTDifficulty.EASY.getId(), "ChisaTaki picks random legal moves.")
                    .addOption("Medium", TTTDifficulty.MEDIUM.getId(), "ChisaTaki plays optimally 50% of the time.")
                    .addOption("Hard", TTTDifficulty.HARD.getId(), "ChisaTaki plays optimally. Nearly always a win or draw for ChisaTaki.")
                    .setRequiredRange(1,1)
                    .build()))
                .queue();
        } else if ("multiplayer".equals(event.getSubcommandName())) {
            //check if member is not in ingnore list
            event.deferReply(true).queue();
            OptionMapping opponentOption = event.getOption("opponent");
            
            if (opponentOption == null || opponentOption.getAsMember() == null) {
                event.getHook().editOriginal("Please choose a member of this server.").queue();
            } else if (IgnoreCommand.isMemberIgnored(opponentOption.getAsMember().getId())) {
               event.getHook().editOriginal("This member wishes not to be challenged by other members").queue();
            } else if (opponentOption.getAsMember().getId().equals(event.getMember().getId())){
                event.getHook().editOriginal("You cannot challenge yourself!").queue();
            } else if (opponentOption.getAsUser().isBot()){
                event.getHook().editOriginal("Please select singleplayer to play against a bot!").queue();
            } else {
               event.getHook().editOriginal("Please select your Game Piece first!").setComponents(ActionRow.of(TTTUtils.createChoiceMenu(event.getMember(), opponentOption.getAsMember(), null))).queue();
            }
            
        }
    }

}
