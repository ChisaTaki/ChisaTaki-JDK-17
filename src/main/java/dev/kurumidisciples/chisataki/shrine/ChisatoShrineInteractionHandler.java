package dev.kurumidisciples.chisataki.shrine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@SuppressWarnings("all")
public class ChisatoShrineInteractionHandler extends ShrineInteractionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChisatoShrineInteractionHandler.class);
    private static final String CHISATO_HEART_ROLE_ID = "1023697460763303936";
    private static final String SAKANA_ROLE_ID = "1023698477240291439";
    private static final String NEW_CHISATO_HEART_WINNER_REASON = "New Chisato Heart Winner";
    private static final String REWARDING_USER_REASON = "Rewarding User";
    private static final String REMOVE_OTHER_REWARD_REASON = "Remove other reward from user";

    protected ChisatoShrineInteractionHandler() {
        super(ShrineHelper.CHISATO_EMOJI.getAsText(), 500);
    }

    @Override
    protected void logUnrecordedCount(Member member, int unrecordedCount) {
        logger.info("Unrecorded Count: User {} used a Chisato Heart and count was not updated since it would've resulted in a new record of {}",
                member.getEffectiveName(), unrecordedCount);
    }

    @Override
    protected void logCount(Member member) {
        logger.info("User {} used a Chisato Heart and one point has been added for chisato", member.getEffectiveName());
    }

    @Override
    protected String getCongratsMessage(MessageReceivedEvent event, int shrineCount) {
        String ordinalSuffix = getOrdinalSuffix(shrineCount);
        return String.format("Congrats to %s for being our %d%s Chinanago's Heart", event.getMember().getAsMention(), shrineCount, ordinalSuffix);
    }

    @Override
    protected void rewardShrineRole(Guild guild, Member memberToReward) {
        Role chisatoHeart = guild.getRoleById(CHISATO_HEART_ROLE_ID);
        Role sakana = guild.getRoleById(SAKANA_ROLE_ID);

        if (chisatoHeart != null) {
            removeRoleFromAllMembers(guild, chisatoHeart, NEW_CHISATO_HEART_WINNER_REASON);
            guild.addRoleToMember(memberToReward, chisatoHeart).reason(REWARDING_USER_REASON).queue();
        }
        if (sakana != null) {
            guild.removeRoleFromMember(memberToReward, sakana).reason(REMOVE_OTHER_REWARD_REASON).queue();
        }
    }

    private void removeRoleFromAllMembers(Guild guild, Role role, String reason) {
        guild.findMembersWithRoles(role).onSuccess(members -> members.forEach(member ->
            guild.removeRoleFromMember(member, role).reason(reason).queue(
                success -> logger.info("Successfully removed {} from {}", role.getName(), member.getEffectiveName()),
                error -> logger.error("Failed to remove {} from {} due to {}", role.getName(), member.getEffectiveName(), error.getMessage())
            )
        ));
    }

    @Override
    protected boolean isDifferentShrineEmoji(Message message) {
        return !message.getContentRaw().equals(ShrineHelper.CHISATO_EMOJI.getAsText());
    }

    @Override
    protected int getShrineCount() {
        return ShrineDatabaseUtils.getChisatoShrineCount();
    }

    @Override
    protected void updateCount() {
        ShrineDatabaseUtils.incrementChisatoShrineCount();
    }
}