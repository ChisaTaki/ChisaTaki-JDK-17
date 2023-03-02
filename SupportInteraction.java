import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.sticker.*;

public class SupportInteraction extends ListenerAdapter {
  private static String SUPPORTER_CHANNEL = "1015626668360077453";
  private static String BOOSTER_CHANNEL = "1028022086888869888";
    
  public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event) {
    Thread boostThread = new Thread(){
      public void run(){
          if (event.getOldTimeBoosted() == null) {
              String messageFormat = "Thank you for the boost %s!!!!!❤️ Please go to %s to claim your customizable role and color if you haven't already!";
              String message = String.format(messageFormat, event.getMember().getAsMention(), event.getGuild().getTextChannelById(BOOSTER_CHANNEL).getAsMention());
              event.getGuild().getTextChannelById(SUPPORTER_CHANNEL).sendMessage(message)
                .setStickers(StickerSnowflake.fromId(event.getGuild().getStickersByName("ChisaTaki Cuddle", false).get(0).getId()))
                .queue();
          }
      }
    };
    boostThread.setName("Boost-Thread");
    boostThread.start();
  }
}