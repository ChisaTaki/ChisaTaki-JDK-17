package dev.kurumidisciples.chisataki.secretsanta;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class MessageUsers {

    private static final String GUILD_ID = "1010078628761055234";
    private static final String TEXT_CHANNEL_ID = "1010078629344051202";

    public static void messageUsers() {
        HashMap<Long, Long> pairings = PairingSystem.getRandomizedPairings();

        if (pairings == null) {
            return;
        }

        for (Long santa : pairings.keySet()) {
            SantaStruct target = SantaDatabaseUtils.getUser(pairings.get(santa));
            User santaUser = Main.getJDA().getUserById(santa);
            User targetUser = Main.getJDA().getUserById(target.getUserId());

            if (targetUser != null && santaUser != null) {
                String message = generateMessage(santaUser, targetUser, target);

                santaUser.openPrivateChannel().queue(
                        (channel) -> channel.sendMessage("**Please use this pairing instead of the one's you were previously given!**").setEmbeds(createEmbed(SantaDatabaseUtils.getUser(santa), target)).queue(
                                null,
                                (error) -> sendMessageError(santaUser, targetUser)
                        )
                );
                //store pairing
                PairingsUtil.insertPairing(santaUser.getIdLong(), targetUser.getIdLong());
            }
        }
    }

    private static String generateMessage(User santaUser, User targetUser, SantaStruct target) {
        String message = "Hello, " + santaUser.getName() + "! You have been selected as a Secret Santa for " + targetUser.getAsMention() + "!";
        message += "\n\n" + targetUser.getName() + " has also requested that you get them a \"" + target.getPreferredGift() + "\".";
        message += "\n\nPlease do not tell anyone who you are the Secret Santa for!";
        return message;
    }

    private static MessageEmbed createEmbed(SantaStruct santa, SantaStruct target) {
        EmbedBuilder builder = new EmbedBuilder();
        User santaUser = Main.getJDA().getUserById(santa.getUserId());
        User targetUser = Main.getJDA().getUserById(target.getUserId());

        builder.setColor(ColorUtils.PURPLE);
        builder.setThumbnail(targetUser.getAvatarUrl());
        builder.setTitle("Secret Santa Pairing");
        builder.setDescription("Hello, " + santaUser.getAsMention() + "! You have been selected as a Secret Santa for " + targetUser.getAsMention() + "!"
                + "\n\n" + targetUser.getName() + " has also requested that you get them a \"" + target.getPreferredGift() + "\"." + "\n\nPlease do not tell anyone who you are the Secret Santa for!");

        builder.setFooter("Contact ChisaTaki Staff if you have any questions or concerns.");

        return builder.build();
    }

    private static void sendMessageError(User santaUser, User target) {
        Main.getJDA().getGuildById(GUILD_ID).getTextChannelById(TEXT_CHANNEL_ID)
                .sendMessage("Failed to send message to " + santaUser.getName() + " (" + santaUser.getId() + ")" + "\n\nMessage: ")
                .setEmbeds(createEmbed(SantaDatabaseUtils.getUser(santaUser.getIdLong()), SantaDatabaseUtils.getUser(target.getIdLong())))
                .queue();
    }
}
