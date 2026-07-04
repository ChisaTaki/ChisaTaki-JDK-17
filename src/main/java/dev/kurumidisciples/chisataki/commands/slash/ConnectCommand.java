package dev.kurumidisciples.chisataki.commands.slash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.games.connect4.enums.ConnectPieces;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ConnectCommand extends SlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(ConnectCommand.class);
    
    public ConnectCommand(){
        super("connect4", "Play a game of connect 4 with Takina and Chisato");
        subcommands.add(new SubcommandData("multiplayer", "play with a server member")
            .addOption(OptionType.USER, "user", "The member you want to play with", true));
    }

    @SuppressWarnings("null")
    @Override
    public void execute(SlashCommandInteractionEvent event){
        String subcommandName = event.getSubcommandName();

        event.deferReply(true).queue();

        OptionMapping opponentOption = event.getOption("user");

        switch (subcommandName) {
            case "multiplayer":
            if (isValidMemberToChallenege(event.getMember(), opponentOption.getAsMember())){
                event.getHook().editOriginal("Select a game piece:")
                    .setComponents(ActionRow.of(choiceMenu(event.getMember(), opponentOption.getAsMember())));
            }
        }
            

    }

    private static StringSelectMenu choiceMenu(Member player1, Member player2){
        return StringSelectMenu.create("menu:C4-" + player1.getId() + "-" + player2.getId())
            .setPlaceholder("Choose your character!")
            .addOption("Sakana", "s", ConnectPieces.TAKINA.getEmoji())
            .addOption("Chianago", "c", ConnectPieces.CHISATO.getEmoji())
            .setRequiredRange(1, 1)
            .build();

    }

    private static boolean isValidMemberToChallenege(Member player, Member opponent){
        if (IgnoreCommand.isMemberIgnored(opponent.getId()) || 
                opponent.getId().equals(player.getId()) || 
                opponent.getUser().isBot()) return false;
        return true;
    }

}
