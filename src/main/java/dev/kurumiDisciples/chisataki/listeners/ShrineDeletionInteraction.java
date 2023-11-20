package dev.kurumidisciples.chisataki.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.enums.EmojiEnum;
import dev.kurumidisciples.chisataki.utils.RoleUtils;

public class ShrineDeletionInteraction extends ListenerAdapter {
    private static final String BOOSTER_CHANNEL = "1028022086888869888";
    private static final ExecutorService deleteExecutor = Executors.newCachedThreadPool();

    public void onMessageReceived(MessageReceivedEvent event) {
        deleteExecutor.execute(() -> handleReceivedMessage(event));
    }

    public void onMessageUpdate(MessageUpdateEvent event) {
        deleteExecutor.execute(() -> handleUpdatedMessage(event));
    }

    private void handleReceivedMessage(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals(ChannelEnum.CHISATO_SHRINE.getId())
                && !event.getMessage().getContentRaw().equals(EmojiEnum.CHISATO_HEART.getAsText())
                && !event.getAuthor().isBot() && !isExcluded(event.getMember())) {
            event.getMessage().delete().reason("not the corresponding shrine emote").queue();
        } else if (event.getChannel().getId().equals(ChannelEnum.TAKINA_SHRINE.getId())
                && !event.getMessage().getContentRaw().equals(EmojiEnum.SAKANA.getAsText()) && !event.getAuthor().isBot()
                && !isExcluded(event.getMember())) {
            event.getMessage().delete().reason("not the corresponding shrine emote").queue();
        } else if (event.getChannel().getId().equals(BOOSTER_CHANNEL) && !event.getAuthor().isBot()
                && !isExcluded(event.getMember())) {
            if (!event.getMessage().getContentStripped().startsWith("bb ")) {
                event.getMessage().delete().reason("This member can't do that.").queue();
            }
        }
    }

    private void handleUpdatedMessage(MessageUpdateEvent event) {
        if ((event.getChannel().getId().equals(ChannelEnum.CHISATO_SHRINE.getId())
                || event.getChannel().getId().equals(ChannelEnum.TAKINA_SHRINE.getId())) && !event.getAuthor().isBot()
                && !isExcluded(event.getMember())) {
            event.getMessage().delete().reason("Edited Message").queue();
        }
    }

    private static boolean isExcluded(Member member) {
        return RoleUtils.isMemberStaff(member);
    }
}
