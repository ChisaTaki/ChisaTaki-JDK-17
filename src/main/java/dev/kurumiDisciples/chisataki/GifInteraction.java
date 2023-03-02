package dev.kurumiDisciples.chisataki;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GifInteraction extends ListenerAdapter {

  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Thread gifThread = new Thread() {
      public void run() {
        if (event.getName().equals("gif")) {
          if (!event.getChannel().getId().equals(ChannelEnum.CHISATAKI.getId())) {
            event.deferReply(true).queue();
            String message = String.format("This command can only be used in the <#%s> channel!",
                ChannelEnum.CHISATAKI.getId());
            event.getHook().editOriginal(message).queue();
            return;
          }

          if (hasOptions(event.getOptions())) {
            /* if hug was used */
            event.deferReply().queue();
            User hugging = getUser(event.getOptions());
            event.getHook()
                .editOriginal(
                    event.getMember().getEffectiveName() + " hugs " + hugging.getAsMention() + " for being a cutie!")
                .setEmbeds(getHug()).queue();
          } else {
            /* if hug was not used */
            event.deferReply().queue();

            /* send a random gif inside of an embed */
            event.getHook().editOriginal(" ").setEmbeds(getRandomGif()).queue();
          }
        }
      }
    };
    gifThread.setName("Gif-Thread");
    gifThread.setPriority(5);
    gifThread.start();
  }

  private static boolean hasOptions(List<OptionMapping> options) {
    /* should only ever be used with /gif */

    for (OptionMapping map : options) {
      if (map.getAsUser() != null) {
        return true;
      }
    }
    return false;
  }

  private static Map<String, String> getGifs() {
    Map<String, String> gifs = new HashMap<String, String>();

    gifs.put("spin", "https://media.giphy.com/media/ljsQMTPV9HJXAwXzlI/giphy.gif");
    gifs.put("shoot", "https://media.giphy.com/media/EedurUF9QRignweIiP/giphy.gif");
    gifs.put("sleep", "https://media0.giphy.com/media/CmSFlB38TR9bpb8eqf/200w_s.gif");
    gifs.put("huh", "https://media4.giphy.com/media/sMc9VNDTwjGmTJDWwk/giphy.gif");
    gifs.put("feed", "https://media3.giphy.com/media/IaeIgBEG9GLk3tJmuL/giphy.gif");
    gifs.put("smile", "https://media4.giphy.com/media/PPrK7uC44l3JCf0G1G/giphy.gif");
    gifs.put("hug", "https://media1.giphy.com/media/eHc6FoXCrtu3GSntos/giphy.gif");
    gifs.put("handshake", "https://media2.giphy.com/media/AxBMBvPgIsTyGIev9c/giphy.gif");
    gifs.put("wink", "https://media1.giphy.com/media/c6VmBVHoWXq5YM6qL3/giphy.gif");
    gifs.put("dodge", "https://media2.giphy.com/media/IKNd2qmkyPaOiihaEz/giphy.gif");
    gifs.put("angry", "https://media4.giphy.com/media/qKyk0tnWcCJtNZbRP2/giphy.gif");
    gifs.put("sip", "https://media0.giphy.com/media/UYq8uAQESDPdvEeA7I/giphy.gif");
    gifs.put("cope", "https://media.giphy.com/media/XNeHwUhgM1GNpV7X0w/giphy.gif");
    gifs.put("sip2", "https://c.tenor.com/dwqn-DDq0GkAAAAC/chisato-nishikigi-lycoris-recoil.gif");
    gifs.put("sakana", "https://media.giphy.com/media/7JdNAHzVFji3Be3vyG/giphy.gif");
    gifs.put("handstand", "https://c.tenor.com/ltshRDoZTikAAAAd/lycoris-recoil-thighs.gif");
    gifs.put("resting", "https://media.giphy.com/media/hDOVYkrYeF0ocERSZ5/giphy.gif");
    gifs.put("cookie", "https://media.giphy.com/media/nscgFe0f7FgBWdx65H/giphy.gif");
    gifs.put("check", "https://media.giphy.com/media/BWhZ8wbCHWS12o3my4/giphy.gif");
    gifs.put("supacar", "https://media.giphy.com/media/tFlPowRgoxZCQbtGYK/giphy.gif");
    gifs.put("neck chop", "https://media.giphy.com/media/QC7rQgwXCgikQy2zBs/giphy.gif");
    gifs.put("victory", "https://media.giphy.com/media/kgruWmJCZVh18AQGIR/giphy.gif");
    gifs.put("hop", "https://media.giphy.com/media/HhHo38uVCCGw6RyAs3/giphy.gif");
    gifs.put("touch", "https://media.giphy.com/media/qWBLHu5p1qs5CsAU93/giphy.gif");
    gifs.put("smirk", "https://media.giphy.com/media/8FsrMoMQZtff9oSndJ/giphy.gif");
    gifs.put("takina laugh", "https://media.giphy.com/media/PuxWgUoO9j1GwpYhP7/giphy.gif");
    gifs.put("chisato laugh", "https://media.giphy.com/media/6QBX34Ejm9kGxLQzwM/giphy.gif");
    gifs.put("fight", "https://media.giphy.com/media/3XBYpAQJWHUQerdIdu/giphy.gif");

    return gifs;
  }

  private static MessageEmbed getRandomGif() {
    /* put all map values inside of a arrayList */
    List<String> gifList = new ArrayList<String>(getGifs().values());
    /* remove hug gif */
    gifList.remove("https://media1.giphy.com/media/eHc6FoXCrtu3GSntos/giphy.gif");

    EmbedBuilder gifEmbed = new EmbedBuilder();
    int randomIndex = new Random().nextInt(gifList.size());
    gifEmbed.setImage(gifList.get(randomIndex));

    return gifEmbed.build();
  }

  private static User getUser(List<OptionMapping> map) {
    return map.get(0).getAsUser();
  }

  private static MessageEmbed getHug() {
    String hug = getGifs().get("hug");

    EmbedBuilder gifEmbed = new EmbedBuilder();

    gifEmbed.setImage(hug);
    return gifEmbed.build();
  }
}