package dev.kurumidisciples.chisataki.shrine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@SuppressWarnings("all")
public class TakinaShrineInteractionHandler extends ShrineInteractionHandler {

    private static final Logger logger = LoggerFactory.getLogger(TakinaShrineInteractionHandler.class);
    private static final String SAKANA_ROLE_ID = "1023698477240291439";
    private static final String CHISATO_HEART_ROLE_ID = "1023697460763303936";

    protected TakinaShrineInteractionHandler() {
        super(ShrineHelper.TAKINA_EMOJI.getAsText(), 1000);
    }

    @Override
    protected void logUnrecordedCount(Member member, int unrecordedCount) {
        logger.info("Unrecorded Count: User {} used a Sakana and count was not updated since it would've resulted in a new record of {}", member.getEffectiveName(), unrecordedCount);
    }

    @Override
    protected void logCount(Member member) {
        logger.info("User {} used a Sakana and one point has been added for takina", member.getEffectiveName());
    }

    @Override
    protected String getCongratsMessage(MessageReceivedEvent event, int shrineCount) {
        String ordinalSuffix = getOrdinalSuffix(shrineCount);
        return String.format("Congrats to %s for being our %d%s Sakana", event.getMember().getAsMention(), shrineCount, ordinalSuffix);
    }

    @Override
    protected void rewardShrineRole(Guild guild, Member memberToReward) {
        Role sakanaRole = guild.getRoleById(SAKANA_ROLE_ID);
        Role chisatoHeartRole = guild.getRoleById(CHISATO_HEART_ROLE_ID);

        if (sakanaRole != null) {
            guild.findMembersWithRoles(sakanaRole).onSuccess(members -> {
                members.forEach(member -> guild.removeRoleFromMember(member, sakanaRole).queue(
                        success -> logger.info("Sakana role removed from {}", member.getEffectiveName()),
                        error -> logger.error("Failed to remove Sakana role from {}", member.getEffectiveName())
                ));
            });
            guild.addRoleToMember(memberToReward, sakanaRole).queue(
                    success -> logger.info("Sakana role added to {}", memberToReward.getEffectiveName()),
                    error -> logger.error("Failed to add Sakana role to {}", memberToReward.getEffectiveName())
            );
        }

        if (chisatoHeartRole != null) {
            guild.removeRoleFromMember(memberToReward, chisatoHeartRole).queue(
                    success -> logger.info("Chisato Heart role removed from {}", memberToReward.getEffectiveName()),
                    error -> logger.error("Failed to remove Chisato Heart role from {}", memberToReward.getEffectiveName())
            );
        }
    }

    @Override
    protected boolean isDifferentShrineEmoji(Message message) {
        return !message.getContentRaw().equals(ShrineHelper.TAKINA_EMOJI.getAsText());
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