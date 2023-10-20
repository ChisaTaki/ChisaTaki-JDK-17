package dev.kurumiDisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.tictactoe.TTTChoice;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Member;

public class TTTCommand extends SlashCommand {
    
    public TTTCommand() {
        super("tic-tac-toe", "play tic tac toe");
        this.subcommands = List.of(
            new SubcommandData("multiplayer", "Request a match with another member").addOption(OptionType.USER, "opponent", "The opponent to challenge", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if (event.getSubcommandName().equals("multiplayer")){
            //check if member is not in ingnore list
            
            OptionMapping opponentOption = event.getOption("opponent");
            if (IgnoreCommand.isMemberIgnored(opponentOption.getAsMember().getId())) {
               event.getHook().editOriginal("This member wishes not to be challenged by other members").queue();
            } else {
               event.getHook().editOriginal("Please select your Game Piece first!").setActionRow(generateChoiceMenu(event.getMember(), opponentOption.getAsMember())).queue();
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

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        /* Temporary method */
        return event.getChannel().getId().equals(ChannelEnum.BOT_HOUSE.getId());
    }
}
