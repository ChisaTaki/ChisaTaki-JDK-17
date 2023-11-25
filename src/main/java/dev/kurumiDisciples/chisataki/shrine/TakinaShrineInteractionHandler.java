package dev.kurumidisciples.chisataki.shrine;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TakinaShrineInteractionHandler extends ShrineInteractionHandler{

  private static final Logger logger = LoggerFactory.getLogger(TakinaShrineInteractionHandler.class);

  protected TakinaShrineInteractionHandler() {
    super(ShrineHelper.TAKINA_EMOJI.getAsText(), 1000);
  }

  @Override
  protected void logUnrecordedCount(Member member, int unrecordedCount) {
    logger.info(
        "Unrecorded Count: User {} used a Sakana and count was not updated since it would've resulted in a new record of {}",
        member.getEffectiveName(), unrecordedCount);
  }

  @Override
  protected void logCount(Member member) {
    logger.info("User {} used a Sakana and one point has been added for takina", member.getEffectiveName());
  }

  @Override
  protected String getCongratsMessage(MessageReceivedEvent event, int shrineCount) {
    return String.format("Congrats to %s for being our %d%s", event.getMember().getAsMention(), shrineCount,
        "th Sakana");
  }

  @Override
  protected void rewardShrineRole(Guild guild, Member memberToReward) {
    /* Remove the role from everyone who has it */
    Role sakana = guild.getRoleById("1023698477240291439");
    Role chisatoHeart = guild.getRoleById("1023697460763303936");
    List<Member> members = guild.getMembersWithRoles(sakana);

    for (Member member : members) {
      try {
        guild.removeRoleFromMember(member, sakana).reason("New Sakana Winner").complete();
      } catch (Exception e) {
        logger.error("Sakana Role removal failure for {}#{} for reason: {}", member.getEffectiveName(),
            member.getUser().getDiscriminator(), e.getMessage());
      }
    }

    /* Grant the role to the user who has won it */
    guild.addRoleToMember(memberToReward, sakana).reason("Rewarding User").complete();
    guild.removeRoleFromMember(memberToReward, chisatoHeart).reason("Remove other reward from user").complete();
  }

  @Override
  protected boolean isDifferentShrineEmoji(Message message) {
    return message.getContentRaw().equals(ShrineHelper.CHISATO_EMOJI.getAsText());
  }

  @Override
  protected int getShrineCount() {
    return ShrineDatabaseUtils.getTakinaShrineCount();
  }

  @Override
  protected void updateCount() {
    ShrineDatabaseUtils.incrementTakinaShrineCount();
  }
}