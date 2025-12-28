package dev.kurumidisciples.chisataki.listeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.introduction.IntroductionUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class IntroductionListener extends ListenerAdapter {
    
    private static ExecutorService introductionExecutor = Executors.newSingleThreadExecutor();

    private static final Logger logger = LoggerFactory.getLogger(IntroductionListener.class);

    @Override
    @SuppressWarnings("null")
    public void onMessageReceived(MessageReceivedEvent event) {
        introductionExecutor.submit(() -> {
            if (event.getChannel().getId().equals(ChannelEnum.INTRODUCTION.getId())) {
                
                if (IntroductionUtils.isUserIntroduced(event.getAuthor().getIdLong())){
                    logger.info("User {}[{}] has already introduced themselves. Message will be deleted.", event.getAuthor().getName(), event.getAuthor().getId());
                    event.getMessage().delete().queue();
                } else {
                    logger.info("New introduction message received from user: {}[{}]", event.getAuthor().getName(), event.getAuthor().getId());
                   int success = IntroductionUtils.insertUserIntroduction(event.getAuthor().getIdLong(), event.getMessageIdLong());
                     if (success == 1) {
                          logger.info("Introduction message for user {}[{}] recorded successfully.", event.getAuthor().getName(), event.getAuthor().getId());
                     } else {
                          logger.error("Failed to record introduction message for user {}[{}].", event.getAuthor().getName(), event.getAuthor().getId());
                     }
                }
            }
        });
    }
}
