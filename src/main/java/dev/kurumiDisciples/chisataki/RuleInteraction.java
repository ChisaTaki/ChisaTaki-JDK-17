package dev.kurumiDisciples.chisataki;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class RuleInteraction extends ListenerAdapter {


  final static String BANNER = "https://media.discordapp.net/attachments/884664269675843594/1035230094387580979/unknown.png";
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
    Thread testRule = new Thread(){
      public void run(){
        if (event.getName().equals("testdfs-send")){
          event.deferReply(true).queue();
          if (!event.getMember().getId().equals("360241951804620800")) return;
          event.getGuild().getTextChannelById("1010080963927232573").sendMessage(" ").setEmbeds(getInfoEmbed()).addActionRow(getSelectMenu()).addActionRow(getButtons()).queue();
        // event.getHook().editOriginal(" ").setEmbeds(getInfoEmbed()).setActionRow(getSelectMenu()).queue();
        }
      }
    };
    testRule.setName("Rule-Thread");
    testRule.start();
  }

  public void onStringSelectInteraction(StringSelectInteractionEvent event){
    Thread selectMenu = new Thread(){
      public void run(){
        if (event.getComponentId().equals("menu:info")){
          event.deferReply(true).queue();
          String selectedValue = event.getValues().get(0);
          switch (selectedValue){
            case "adminSelect":
              event.getHook().editOriginal(" ").setEmbeds(getStaffEmbed()).queue();
              break;
            case "sisterSelect":
              event.getHook().editOriginal(" ").setEmbeds(getSisterServer()).queue();
              break;
              
            case "partnerSelect":
              event.getHook().editOriginal(" ").setEmbeds(getPartnerMenu()).queue();
              break; 
            case "verifySelect":
              if(event.getMember().hasAccess(event.getGuild().getTextChannelById("1041573615822450800"))){
                event.getHook().editOriginal("You are already verified on this server").queue();
              }
              else{
              //  event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("1002789808931885169")).queue();
                event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("1014411262916046869")).queue();
                event.getHook().editOriginal("Welcome to Café LycoReco!").queue();
              }
              break;
            case "xpSystem":
              event.getHook().editOriginal(" ").setEmbeds(getXPSystemEmbed()).queue();
              break;
          }
        }
      }
    };
    selectMenu.start();
  }

  public void onButtonInteraction(ButtonInteractionEvent event){
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

  private static List<MessageEmbed> getXPSystemEmbed(){
    List<MessageEmbed> systemEmbeds = new ArrayList<MessageEmbed>();

    MessageEmbed XP = new EmbedBuilder()
      .setTitle("Activity Role Rewards for Lycoris Recoil")
      .setDescription("**Chat to earn Server Score to be awarded with these roles!**\n\n`[1]` | <@&1015237036879392799> **(1000)**\n`[2]` | <@&1015237786128887808> **(5000)**\n`[3]` | <@&1015237918006198312> **(10000)**\n`[4]` | <@&1015238032015753236> **(25000)**\n`[5]` | <@&1015238230125318234> **(50000)**\n`[6]` | <@&1054436745632043149> **(100000)**\n`[7]` | <@&1054436911244116069> **(250000)**\n`[8]` | <@&1054437065141530765> **(500000)**")
      .setColor(new Color(229, 20, 104))
      .build();
    systemEmbeds.add(XP);

    return systemEmbeds;
  }
  private static List<MessageEmbed> getStrikeEmbed(){
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
  private static List<MessageEmbed> getRuleEmbed(){
    List<MessageEmbed> rules = new ArrayList<MessageEmbed>();
    MessageEmbed bannerEmbed = new EmbedBuilder()
      .setImage("https://media.discordapp.net/attachments/972874186378842122/1053067030259056701/unknown.png")
      .setColor(new Color(229, 20, 104))
      .build();
    rules.add(bannerEmbed);

    MessageEmbed rulesEmbed = new EmbedBuilder()
      .setColor(new Color(229, 20, 104))
      .setTitle("Lycoris Recoil Discord Rules\nPlease read this section carefully.")
      .setDescription("🔶**Follow the Discord Terms of Services & Guidelines.**\n\n🔹[**Discord Terms**](https://discord.com/terms)\n🔹[**Discord Guidelines**](https://discord.com/guidelines)\n\n> Severe violations will result in an immediate ban. This includes but is not limited to being under the age of 13, Modification of client & Raiding.\n\n🔶**Be respectful, avoid potentially sensitive or awkward topics.**\n\n> To keep this community welcoming for anyone; Harassment, Toxicity and Discrimination will not be tolerated. If you have an issue with another user, please handle it in your DMs rather than settling your disputes in our server.\n\n> Controversial topics should be kept out of the server. This can include Political, religious discussions etc. NSFW topics or conversations with NSFW nature are strictly not allowed.\n\n> No negative talk. Racist, trolling, flaming, and/or any other destructive speech and material are prohibited.\n\n> Casual jokes are fine, but if a member feels uncomfortable with those jokes then stop making them, even if they are not directed at the member in question.\n\n🔶**Do not post NSFW or unsafe content.**\n\n> Posting media that contains or heavily alludes to NSFW or NSFL content is forbidden. IP loggers, viruses, and any other file that does not embed into discord should be refrained from being posted in this server.\n\n> This rule applies to your profile as well, there are no inappropriate profiles allowed. Present yourself with a proper nickname and profile picture. Engage in civilized speech and manner.\n\n> Roleplaying and Excessive flirting should remain in DMs, this is not a dating server.\n\n🔶**Do not spam and/or advertise.**\n\n> Avoid messages that are designed to flood the chat, such as copy-pastas & rapid message sending.\n\n> Do not advertise. Do not post links to other Discord servers or to promote yourself. This includes asking users to like/subscribe/donate/etc. to you or another individual. This rule extends to your DMs with other server members.\n\n🔶**Spoilers should be kept out of all chats other than the appropriate channels.**\n\n> Moderation around this rule is very strict. <#1011237234210517065> is our general chat, it is NOT a place to discuss the series.\n\n> All discussions around Lycoris Recoil should be kept under channels within the channels under the **Lycoris Recoil** category\n\n🔶**Do not attempt to take advantage of any loop-holes within the rules.**\n\n> Do not attempt to find loopholes in these rules or bypass the auto-mod system. Repeatedly almost violating the rules or 'Toeing the line' is not permitted.\n\n🔶**The staff team reserves the right to make any decision they see fit to keep the server running smoothly.**\n\n> “Not breaking any of the rules” does not excuse you from purposefully degrading the server’s quality.\n\n> If you believe that a staff member is being abusive towards others or possibly you then please DM/ping any of the admins. \n")
      .build();
    rules.add(rulesEmbed);

    return rules;
  }
  
  private static List<MessageEmbed> getInfoEmbed(){
    List<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
   /* MessageEmbed bannerEmbed = new EmbedBuilder().setImage(BANNER).setColor(new Color(229, 20, 104)).build();
    embeds.add(bannerEmbed); */

    MessageEmbed aboutServer = new EmbedBuilder()
      .setTitle("Welcome to ChisaTaki!")
      .setThumbnail("https://cdn.discordapp.com/icons/1010078628761055234/a_31c7473cad3f9b74a96b331d9b9dbaa1?size=128")
      .setDescription("Welcome to ChisaTaki!\nWe are a server dedicated to ChisaTaki.\nIt also has a Chisato and Takina Fanclub integrated in it.\n\nLet's gather together to ~~worship~~ lead wholesome discussions and enjoy some time together with Lycoirs Recoil fans. Let's enjoy the harmony and ship Chisato and Takina all along.\n\n**Please note that we are a sister server of the below embedded Lycoris Recoil main server.**")
      .setColor(new Color(144, 96, 233))
      .build();
    embeds.add(aboutServer);

    MessageEmbed aboutSeries = new EmbedBuilder()
      .addField("About us", "This server is dedicated to **Lycoris Recoil (リコリス・リコイル)** original TV Anime Series set to be produced by A-1 Pictures and Director Shingo Adachi (SAO, To-Love Ru) featuring an original story by Asaura and Character Design by Imigimuru.", false)
      .addField("Story", "For these peaceful days――there’s a secret behind it all. A secret organization that prevents crimes: “DA - Direct Attack”. And their group of all-girl agents: “Lycoris”. \n\nThis peaceful everyday life is all thanks to these young girls.\n\nThe elite Chisato Nishikigi is the strongest Lycoris agent of all time. Alongside is Takina Inoue, the talented but mysterious Lycoris.\n\nThey work together at one of its branches–Café LycoReco.\nHere, the orders this café takes range from coffee and sweets to childcare, shopping, teaching Japanese to foreign students, etc.\n\nThe free-spirited and optimistic pacifist, Chisato. And the cool-headed and efficient Takina.\n\nThe chaotic everyday lives of this mismatched duo begin!", false)
      .setColor(new Color(144, 96, 233))
      .build();
    embeds.add(aboutSeries);

  /*  MessageEmbed verify = new EmbedBuilder()
      .setTitle("Verification and Server Access")
      .setDescription("**In order to gain access to our server and start chatting, please use the selection menu below.**\n\nPlease note that when you choose to verify, you also accept our rules and conditions. Excuses such as `I did not know there were rules.` or `I did not read the rules.` will not be accepted as valid excuses.")
      .setColor(new Color(229, 20, 104))
      .build();
    embeds.add(verify); */
    return embeds;
  }

  private static SelectMenu getSelectMenu(){
    StringSelectMenu menu = StringSelectMenu.create("menu:info")
      .setPlaceholder("Use this menu to learn more about our server.")
     // .addOption(/*label*/"Verify", "verifySelect", "Select to verify server access.", /*emoji*/ Emoji.fromUnicode("U+2705"))
      .addOption(/*label*/"Church Staff", /*value*/"adminSelect", /*description*/"Select to learn about our lovely staff.", Emoji.fromCustom("LycoReco", 993444445741645845L, false))
      //.addOption(/*label*/"Channel Information", /*value*/"channelSelect", /*description*/"Select to view info about our channels.")
      .addOption(/*label*/"Main Server", /*value*/ "sisterSelect", "Select to view our main server.", Emoji.fromCustom("ChisaTakiKiss", 1014257843974721606L, false))
      .addOption(/*label*/"Shrine Info", /*value*/ "partnerSelect", /*description*/"Select to learn more about the Shrine Channels", Emoji.fromUnicode("U+26E9"))
      .addOption("XP System", "xpSystem", "Select to learn about our XP System.", Emoji.fromCustom("YellowSpiderLily", 997605599531507873L, false))
      //.addOption("Server Credits", "creditSelect", "Select to view the credits of the people who helped make this server come to life~")
      .build();
    return menu;
  }

  private static List<Button> getButtons(){
    List<Button> butt = new ArrayList<Button>();
    butt.add(
      Button.secondary("rules", "Server Rules").withEmoji(Emoji.fromCustom("LycorisLogo", 994615569833791498L, false))
    );
    butt.add(
      Button.secondary("strike", "Strike System").withEmoji(Emoji.fromCustom("LycorisFlower1", 994650226453397634L, false))
    );
    butt.add(
      Button.secondary("modmail", "Contact Staff").withEmoji(Emoji.fromUnicode("U+1F4E8"))
    );
   /* butt.add(
      Button.link("https://discord.com/channels/990527994969874493/1041770584125747220/1041771975539949618", "Contact Staff").withEmoji(Emoji.fromCustom("RedSpiderLily", 997584350407295178L, false))
    );*/

    return butt;
  }

  private static List<ItemComponent> getActions(){
    List<ItemComponent> t = new ArrayList<ItemComponent>();
    t.add(getSelectMenu());
    t.add(getButtons().get(0));
    t.add(getButtons().get(1));

    return t;
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
      .addField("Vice-President", "🔹<@944156317399011388>\n🔹<@838708612402249740>", true)
      .addField("", "", true) // simulate 2 columns
      .addField("ChisaTaki Staff", "🔹<@360241951804620800>\n🔹<@330540834753740810>", true)
      .addField("ChisaTaki Guards", "🔹<@599973543672938511>\n🔹<@119142790537019392>", true)
      .addField("", "", true) // simulate 2 columns
      .addField("Head Streamer", "🔹<@353335252304789504>", true)
      .addField("Streamers", "🔹<@727045572028137523>\n🔹<@133126607945728001>\n🔹<@265416682569334784>", true)
      .addField("", "", true) // simulate 2 columns
      .build();
    staffEmbed.add(staff);
    return staffEmbed;
  }

  private static MessageEmbed getSisterServer(){
    MessageEmbed sister = new EmbedBuilder()
      .setColor(new Color(229, 20, 104))
      .setThumbnail("https://cdn.discordapp.com/icons/990527994969874493/fcd39827e77b320389cbfb328b397c2d?size=128")
      .setTitle("Lycoris Recoil","https://discord.gg/LycorisRecoil")
      .setDescription("The Lycoris Recoil server is dedicated to **Lycoris Recoil (リコリス・リコイル)** original TV Anime Series set to be produced by A-1 Pictures and Director Shingo Adachi (SAO, To-Love Ru) featuring an original story by Asaura and Character Design by Imigimuru.")
      .build();
    return sister;
  }

  private static List<MessageEmbed> getPartnerMenu(){
    List<MessageEmbed> partnerMenus = new ArrayList<MessageEmbed>();
   /* MessageEmbed eyecatcher = new EmbedBuilder()
      .setImage("https://media.discordapp.net/attachments/972874186378842122/1053025107968270336/Untitled67_20220902165058.png")
      .setColor(new Color(229, 20, 104))
      .build();
    partnerMenus.add(eyecatcher);*/

    MessageEmbed requirements = new EmbedBuilder()
      .setTitle("Shrine Info")
      .setDescription("The server is split into two factions <@&1013567857075953706> and <@&1013558607213756518>.\n\nEach group has their own secret channel and their own `shrine channel`; <#1013939540420997262> and <#1013939451979911289> respectively.")
      .addField("Shrine Channel Points", "In each shrine channel, a member can send 1 of their respective shrine emotes to earn a point for their group.\nConsecutive posting by the same person does not count and will be deleted.", false)
      .addField("Special Role", "After every assigned increment from the shrine channel (1000 for <#1013939540420997262> and 500 for <#1013939451979911289>) the user who reached that increment will be given a special temporary role.", true)
      .setColor(new Color(229, 20, 104))
      .build();
    partnerMenus.add(requirements);

    return partnerMenus;
  }
}