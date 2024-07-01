package dev.kurumidisciples.chisataki.listeners;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RuleInteraction extends ListenerAdapter {

  public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
    Thread selectMenu = new Thread() {
      public void run() {
        if (event.getComponentId().equals("menu:info")) {
          event.deferReply(true).queue();
          String selectedValue = event.getValues().get(0);
          switch (selectedValue){
            case "adminSelect":
              event.getHook().editOriginalEmbeds(getStaffEmbed()).queue();
              break;
            case "sisterSelect":
              event.getHook().editOriginalEmbeds(getSisterServer()).queue();
              break;
            case "shrineSelect":
              event.getHook().editOriginalEmbeds(getShrineMenu()).queue();
              break; 
            case "xpSystem":
              event.getHook().editOriginalEmbeds(getXPSystemEmbed()).queue();
              break;
          }
        }
      }
    };
    selectMenu.start();
  }

  public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
    Thread buttonThread = new Thread(){
      public void run(){
        String buttonId = event.getComponentId();
        switch (buttonId){
          case "rules":
            event.deferReply(true).queue();
            event.getHook().editOriginal(" ").setEmbeds(getRuleEmbed()).queue();
            break;
          case "strike":
            event.deferReply(true).queue();
            event.getHook().editOriginal(" ").setEmbeds(getStrikeEmbed()).queue();
            break;
        }
      }
    };
    buttonThread.start();
  }

  private static MessageEmbed getXPSystemEmbed() {
    MessageEmbed xpEmbed = new EmbedBuilder()
      .setTitle("Activity Role Rewards for Lycoris Recoil")
      .setDescription("**Chat to earn Server Score to be awarded with these roles!**\n\n`[1]` | <@&1015237036879392799> **(1000)**\n`[2]` | <@&1015237786128887808> **(5000)**\n`[3]` | <@&1015237918006198312> **(10000)**\n`[4]` | <@&1015238032015753236> **(25000)**\n`[5]` | <@&1015238230125318234> **(50000)**\n`[6]` | <@&1054436745632043149> **(100000)**\n`[7]` | <@&1054436911244116069> **(250000)**\n`[8]` | <@&1054437065141530765> **(500000)**\n\n**Note:** You need to be at least Oshi rank [3] to send screenshots/pics in some of our discussion channels")
      .setColor(new Color(229, 20, 104))
      .build();
    return xpEmbed;
  }
  
  private static List<MessageEmbed> getStrikeEmbed() {
    List<MessageEmbed> strikeEmbeds = new ArrayList<MessageEmbed>();

    MessageEmbed bannerEmbed = new EmbedBuilder()
      .setColor(new Color(229, 20, 104))
      .setImage("https://media.discordapp.net/attachments/972874186378842122/1053067030259056701/unknown.png")
      .build();
    strikeEmbeds.add(bannerEmbed);

    MessageEmbed strike = new EmbedBuilder()
      .setColor(new Color(229, 20, 104))
      .setTitle("Punishment System")
      .setDescription("🔶 This server follows a **4 strike** system applied to major offenses.\n\n> 1st Strike - One hour mute\n> 2nd Strike - 24 hour mute\n> 3rd Strike - 48 hour mute\n> 4th Strike - Ban\n\n🔶 Please note that this is just a standard system, our moderators are free to choose the punishment as they feel necessary, subject to the incident.\n\n🔶 A strike can be retracted after a user has shown genuine improvement after a period of time.\n\n🔶 **Rapid fire violations can lead to an immediate ban, regardless of prior strikes**")
      .build();
    strikeEmbeds.add(strike);
    return strikeEmbeds;
  }
  
  private static List<MessageEmbed> getRuleEmbed() {
    List<MessageEmbed> rules = new ArrayList<MessageEmbed>();
    MessageEmbed bannerEmbed = new EmbedBuilder()
      .setImage("https://media.discordapp.net/attachments/972874186378842122/1053067030259056701/unknown.png")
      .setColor(new Color(229, 20, 104))
      .build();
    rules.add(bannerEmbed);

    MessageEmbed rulesEmbed = new EmbedBuilder()
      .setColor(new Color(229, 20, 104))
      .setTitle("Lycoris Recoil Discord Rules\nPlease read this section carefully.")
      .setDescription("🔶 **Follow the Discord Terms of Services & Guidelines.**\n\n🔹[**Discord Terms**](https://discord.com/terms)\n🔹[**Discord Guidelines**](https://discord.com/guidelines)\n\n> Severe violations will result in an immediate ban. This includes but is not limited to being under the age of 13, Modification of client & Raiding.\n\n"+
    		  		"🔶 **Be respectful, avoid potentially sensitive or awkward topics.**\n\n> To keep this community welcoming for anyone; Harassment, Toxicity and Discrimination will not be tolerated. If you have an issue with another user, please handle it in your DMs rather than settling your disputes in our server.\n\n> Controversial topics should be kept out of the server. This can include Political, religious discussions etc. NSFW topics or conversations with NSFW nature are strictly not allowed.\n\n> No negative talk. Racist, trolling, flaming, and/or any other destructive speech and material are prohibited.\n\n> Casual jokes are fine, but if a member feels uncomfortable with those jokes then stop making them, even if they are not directed at the member in question.\n\n"+
    		  		"🔶 **Do not post NSFW or unsafe content.**\n\n> Posting media that contains or heavily alludes to NSFW or NSFL content is forbidden. IP loggers, viruses, and any other file that does not embed into discord should be refrained from being posted in this server.\n\n> This rule applies to your profile as well, there are no inappropriate profiles allowed. Present yourself with a proper nickname and profile picture. Engage in civilized speech and manner.\n\n> Roleplaying and Excessive flirting should remain in DMs, this is not a dating server.\n\n"+
    		  		"🔶 **Do not spam and/or advertise.**\n\n> Avoid messages that are designed to flood the chat, such as copy-pastas & rapid message sending.\n\n> Do not advertise. Do not post links to other Discord servers or to promote yourself. This includes asking users to like/subscribe/donate/etc. to you or another individual. This rule extends to your DMs with other server members.\n\n"+
    		  		"🔶 **Keep conversations in English.**\n\n> To ensure effective communication and understanding among all members, discussions must be in English.\n\n> While occasional use of foreign words related to Lycoris Recoil is permissible, conversations should predominantly be in English\n\n" +
    		  		"🔶 **Keep spoilers in their respective channels and spoiler tag when needed.**\n\n> Please keep Lycoris Recoil discussions under <#1013849964775997510>.\n> <#1011237234210517065> is our general chat, it is NOT a place to discuss the series.\n\n> Members must spoiler tag any recent spoilers from series under <#1046914709540061224>. Please give members a 24h window to catch up before openly discussing the episode.\n\n> Please spoiler tag any other series shared outside those channels (i.e., <#1015623881740005396>). When sending images, we encourage you to specify the series name so members can decide if they want to click.\n\n"+
    		  		"🔶 **Do not attempt to take advantage of any loop-holes within the rules.**\n\n> Do not attempt to find loopholes in these rules or bypass the auto-mod system. Repeatedly almost violating the rules or 'Toeing the line' is not permitted.\n\n"+
    		  		"🔶 **The staff team reserves the right to make any decision they see fit to keep the server running smoothly.**\n\n> “Not breaking any of the rules” does not excuse you from purposefully degrading the server’s quality.\n\n> If you believe that a staff member or another user is being abusive towards others or possibly you then please DM/ping any of the admins. Alternatively, we suggest you use the `Contact Staff` option above.")
      .build();
    rules.add(rulesEmbed);

    return rules;
  }

  private static List<MessageEmbed> getStaffEmbed() {
    List<MessageEmbed> staffEmbed = new ArrayList<MessageEmbed>();
    MessageEmbed image = new EmbedBuilder()
      .setImage("https://media.discordapp.net/attachments/1041793898047094824/1053054109676220516/Untitled67_20221216023031.png")
      .setColor(new Color(182, 255, 193))
      .build();
    staffEmbed.add(image);
    
    MessageEmbed staff = new EmbedBuilder()
      .setColor(new Color(182, 255, 193))
      .setTitle("Community Staff Members")
      .setDescription("🔸Do not ping Staff Roles for minor cases. Only mention this role if there is a dire emergency, such as a server raid.\n🔸Any misuse of pinging the Staff Roles may be punished.\n🔸Please treat mods with respect, remember, we're only humans as well.")
      .addField("President", "🔹<@422176394575872001>\n🔹<@263352590534836224>\n🔹<@258118512575381506>", true)
      .addField("Vice-President", "🔹<@838708612402249740>", true)
      .addBlankField(true) // simulate 2 columns
      .addField("ChisaTaki Staff", "🔹<@330540834753740810>", true)
      .addField("ChisaTaki Guards", "🔹<@599973543672938511>\n🔹<@119142790537019392>", true)
      .addField("ChisaTaki Interns", "🔹<@360241951804620800>\n", true)
      .addBlankField(true) // simulate 2 columns
      .addField("Head Streamer", "🔹<@353335252304789504>", true)
      .addField("Streamers", "🔹<@727045572028137523>\n🔹<@133126607945728001>\n🔹<@265416682569334784>", true)
      .addBlankField(true) // simulate 2 columns
      .build();
    staffEmbed.add(staff);
    return staffEmbed;
  }

  private static MessageEmbed getSisterServer() {
    MessageEmbed sister = new EmbedBuilder()
      .setColor(new Color(229, 20, 104))
      .setThumbnail("https://cdn.discordapp.com/icons/1127334757026500782/f257e945aedb1e10d0e2b9bb383ee24d.webp?size=128")
      .setTitle("Bar4bidden","https://discord.gg/PT6Ek66urc")
      .setDescription("A Lycoris Recoil Translations Group.\r\n" + "We translate various LycoReco official contents.")
      .build();
    return sister;
  }

  private static MessageEmbed getShrineMenu() {
    MessageEmbed shrineMenu = new EmbedBuilder()
      .setTitle("Shrine Info")
      .setDescription("The server is split into two factions <@&1013567857075953706> and <@&1013558607213756518>.\n\nEach group has their own secret channel and their own `shrine channel`; `#takina-shrine`(<#1013939540420997262>) and `chisato-shrine` (<#1013939451979911289>) respectively.")
      .addField("Shrine Channel Points", "In each shrine channel, a member can send 1 of their respective shrine emotes to earn a point for their group.\nConsecutive posting by the same person does not count and will be deleted.", false)
      .addField("Special Role", "After every assigned increment from the shrine channel (1000 for `#takina-shrine` and 500 for `chisato-shrine`) the user who reached that increment will be given a special temporary role.", true)
      .setColor(new Color(229, 20, 104))
      .build();
    return shrineMenu;
  }
}
