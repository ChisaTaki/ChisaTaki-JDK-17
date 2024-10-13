package dev.kurumidisciples.chisataki.shrine;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import dev.kurumidisciples.chisataki.utils.MessageHistoryUtils;
import dev.kurumidisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


@SuppressWarnings("all")
public abstract class ShrineInteractionHandler {

    private static final String CLEAN_UP_REASON = "clean up";
    private static final String BOT_HOUSE_CHANNEL_ID = "1010078629344051202";
    private static final String HAMTARO_ID = "422176394575872001";
    private static final Duration MESSAGE_DELETE_DELAY = Duration.ofSeconds(10);
    private static final Duration BOT_MESSAGE_CLEANUP_DELAY = Duration.ofSeconds(10);

    private final String shrineEmoji;
    private final int recurrence;

    protected ShrineInteractionHandler(String shrineEmoji, int recurrence) {
        this.shrineEmoji = shrineEmoji;
        this.recurrence = recurrence;
    }

    public void handleShrineInteraction(MessageReceivedEvent event) {
        if (shouldHandleShrineCount(event.getMessage(), event.getMember(), event.getGuildChannel().asTextChannel())) {
            handleShrineCount(event);
        } else if (isCorrespondingShrineEmoji(event.getMessage())) {
            deleteMessage(event.getMessage(), event.getMember(), event.getGuildChannel().asTextChannel(), "consecutive shrine emoji");
        } else if (isDifferentShrineEmoji(event.getMessage())) {
            deleteMessage(event.getMessage(), event.getMember(), event.getGuildChannel().asTextChannel(), "unauthorized shrine emoji");
        }
    }

    private boolean shouldHandleShrineCount(Message message, Member member, TextChannel textChannel) {
        return isCorrespondingShrineEmoji(message) && !MessageHistoryUtils.isConsecutiveMessage(member, textChannel, message.getId());
    }

    private void handleShrineCount(MessageReceivedEvent event) {
        int newCount = getShrineCount();
        boolean isNewRecord = newCount % recurrence == 0;
        Member member = event.getMember();

        if (isNewRecord && RoleUtils.isMemberStaff(member)) {
            logUnrecordedCount(member, newCount);
            return;
        }

        updateCount();
        logCount(member);

        if (isNewRecord) {
            String congratsMessage = getCongratsMessage(event, newCount);
            event.getMessage().reply(congratsMessage).submit().thenAccept(message -> message.delete().queueAfter(MESSAGE_DELETE_DELAY.getSeconds(), TimeUnit.SECONDS));
            rewardShrineRole(event.getGuild(), member);
            pingHamtaro(event.getGuild(), event.getGuildChannel().asTextChannel(), member);
        }
    }

    private void pingHamtaro(Guild guild, TextChannel shrineChannel, Member newShrineRoleOwner) {
        TextChannel botHouseChannel = guild.getTextChannelById(BOT_HOUSE_CHANNEL_ID);
        Member hamtaroMember = guild.getMemberById(HAMTARO_ID);
        String message = String.format("%s Detected a new shrine record in %s by %s", hamtaroMember.getAsMention(), shrineChannel.getAsMention(), newShrineRoleOwner.getAsMention());
        botHouseChannel.sendMessage(message).queue();
    }

    private void deleteMessage(Message message, Member member, TextChannel textChannel, String reason) {
        if (!member.getUser().isBot()) {
            message.delete().reason(reason).queueAfter(MESSAGE_DELETE_DELAY.getSeconds(), TimeUnit.SECONDS);
            textChannel.sendMessage(member.getAsMention() + " You can't do that.").submit().thenAccept(sentMessage -> sentMessage.delete().queueAfter(MESSAGE_DELETE_DELAY.getSeconds(), TimeUnit.SECONDS));
        }
    }

    protected boolean isCorrespondingShrineEmoji(Message message) {
        return message.getContentRaw().equals(shrineEmoji);
    }

    protected String getOrdinalSuffix(int number) {
      switch (number % 100) {
          case 11:
          case 12:
          case 13:
              return "th";
          default:
              switch (number % 10) {
                  case 1:
                      return "st";
                  case 2:
                      return "nd";
                  case 3:
                      return "rd";
                  default:
                      return "th";
              }
      }
  }

    protected abstract void logUnrecordedCount(Member member, int unrecordedCount);

    protected abstract void logCount(Member member);

    protected abstract String getCongratsMessage(MessageReceivedEvent event, int shrineCount);

    protected abstract void rewardShrineRole(Guild guild, Member memberToReward);

    protected abstract boolean isDifferentShrineEmoji(Message message);

    protected abstract int getShrineCount();

    protected abstract void updateCount();
}