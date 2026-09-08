package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumidisciples.chisataki.games.tictactoe.TTTChoice;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Member;
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
            event.reply("Choose your game piece! You go first against ChisaTaki.")
                .setEphemeral(true)
                .setComponents(ActionRow.of(generateChoiceMenu(event.getMember(), event.getGuild().getSelfMember())))
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
               event.getHook().editOriginal("Please select your Game Piece first!").setComponents(ActionRow.of(generateChoiceMenu(event.getMember(), opponentOption.getAsMember()))).queue();
            }
            
        }
    }

    private StringSelectMenu generateChoiceMenu(Member player1, Member player2){
        return StringSelectMenu.create("menu:TTT-" + player1.getId() + "-" + player2.getId())
        .setPlaceholder("Choose your game piece!")
        .addOption("X", "x", TTTChoice.X.getEmoji())
        .addOption("O", "o", TTTChoice.O.getEmoji())
        .setRequiredRange(1, 1)
        .build();
    }
}
