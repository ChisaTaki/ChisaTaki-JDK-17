package dev.kurumidisciples.chisataki.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.introduction.IntroductionUtils;
import dev.kurumidisciples.chisataki.utils.MessageHistoryUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

public class IntroductionSearchCommand extends SlashCommand{

    private static final long INTRODUCTION_CHANNEL_ID = 1022467787790229564L;
    
    private static final Logger logger = LoggerFactory.getLogger(IntroductionSearchCommand.class);

    public IntroductionSearchCommand(){
        super("introduction-search", "Search the introduction channel.", Permission.VIEW_AUDIT_LOGS);
    }

    @Override
    @SuppressWarnings("null")
    public void execute(SlashCommandInteractionEvent event) {
        // This method can't really tell if the introduction is actually an introduction or not, but it will add it to the database if it's not already there and override the existing one if it is.

        event.reply("Pulling messages from the introduction channel...").queue();

        
        CompletableFuture<List<Message>> future = 
        MessageHistoryUtils.getAllChannelHistory(
            event.getGuild().getTextChannelById(INTRODUCTION_CHANNEL_ID)
            );

        future.orTimeout(5, TimeUnit.MINUTES)
        .thenAccept(messages-> {
            if (messages.isEmpty()) {
                event.getHook().editOriginal("No messages found. Exiting.").queue(message -> {
                    message.delete().queueAfter(30, TimeUnit.SECONDS);
                });
                return;
            }

            int addedCount = 0;
            for (Message message : messages) { 
               int success = IntroductionUtils.insertUserIntroduction(message.getAuthor().getIdLong(), message.getIdLong());
                if (success != -1) {
                     addedCount++;
                }
            }

            event.getHook().editOriginal("Successfully added " + addedCount + " messages to the database.").queue(message -> {
                message.delete().queueAfter(30, TimeUnit.MINUTES);
            });

        })
        .exceptionally(ex -> {
            if (ex instanceof TimeoutException) {
                event.getHook().editOriginal("Message retrieval timed out after 5 minutes. No action taken.").queue();
                logger.error("Future timed out before completion.", ex);
            } else {
                event.getHook().editOriginal("An error occurred while retrieving messages: " + ex.getMessage()).queue();
                logger.error("Couldn't complete future due to an error.", ex);
            }
            return null;
        });

    }

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        // Only allow the command to be used in the introduction channel
        return event.getChannel().getId().equals(ChannelEnum.BOT_HOUSE.getId());
    }
}
