package dev.kurumiDisciples.chisataki.listeners;

import dev.kurumiDisciples.chisataki.enums.EmojiEnum;
import dev.kurumiDisciples.chisataki.shrine.ShrineInteractionFactory;
import dev.kurumiDisciples.chisataki.shrine.ShrineInteractionHandler;
import dev.kurumiDisciples.chisataki.utils.FileUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShrineInteraction extends ListenerAdapter {

    private static final ExecutorService shrineExecutor = Executors.newCachedThreadPool();

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        shrineExecutor.submit(() -> handleSlashCommandInteraction(event));
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        shrineExecutor.submit(() -> handleShrineMessageReceived(event));
    }

    private void handleSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("count")) {
            event.deferReply(true).queue();

            int chisatoCount = FileUtils.getFileContent("data/chisatoHeart.json").getInt("count");
            int takinaCount = FileUtils.getFileContent("data/takina.json").getInt("count");

            String message = String.format("%s - **%d**\n%s - **%d**", EmojiEnum.CHISATO_HEART.getAsText(), chisatoCount,
                    EmojiEnum.SAKANA.getAsText(), takinaCount);
            event.getHook().editOriginal(message).queue();
        }
    }

    private void handleShrineMessageReceived(MessageReceivedEvent event) {
        ShrineInteractionHandler shrineHandler = ShrineInteractionFactory
                .getShrineInteractionHandler(event.getChannel().getId());
        if (shrineHandler != null) {
            shrineHandler.handleShrineInteraction(event);
        }
    }
}
