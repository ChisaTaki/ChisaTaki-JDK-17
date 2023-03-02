package dev.kurumiDisciples.chisataki;

import java.time.Instant;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class EmbedInteraction extends ListenerAdapter {
  
  
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
    Thread embedThread = new Thread(){
      public void run(){
        if (event.getName().equals("embed")){
          event.deferReply(true).queue();
          /* Create the Embed */ 
          MessageEmbed t = createEmbed(event);
          /* Now we need to send this Embed to desired channel */
          if (hasPermission(event.getMember())){
             try{
            GuildChannelUnion channel = getChannel(event.getOptions());
            channel.asTextChannel().sendMessageEmbeds(t).queue();
            /*Send a message saying that the post was successful*/
            event.getHook().editOriginal("Successfully sent Embed to channel #" + channel.getName()).queue();
            logEmbedToServerLog(event);
          }
          catch (IllegalStateException e){
            /* The code will throw a illegal state exception when the given channel is not / cannot be a TextChannel */
            event.getHook().editOriginal("The channel you gave is not a Text Channel").queue();
          }
          }
          else {
            event.getHook().editOriginal("Sorry, but you do not have permission to use this command.").queue();
          }
        }
      }  
    };
    embedThread.setName("Embed Interaction");
    embedThread.start();
  }

/******************************Embed*Commands***********************************/



  /*******************************Option*Mappings**********************************/
  public static OptionMapping findOptionMap(List<OptionMapping> list, String name){
    /* This method helps us find the option mapping for a given name. */
    /* Which will save us processing time in the future. */
    for(OptionMapping mapping : list){
      if(mapping.getName().equals(name)){
        return mapping;
      }
    }
    return null;
  }
  
  public static String getTitle(List<OptionMapping> list){
    return findOptionMap(list, "title").getAsString();
  }
  public static String getDescription(List<OptionMapping> list){
    return findOptionMap(list, "description").getAsString();
  }
  public static String getAuthor(List<OptionMapping> list){
    return findOptionMap(list, "author").getAsString();
  }
  public static String getFooter(List<OptionMapping> list){
    return findOptionMap(list, "footer").getAsString();
  }
  public static int getColor(List<OptionMapping> list){
    return findOptionMap(list, "color").getAsInt();
  }
  public static GuildChannelUnion getChannel(List<OptionMapping> list){
    return findOptionMap(list, "channel").getAsChannel();
    /* GuildChannelUnion is a type of channel class that contains all channel types */
  }
  public static ChannelType getChannelType(List<OptionMapping> list){
    return findOptionMap(list, "channel").getChannelType();
  }


  private static boolean hasPermission(Member member){
      Role botDev = member.getGuild().getRoleById("1044358875039666316");
      Role modRole = member.getGuild().getRoleById("1016048811581382676");

      if (doesMemberHaveRole(member, botDev) || doesMemberHaveRole(member, modRole)){
        return true;
      }
    else {
      return false;
    }
  }
  /*******************************Embed*Construction**********************************/
  public static MessageEmbed createEmbed(SlashCommandInteractionEvent event){
    EmbedBuilder builder = new EmbedBuilder();
    try{builder.setTitle(getTitle(event.getOptions()));}
    catch (NullPointerException e){/*Embeds cannot be blank */builder.setTitle("Empty Embed");}
    try{builder.setDescription(getDescription(event.getOptions()));}
    catch (NullPointerException e){}
    try{builder.setAuthor(getAuthor(event.getOptions()));}
    catch (NullPointerException e){}
  try{  builder.setFooter(getFooter(event.getOptions())); }
    catch (NullPointerException e) {}
 try{   builder.setColor(getColor(event.getOptions()));}
    catch (NullPointerException e){}

    /* finish the method by building out the embed */
    return builder.build();
  }


  /*******************************************Security*Methods*********************************************************/

  private static boolean doesMemberHaveRole(Member member, Role role){
    for (Role t : member.getRoles()){
      if (t.getId().equals(role.getId())){
        return true;
      }
    }
    return false;
  }




  private static Role getRoleById(List<Role> roles, String id){
    for(Role role : roles){
      if(role.getId().equals(id)){
        return role;
      }
    }
    return null;
  }






  /************************************************Logging*****************************************************************/


  private static void logEmbedToServerLog(SlashCommandInteractionEvent event){

    /* This is a method that logs the embed to the server log */
    event.getGuild().getTextChannelById("1010094270033711175").sendMessageEmbeds(createLogEmbed(event)).queue();
  }



  private static MessageEmbed createLogEmbed(SlashCommandInteractionEvent event){
    EmbedBuilder builder = new EmbedBuilder();
    /* Grab the channel name to display in the embed */
    String channelName = getChannel(event.getOptions()).getName();
    
    builder.setTitle(event.getMember().getEffectiveName() + " created a new embed for " + channelName);
    String description = "";
    try{description += "Title: " + getTitle(event.getOptions()) + "\n";}
    catch (NullPointerException e){}
    try{description += "Description: " + getDescription(event.getOptions()) + "\n";}
    catch (NullPointerException e){}
    try{description += "Author: " + getAuthor(event.getOptions()) + "\n";}
    catch (NullPointerException e){}
    try{description += "Footer: " + getFooter(event.getOptions()) + "\n";}
    catch (NullPointerException e){}
    try{description += "Color: " + String.valueOf(getColor(event.getOptions())) + "\n";}
    catch (NullPointerException e){}

    builder.setDescription(description);
    
    builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getAvatarUrl());

    builder.setTimestamp(Instant.now());

    return builder.build();
  }
}