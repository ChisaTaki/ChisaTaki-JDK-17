package dev.kurumidisciples.chisataki.quotes.command;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.imageio.ImageIO;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.commands.message.MessageCommand;
import dev.kurumidisciples.chisataki.quotes.QuoteSettings;
import dev.kurumidisciples.chisataki.quotes.QuoteSettingsUtils;
import dev.kurumidisciples.chisataki.quotes.QuoteThreadResolver;
import dev.kurumidisciples.chisataki.quotes.image.QuoteImageGenerator;
import dev.kurumidisciples.chisataki.quotes.quota.QuoteQuotaUtils;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

@SuppressWarnings("null")
public class QuoteContextCommand extends MessageCommand  {

    final static Logger logger = LoggerFactory.getLogger(QuoteContextCommand.class);
    private static final long QUOTES_THREAD_ID = 1221538772785561600L;
    
    public QuoteContextCommand(){
        super("Make it a Quote!");
    }

    @Override
    public void execute(MessageContextInteractionEvent event) {
        if (event.getGuild() == null || event.getMember() == null) {
            event.reply("Quotes can only be generated in a server.").setEphemeral(true).queue();
            return;
        }
        event.deferReply(true).queue(hook -> generateQuote(event));
    }

    private void generateQuote(MessageContextInteractionEvent event) {
        if (event.getTarget().getContentRaw().length() > 2000) {
            event.getHook().editOriginal("The message is too long to be a quote!").queue();
            return;
        }

        QuoteSettings settings = QuoteSettingsUtils.selectSettings(event.getGuild().getId());

        if (settings == null) {
            logger.warn("Quote settings were not found in database for server {}", event.getGuild().getId());
            event.getHook().editOriginal("No settings found for this server! Please contact the moderators.").queue();
            return;
        } 

        if(hasUserExceededQuota(event.getUser().getIdLong())) {
            event.getHook().editOriginal("Please wait 10 seconds before generating another quote.").queue();
            return;
        }

        if (hasConflicts(event, settings, event.getMember())) {
            return;
        }

        if (withinModerationCategory(event)) {
            event.getHook().editOriginal("Quotes can not be generated in the moderation category.").queue();
            return;
        }

        CompletableFuture.completedFuture(event.getGuild())
            .thenCompose(guild -> QuoteThreadResolver.resolve(guild, QUOTES_THREAD_ID))
            .thenCompose(thread -> {
                if (thread.isLocked()) {
                    return CompletableFuture.<Message>failedFuture(new IllegalStateException("The quotes thread is locked."));
                }
                // Discord automatically unarchives an unlocked thread when a message is sent.
                return thread.sendMessage("*Generating Quote...*").submit();
            })
            .thenCompose(message -> finishQuote(event, settings, message))
            .whenComplete((message, failure) -> {
                if (failure != null) {
                    logger.error("Could not generate a quote in thread {}", QUOTES_THREAD_ID, failure);
                    event.getHook().editOriginal("I couldn't post the quote. Please ask a moderator to check that the quotes thread exists, is unlocked, and that I can view it, read its history, send messages in threads, and attach files.").queue();
                } else {
                    event.getHook().editOriginal("A quote has been generated for you! " + message.getJumpUrl()).queue();
                }
            });
    }

    private CompletableFuture<Message> finishQuote(MessageContextInteractionEvent event, QuoteSettings settings,
            Message placeholder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BufferedImage quoteImage = QuoteImageGenerator.generateQuoteImage(settings, event.getTarget());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (!ImageIO.write(quoteImage, "png", bytes)) {
                    throw new IllegalStateException("PNG image encoder is unavailable");
                }
                return bytes.toByteArray();
            } catch (Exception failure) {
                throw new CompletionException(failure);
            }
        }).thenCompose(bytes -> placeholder.editMessageEmbeds(generateQuoteEmbed())
            .setContent(event.getMember().getAsMention() + " a quote has been generated for you! " + event.getTarget().getJumpUrl())
            .setFiles(FileUpload.fromData(bytes, "quote.png")).submit())
            .whenComplete((message, failure) -> {
                if (failure != null) {
                    placeholder.delete().queue(null, cleanupFailure ->
                        logger.warn("Could not remove failed quote placeholder {}", placeholder.getId(), cleanupFailure));
                }
            });
    }

    private static boolean hasConflicts(MessageContextInteractionEvent event, QuoteSettings settings, Member member) {
        if (!settings.isEnabled()){
            event.getHook().editOriginal("Quotes are currently not enabled for this server!").queue();
            return true;
        } else if (settings.isBoosterOnly() && !member.isBoosting()){
            event.getHook().editOriginal("Quotes are currently only enabled for boosters!").queue();
            return true;
        }
        return false;
    }

    private static boolean hasUserExceededQuota(long userid){
        // if the timestamp is within the last 1 hour, return true
        Timestamp timestamp = QuoteQuotaUtils.selectTimestamp(userid);
        if (timestamp == null){
            //insert the current timestamp into the database with the user id
            QuoteQuotaUtils.insertQuota(userid, System.currentTimeMillis());
            return false;
        } else if (System.currentTimeMillis() - timestamp.getTime() < 10000){
            return true;
        } else {
            //update the timestamp to the current time
            QuoteQuotaUtils.insertQuota(userid, System.currentTimeMillis());
            return false;
        }
    }

    private static MessageEmbed generateQuoteEmbed(){
        return new EmbedBuilder()
                .setTitle("Here is your quote!")
                .setImage("attachment://quote.png")
                .setFooter("Quote image generated by Chisataki | Experimental Feature")
                .setColor(ColorUtils.PURPLE)
                .build();
    }

    private static boolean withinModerationCategory(MessageContextInteractionEvent event){
        return event.getTarget().getCategory().equals("1010078629344051200");
    }
}
