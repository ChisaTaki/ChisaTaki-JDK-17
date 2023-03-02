import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.*;

import dev.kurumiDisciples.chisataki.listeners.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;

/* To do */
//1. remove the opposing emotes from the respective shrines;
public class MemeInteraction extends ListenerAdapter {

  private static final String TAKINA_BOXERS = "<:TakinaBoxers:1015393926573719593>";
  private static final String CHISATO_FOOT = "<:ChisatoFoot:1015522197156155405>";
  private static final String TAKINA_FOOT = "<:TakinaFoot:1039462047257219162>";
  private static final String CHISATO_SHRINE = "1013939451979911289";
  private static final String TAKINA_SHRINE = "1013939540420997262";
  private static final String MAD_DOG_TAKINA = "<:MadDogTakina:1027897140976025701>";
  private static final String CHISATAKI_KISS = "<:ChisaTakiKiss:1013059473167888486>";

  public void onMessageReceived(MessageReceivedEvent event) {
    Thread messageThread = new Thread() {
      public void run() {
        MessageCache.storeMessage(event.getMessage());
        if (event.getMessage().isWebhookMessage())
          return;
        String userId = event.getAuthor().getId();
        if (IgnoreInteraction.inList(event.getMember())) {
          return;
        }

        String userMessage = event.getMessage().getContentRaw();
        if (userMessage.contains(TAKINA_BOXERS) && userMessage.contains(CHISATO_FOOT)
            && userMessage.contains(TAKINA_FOOT)) {
          event.getMessage().reply("KINKY").queue();
        } else if (userMessage.contains(TAKINA_BOXERS) && userMessage.contains(CHISATO_FOOT)) {
          event.getMessage().reply("OMG! CROSS COMBINATION OF BOXERS AND STINKY CHISATOE?!??! THE HUMANITY!!!!")
              .queue();
        } else if (userMessage.contains(TAKINA_BOXERS) && userMessage.contains(TAKINA_FOOT)) {
          event.getMessage().reply("LEWDDDDDDDDDDDDDDDDDDDDDD").queue();
        } else if (userMessage.contains(TAKINA_BOXERS)) {
          event.getMessage().reply("BOXERS???????").queue();
        } else if (userMessage.contains(CHISATO_FOOT) && userMessage.contains(TAKINA_FOOT)) {
          event.getMessage().reply("Feet holding?!? That’s so lewd…").queue();
        } else if (userMessage.contains(CHISATO_FOOT)) {
          event.getMessage().reply("Ewwwwww Stinky Chisatoe!!!!!!!!").queue();
        } else if (userMessage.contains(TAKINA_FOOT)) {
          event.getMessage().reply("Don’t you dare hurt Chisato or I’ll CRRRUSSH YOU " + MAD_DOG_TAKINA).queue();
        } else if (userMessage.contains(CHISATAKI_KISS)) {
          event.getMessage().addReaction(Emoji.fromFormatted(CHISATAKI_KISS)).queue();
        }

      }
    };
    messageThread.setName("Message-Thread");
    messageThread.setPriority(4);
    messageThread.start();
  }


  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Thread slashCommand = new Thread(){
      public void run(){
        if (event.getName().equals("sus")){

          if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This command can only be used in <#" + ChannelEnum.BOT_CHANNEL.getId() + ">").queue();
            return;
          }
          
          event.deferReply(false).queue();

          if (event.getOption("user") == null){
            event.getHook().editOriginal(" ").setEmbeds(buildSus(event.getMember())).queue();
          }
          else {
            event.getHook().editOriginal(event.getOption("user").getAsMember().getAsMention()).setEmbeds(buildSus(event.getOption("user").getAsMember())).queue();
          }
        }
        else if (event.getName().equals("cute")){
          
          if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This command can only be used in <#" + ChannelEnum.BOT_CHANNEL.getId() + ">").queue();
            return;
          }
          
          event.deferReply(false).queue();
          
          if (event.getOption("user") == null){
            event.getHook().editOriginal(" ").setEmbeds(buildCute(event.getMember())).queue();
          }
          else {
            event.getHook().editOriginal(event.getOption("user").getAsMember().getAsMention()).setEmbeds(buildCute(event.getOption("user").getAsMember())).queue();
          }
        }
        else if (event.getName().equals("cuddle")){
          
          if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This command can only be used in <#" + ChannelEnum.BOT_CHANNEL.getId() + ">").queue();
            return;
          }

          event.deferReply(true).queue();
          
          event.getHook().deleteOriginal().queue();
          
          event.getGuildChannel().asTextChannel().sendMessage(event.getOption("user").getAsMember().getAsMention()).setEmbeds(buildCuddle(event.getMember(), event.getOption("user").getAsMember())).queue();
        }
        else if (event.getName().equals("bite")){
          
          if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This command can only be used in <#" + ChannelEnum.BOT_CHANNEL.getId() + ">").queue();
            return;
          }

          if (event.getOption("user").getAsMember().getId().equals(event.getMember().getId())){
            event.reply("Why would you bite yourself?").queue();
            return;
          }

          event.deferReply(true).queue();
          event.getHook().deleteOriginal().queue();

          event.getGuildChannel().asTextChannel().sendMessage(" ").setEmbeds(buildBite(event.getMember(), event.getOption("user").getAsMember())).queue();

        }
        else if (event.getName().equals("kiss")){
          
          if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This command can only be used in <#" + ChannelEnum.BOT_CHANNEL.getId() + ">").queue();
            return;
          }

          Member memberToKiss = event.getOption("user").getAsMember();
          Member kissing = event.getMember();
          
          event.deferReply(false).queue();
          event.getHook().editOriginal(memberToKiss.getAsMention()).setEmbeds(buildKiss(kissing, memberToKiss)).queue();
        }
      }
    };
    slashCommand.setName("SlashCommand-Thread");
    slashCommand.setPriority(2);
    slashCommand.start();
  }



  public MessageEmbed buildSus(Member member){
    String format = "";
    return new EmbedBuilder()
      .setColor(new Color(144, 96, 233))
      .setImage("https://media.tenor.com/xduDNIZJD2wAAAAC/86anime-frederica.gif")
      .setDescription("**So, "+member.getEffectiveName()+", we evaluated how sus you are! \n\n"+member.getEffectiveName()+", you're "+String.valueOf(new Random().nextInt(101))+"% sus! How sussy!**")
      .setFooter("not stolen from another bot at all, i promise")
      .build();
  }

  public MessageEmbed buildCute(Member member){
    return new EmbedBuilder()
      .setColor(new Color(144, 96, 233))
      .setImage("https://media.tenor.com/3S9l9HzhGVcAAAAC/shake-kaninayuta.gif")
      .setDescription("**So, "+member.getEffectiveName()+", we evaluated how cute you are! \n\n"+member.getEffectiveName()+", you're "+String.valueOf(new Random().nextInt(101))+"% cute! You're such a cutie pie!**")
      .setFooter("this is actually original lol")
      .build();
  }

  public MessageEmbed buildCuddle(Member memberHugging, Member memberCuddled){
    return new EmbedBuilder()
      .setColor(new Color(144, 96, 233))
      .setImage("https://media.tenor.com/kCZjTqCKiggAAAAC/hug.gif")
      .setDescription("**"+memberHugging.getEffectiveName()+" cuddles with "+memberCuddled.getEffectiveName()+"!\n\nDon't be sad anymore, you're an amazing person!**")
      .setFooter("this is stolen")
     .build();
  }

  public MessageEmbed buildBite(Member memberBiting, Member memberBitten){
    return new EmbedBuilder()
      .setColor(new Color(144, 96, 233))
      .setImage("https://tenor.com/view/bite-gif-22830201")
      .setTitle("Just a little bite, " + memberBitten.getEffectiveName() + ", it shouldn't hurt!")
      .setAuthor(memberBiting.getEffectiveName() + " bites " + memberBitten.getEffectiveName())
      .setFooter("OOPS image broke")
      .build();
  }

  private MessageEmbed buildKiss(Member memberKissing, Member memberKissed){
    return new EmbedBuilder()
      .setColor(new Color(144, 96, 233))
      .setAuthor(memberKissing.getEffectiveName() + " kisses " + memberKissed.getEffectiveName())
      .setTitle("Just a little kiss, " + memberKissed.getEffectiveName() + ", hope you love it!")
     .setImage("https://cdn.discordapp.com/attachments/1043880710357917727/1070706217728606328/IMG_0452.gif")
      .setFooter("this was a requested feature. a yuri image for a yuri server")
      .build();
  }
}
