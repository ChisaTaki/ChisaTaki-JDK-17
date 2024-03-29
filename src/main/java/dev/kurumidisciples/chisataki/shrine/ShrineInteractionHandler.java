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
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public abstract class ShrineInteractionHandler {

  private String shrineEmoji;
  private String filePath;
  private int recurrence;

  protected ShrineInteractionHandler(String shrineEmoji, int recurrence) {
    this.shrineEmoji = shrineEmoji;
    this.recurrence = recurrence;
  }

  public void handleShrineInteraction(MessageReceivedEvent event) {
    if (shouldHandleShrineCount(event.getMessage(), event.getMember(), event.getGuildChannel().asTextChannel())) {
      handleShrineCount(event);
    } else if (isCorrespondingShrineEmoji(event.getMessage())) {
      deleteMessage(event.getMessage(), event.getMember(), event.getGuildChannel().asTextChannel(),
          "consecutive shrine emoji");
    } else if (isDifferentShrineEmoji(event.getMessage())) {
      deleteMessage(event.getMessage(), event.getMember(), event.getGuildChannel().asTextChannel(),
          "unauthorized shrine emoji");
    }
  }

  private boolean shouldHandleShrineCount(Message message, Member member, TextChannel textChannel) {
    return isCorrespondingShrineEmoji(message)
        && !MessageHistoryUtils.isConsecutiveMessage(member, textChannel, message.getId());
  }
  
  private void handleShrineCount(MessageReceivedEvent event) {
    int newCount = getShrineCount();
    boolean isNewRecord = newCount % this.recurrence == 0;
    Member member = event.getMember();

    if (isNewRecord && RoleUtils.isMemberStaff(member)) {
      logUnrecordedCount(member, newCount);
      return;
    }

    updateCount();
    logCount(member);

    if (isNewRecord) {
      String congratsMessage = getCongratsMessage(event, newCount);
      event.getMessage().reply(congratsMessage).queue();

      rewardShrineRole(event.getGuild(), member);

      TextChannel shrineChannel = event.getGuildChannel().asTextChannel();
      pingHamtaro(event.getGuild(), shrineChannel, member);

      MessageHistoryUtils.getLastBotMessage(shrineChannel).delete().reason("clean up").queueAfter(5, TimeUnit.MINUTES);
    }
  }

  private void pingHamtaro(Guild guild, TextChannel shrineChannel, Member newShrineRoleOwner) {
    String botHouseChannelId = "1010078629344051202";
    String hamtaroId = "422176394575872001";

    TextChannel botHouseChannel = guild.getTextChannelById(botHouseChannelId);
    Member hamtaroMember = guild.getMemberById(hamtaroId);

    String message = String.format("%s Detected a new shrine record in %s by %s", hamtaroMember.getAsMention(),
        shrineChannel.getAsMention(), newShrineRoleOwner.getAsMention());
    botHouseChannel.sendMessage(message).queue();
  }

  private void deleteMessage(Message message, Member member, TextChannel textChannel, String reason) {
    if (!member.getUser().isBot()) {
      message.delete().reason(reason).queueAfter(5, TimeUnit.SECONDS);
      textChannel.sendMessage(member.getAsMention() + " You can't do that.").complete();
      MessageHistoryUtils.getLastBotMessage(textChannel).delete().reason("clean up").delay(Duration.ofSeconds(10))
          .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }
  }

  protected boolean isCorrespondingShrineEmoji(Message message) {
    return message.getContentRaw().equals(this.shrineEmoji);
  }

  protected abstract void logUnrecordedCount(Member member, int unrecordedCount);

  protected abstract void logCount(Member member);

  protected abstract String getCongratsMessage(MessageReceivedEvent event, int shrineCount);

  protected abstract void rewardShrineRole(Guild guild, Member memberToReward);

  protected abstract boolean isDifferentShrineEmoji(Message message);

  protected abstract int getShrineCount();

  protected abstract void updateCount();
}