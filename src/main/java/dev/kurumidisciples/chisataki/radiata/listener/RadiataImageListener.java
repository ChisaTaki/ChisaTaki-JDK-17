package dev.kurumidisciples.chisataki.radiata.listener;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.radiata.service.RadiataModerationService;
import dev.kurumidisciples.chisataki.radiata.service.RadiataModerationTask;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RadiataImageListener extends ListenerAdapter {

    static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    static final Logger logger = LoggerFactory.getLogger(RadiataImageListener.class);
    
    /**
     * When a message is received, check if it contains an image and send
     * the image through openai's moderation model.
     * If the image is flagged, alert the moderators.
     * Do not delete the message, instead have a button to delete the message containing the image if they see fit.
     */
    @SuppressWarnings("null")
    @Override
    public void onMessageReceived(@SuppressWarnings("null") MessageReceivedEvent event){
        executor.execute(() -> {
            if (event.getMessage().getAttachments().isEmpty() || event.getChannel().asTextChannel().isNSFW()) {
                return;
            }

            for (Attachment attachment : event.getMessage().getAttachments()){
                // process the attachment and check if it's an image
                if (attachment.getContentType() != null && attachment.getContentType().startsWith("image/")) {
                    // send the image to the moderation model and get the results
                    if (attachment.getSize() > 20971520) logger.warn("Attachment ({}) is larger than 20 MiB, service may slow down.", attachment.getUrl());
                    //retrieve the attachment and turn it into a InputStream
                    attachment.getProxy().download().thenAccept(stream -> {
                        try (InputStream in = stream) {

                            byte[] bytes = in.readAllBytes();

                            RadiataModerationTask task = new RadiataModerationTask(
                                        bytes,
                                        attachment.getContentType(),
                                        event
                                    );

                            RadiataModerationService.enqueue(task);

                        } catch (Exception e) {
                            logger.error("Failed preparing moderation task", e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        //handle the button interaction for deleting the message containin the offending image
    }
}
