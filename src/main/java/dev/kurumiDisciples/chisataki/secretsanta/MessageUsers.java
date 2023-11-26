package dev.kurumidisciples.chisataki.secretsanta;

import java.util.HashMap;

import dev.kurumidisciples.chisataki.Main;
import net.dv8tion.jda.api.entities.User;

public class MessageUsers {
    

    public static void messageUsers(){
        HashMap<Long, Long> pairings = PairingSystem.getRandomizedPairings();

        if (pairings == null){
            return;
        }

        for (Long santa : pairings.keySet()){

            SantaStruct target = SantaDatabaseUtils.getUser(pairings.get(santa));

            User santaUser = Main.getJDA().getUserById(santa);
            User targetUser = Main.getJDA().getUserById(pairings.get(santa));

            if (targetUser == null || santaUser == null){
                continue;
            }

            String message = "Hello, " + santaUser.getName() + "! You have been selected as a Secret Santa for " + santaUser.getName() + "!";

            message += "\n\n" + targetUser.getName() + " has also requested that you get them a \"" + target.getPreferredGift() + "\".";

            message += "\n\nPlease do not tell anyone who you are the Secret Santa for!";

            final String finalMessage = message;
            
            santaUser.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(finalMessage).queue(null, (error) -> {
                    Main.getJDA().getGuildById("1010078628761055234").getTextChannelById("1010078629344051202")
                    .sendMessage("Failed to send message to " + santaUser.getName() + " (" + santaUser.getId() + ")" + "\n\nMessage: " + finalMessage).queue();
                });
            });
        }
    }
}
