package dev.kurumidisciples.chisataki.listeners;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.character.AssistantMessageRequest;
import dev.kurumidisciples.chisataki.character.UsageTableUtils;
import dev.kurumidisciples.chisataki.character.UserUsage;
import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentProxy;

import java.util.Optional;
import java.util.List;

public class AiListenerInteraction extends ListenerAdapter {
    

      final static Logger logger = LoggerFactory.getLogger(AiListenerInteraction.class);
      static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    
      @Override
      public void onMessageReceived(@Nonnull MessageReceivedEvent event){
        Thread messageThread = new Thread(){
            public void run(){
                SelfUser user = event.getJDA().getSelfUser();

                
                
                if (!event.getMessage().getMentions().isMentioned(user, MentionType.USER) || !(event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())) ) return; //ignore all messages that don't mention the bot
                logger.info("Message mentioning the bot received from user: {}", event.getAuthor().getId());
                
                //check if the user has exceeded the message limit
                //if they have simply ignore them
                if (UsageTableUtils.selectUserUsage(event.getAuthor().getIdLong()) != null){
                    UserUsage usage = UsageTableUtils.selectUserUsage(event.getAuthor().getIdLong());
                    if (usage.hasReachedLimitOfDay(event.getGuild().getIdLong())){
                        logger.info("User {} has reached the message limit for the day", event.getAuthor().getId());
                        //for debugging purposes we will responed to the user for now
                        event.getMessage().reply("You have reached the message limit for the day").mentionRepliedUser(true).submit().thenAccept(message -> {
                            message.delete().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS);
                        });
                        return;
                    }
                }
                event.getChannel().sendTyping().queue(); // let the user know that the bot saw there message and is typing a response
                //if the user has not reached the message limit, we will respond to the user by contacting the AI assistant
                AssistantMessageRequest request = new AssistantMessageRequest(event.getMessage().getContentStripped(), event.getMember(), event.getGuild().getIdLong());
                // max number of attachments is 3 for now
                Optional<List<AttachmentProxy>> attachments = event.getMessage().getAttachments().isEmpty() ? Optional.empty() : Optional.of(event.getMessage().getAttachments().stream().limit(3).map(attach -> attach.getProxy()).toList());
                logger.info("Requesting response from AI");
                AssistantMessageRequest.Response response = request.submitRequest(attachments);
                logger.info("Response received from AI");

                event.getMessage().reply(response.getMessage(event.getMember())).mentionRepliedUser(true).queue();
            }
        };
        executor.execute(messageThread);
      }
}
