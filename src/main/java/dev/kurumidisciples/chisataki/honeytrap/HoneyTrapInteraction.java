package dev.kurumidisciples.chisataki.honeytrap;

import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.components.separator.Separator;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;


public class HoneyTrapInteraction extends ListenerAdapter{

    final static Logger logger = LoggerFactory.getLogger(HoneyTrapInteraction.class);

    final static ExecutorService honeyexecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        honeyexecutor.execute(() -> {
            if (event.getAuthor().isBot() || !event.getChannel().getId().equals("1510911789100044331")) return;
            
            logger.info("Honey trap message received from user {}", event.getAuthor().getId());

            notifyBotHouse(event);
            
            event.getMember()
                .ban(7, TimeUnit.DAYS)
                .reason("Fell for the honey trap").queueAfter(4, TimeUnit.SECONDS,
                    response -> logger.info("User {} has been banned for falling for the honey trap", event.getAuthor().getId()),
                    error -> logger.error("Failed to ban user {} for falling for the honey trap", event.getAuthor().getId(), error)
                );
        });
    }

    @SuppressWarnings("null")
    private static void notifyBotHouse(MessageReceivedEvent event){
        var attachmentsComponent = event.getMessage().getAttachments().isEmpty()
            ? TextDisplay.of("*No attachments were sent with the message.*")
            : MediaGallery.of(collectAttachments(event.getMessage()));
        var messageContent = event.getMessage().getContentRaw().isEmpty()
            ? TextDisplay.of("*No text content was sent with the message.*")
            : TextDisplay.of("```\n" + event.getMessage().getContentRaw() + "\n```");

        event.getGuild().getTextChannelById(ChannelEnum.BOT_HOUSE.getId())
            .sendMessageComponents(
                Container.of(
                    Section.of(
                        Thumbnail.fromUrl(event.getMember().getEffectiveAvatarUrl()),
                        TextDisplay.of("# Honey Trap Triggered\nContents of the messsage are contained below."),
                        TextDisplay.of("**User ID:** " + event.getAuthor().getId() + "\n**Username:** " + event.getAuthor().getAsTag())
                    ),
                    Separator.createDivider(Separator.Spacing.LARGE),

                    TextDisplay.of("Message Content:"),
                    messageContent,
                    attachmentsComponent
                )
            ).useComponentsV2().queue();
    }

    private static ArrayList<MediaGalleryItem> collectAttachments(Message message){
        ArrayList<MediaGalleryItem> items = new ArrayList<>();

        for (Message.Attachment attachment : message.getAttachments()) {
            try {
                items.add(MediaGalleryItem.fromFile(FileUpload.fromData(attachment.getProxy().download().get(), attachment.getFileName())).withSpoiler(true));
            } catch (InterruptedException e) {
                logger.error("Attachment download interrupted for {} from message {}", attachment.getFileName(), message.getId(), e);
            } catch (ExecutionException e) {
                logger.error("Failed to download attachment {} from message {}", attachment.getFileName(), message.getId(), e);
            }
        }

        return items;
    }
}