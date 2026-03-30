package dev.kurumidisciples.chisataki.games.connect4;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectInteraction extends ListenerAdapter {

    private final static ExecutorService c4Executor = Executors.newCachedThreadPool();

    private final static Logger logger = LoggerFactory.getLogger(ConnectInteraction.class);


    // This alerts the opponent player and asks them if they want to play
    @Override
    public void onStringSelectInteraction(@SuppressWarnings("null") StringSelectInteractionEvent event){
        c4Executor.execute(() -> {
            if (!event.getCustomId().startsWith("menu:C4-")) return;

            logger.debug("Connect 4 string select menu interaction fired");

            Member opponent = event.getGuild().getMember(UserSnowflake.fromId(getOpponent(event.getCustomId())));

            event.getChannel().asTextChannel().sendMessage("[TEMP MESSAGE] " + opponent.getAsMention() + " will you like to play connect 4?")
                .setComponents(ActionRow.of(
                    Button.danger("button:C4-deny-" + event.getMember().getId() + "-" + opponent.getId(), Emoji.fromUnicode("U+274C")),
                    Button.success("button:C4-accept-" + event.getMember().getId() + "-" + opponent.getId(), Emoji.fromUnicode("☑️"))
                )).queue();
            logger.debug("Request message sent to channel");
        });
    }

    // add functionality that prevents other users from interacting with it
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        c4Executor.execute(() -> {
            if (event.getMember().getId().equals(event.getButton().getCustomId().split("-")[3])) {
                event.reply("You cannot interact with this button.").setEphemeral(true).queue();
            }

            String response = getDenyOrAccept(event.getCustomId());
        });
    }

    private static HashMap<Integer, String> createBoard() {
        // implement later
        return null;
    }

    private static String getOpponent(String customId){
        return customId.split("-")[2];
    }
    
    // to be used only for the request buttons
    private static String getDenyOrAccept(String customId){
        return customId.split("-")[1];
    }
    
}
