package dev.kurumidisciples.chisataki.games.connect4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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


            switch (response){
                case "deny":
                    logger.info("user {} rejected a connect 4 request", event.getMember().getId());
                    event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);
                case "accept":

            }
        });
    }

    private static char[][] createBoard(int rows, int cols) {

        char[][] board = new char[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                board[row][col] = '.';
            }
        }

        return board;
    }

    private static String renderBoard(char[][] board) {

        StringBuilder output = new StringBuilder();

        for (int row = 0; row < 6; row++) {

            for (int col = 0; col < 7; col++) {

                char cell = board[row][col];

                if (cell == 'R') {
                    output.append("🔴");
                } else if (cell == 'Y') {
                    output.append("🟡");
                } else {
                    output.append("⚪");
                }

            }

            output.append("\n");
        }

        // Add column indicators at the bottom
        output.append("1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣");

        return output.toString();
    }

    // discord can only handle 5 buttons per action row
    private static Button[] createDropButtons(int numberOfButtons){
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
