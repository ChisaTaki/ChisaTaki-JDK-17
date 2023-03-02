package dev.kurumiDisciples.chisataki.listeners;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.time.*;
import java.util.concurrent.TimeUnit;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.enums.GifEnum;
import dev.kurumiDisciples.chisataki.rps.RpsChoice;
import dev.kurumiDisciples.chisataki.rps.RpsLogic;
import dev.kurumiDisciples.chisataki.utils.RoleUtils;

import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.Timestamp;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


public class RockPaperInteraction extends ListenerAdapter {

  private ButtonInteractionEvent buttonEvent;
  /* Slash Command Interaction */
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Thread paperCommand = new Thread() {
      public void run() {
        if (!event.getName().equals("rps")) {
          return;
        }
        if (event.getSubcommandName().equals("singleplayer")){
        String channelId = event.getChannel().getId();
        if (ChannelEnum.CHISATAKI.getId().equals(channelId) || ChannelEnum.BOT_HOUSE.getId().equals(channelId)) {
          event.deferReply(true).queue();
          event.getHook().sendMessage(getMatchOpenMessage()).addActionRow(getRpsButtons()).queue();
        } else {
          event.deferReply(true).queue();
          event.getHook().editOriginal("This command can only be used in "
              + event.getGuild().getTextChannelById(ChannelEnum.CHISATAKI.getId()).getAsMention()).queue();
        }
      }
        if (event.getSubcommandName().equals("multiplayer")){
          String channelId = event.getChannel().getId();
          if (IgnoreInteraction.inList(event.getOption("challenge").getAsMember())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This member wishes not to be challenged by other members").queue();
            return;
          }
          else if (event.getOption("challenge").getAsUser().isBot()){
            event.deferReply(true).queue();
            event.getHook().editOriginal("This member cannot be challenged to a match.").queue();
            return;
          }
          else if (event.getOption("challenge").getAsMember().getId().equals(event.getMember().getId())){
            event.deferReply(true).queue();
            event.getHook().editOriginal("You cannot challenge yourself to a match.").queue();
            return;
          }
          
          event.deferReply(true).queue();
          Member opponent = event.getOption("challenge").getAsMember();

          if (ChannelEnum.CHISATAKI.getId().equals(channelId) || ChannelEnum.BOT_HOUSE.getId().equals(channelId)) {
          
          event.getHook().sendMessage(getMatchOpenMessage()).addActionRow(getRpsMultiButtons(opponent)).queue();
        } else {
          
          event.getHook().editOriginal("This command can only be used in "
              + event.getGuild().getTextChannelById(ChannelEnum.CHISATAKI.getId()).getAsMention()).queue();
        }
        }
      }
    };
    paperCommand.setName("Paper-Rock-Thread");
    paperCommand.setPriority(5);
    paperCommand.start();
  }

  private static List<Button> getRpsButtons() {
    List<Button> rpsButtons = new ArrayList<>();

    for (RpsChoice choice : RpsChoice.values()) {
      Emoji emoji = Emoji.fromUnicode(choice.getUnicode());
      Button button = Button.secondary("btnRps-" + choice, choice.toString()).withEmoji(emoji);
      rpsButtons.add(button);
    }

    return rpsButtons;
  }

  private static List<Button> getRpsMultiButtons(Member opponent) {
    List<Button> rpsButtons = new ArrayList<>();
    for (RpsChoice choice : RpsChoice.values()) {
      Emoji emoji = Emoji.fromUnicode(choice.getUnicode());
      Button button = Button.secondary("btnRpsM-" + choice + "-"+ opponent.getId(), choice.toString()).withEmoji(emoji);
      rpsButtons.add(button);
    }
    return rpsButtons;
  }

  private static List<Button> getMultiResponseButtons(Member challenger, Member opponent, String choiceChallenger) {
    List<Button> buttons = new ArrayList<>();
    for (RpsChoice choice : RpsChoice.values()) {
      Emoji emoji = Emoji.fromUnicode(choice.getUnicode());
      Button button = Button.secondary("btnRpsMR-" + choice + "-" + challenger.getId()+"-"+opponent.getId()+"-"+choiceChallenger, choice.toString()).withEmoji(emoji);
      buttons.add(button);
    }
    return buttons;
  }  


  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    Thread buttonThread = new Thread() {
      public void run() {
        buttonEvent = event;
        if (!ChannelEnum.CHISATAKI.getId().equals(event.getChannel().getId())
            && !ChannelEnum.BOT_HOUSE.getId().equals(event.getChannel().getId())) {
          return;
        }

        if (event.getComponentId().startsWith("btnRps-")) {
          event.deferEdit().queue();

          String choiceText = event.getComponentId().split("-")[1].toUpperCase();
          RpsChoice playerChoice = RpsChoice.valueOf(choiceText);
          executeRpsMatch(event, playerChoice);
        }
        else if (event.getComponentId().startsWith("btnRpsM-")){
          event.deferEdit().queue();
          event.getHook().deleteOriginal().complete();
          String choiceText = event.getComponentId().split("-")[1].toUpperCase();
          String opponentId = event.getComponentId().split("-")[2];
          Member opponent = event.getGuild().getMemberById(opponentId);
          /* 
            when someone requests a match the bot will send a non-replying message in chisataki asking if they want to play. The person than has 2 minutes to respond to the message through a accept button. If it is not responded to in time we edit the button to be disabled.
          */

          Message message = event.getGuildChannel().asTextChannel().sendMessage(getMultiMatchResponseMessage(event.getMember(), opponent)).addActionRow(getMultiResponseButtons(event.getMember(), opponent, choiceText)).complete();
          message.delete().queueAfter(10, TimeUnit.MINUTES, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        }

        else if (event.getComponentId().startsWith("btnRpsMR-")){
          String userIdFromButton = event.getComponentId().split("-")[3];
          if (!userIdFromButton.equals(event.getMember().getId())) {
            event.deferReply(true).queue();
            event.getHook().editOriginal("You cannot interact with this button").queue();
            return;
          }
          event.deferEdit().queue();
          RpsChoice choiceOpponent = RpsChoice.valueOf(event.getComponentId().split("-")[1].toUpperCase());
          RpsChoice choiceChallenger = RpsChoice.valueOf(event.getComponentId().split("-")[4].toUpperCase());
          Member opponent = event.getMember();
          Member challenger = event.getGuild().getMemberById(event.getComponentId().split("-")[2]);

          MessageEmbed embed = getRpsMultiResultsEmbed(choiceChallenger, choiceOpponent, challenger, opponent);
          event.getHook().deleteOriginal().complete();
          event.getGuildChannel().sendMessage(" ").setEmbeds(embed).queue();
        }
      }
    };
    buttonThread.setName("RpsSelection-Thread");
    buttonThread.start();
  }

  private static void executeRpsMatch(ButtonInteractionEvent event, RpsChoice playerChoice) {
    RpsChoice botChoice = RpsLogic.getBotChoice(playerChoice);
    MessageEmbed embed = getRpsResultsEmbed(playerChoice, botChoice, event.getMember());

    event.getHook().deleteOriginal().queue();
    event.getGuildChannel().sendMessage("").setEmbeds(embed).queue();
  }

  private static MessageEmbed getRpsResultsEmbed(RpsChoice playerChoice, RpsChoice botChoice, Member member) {
    int matchResult = RpsLogic.compareMatch(playerChoice, botChoice);
    String matchOutput = matchResult > 0 ? member.getUser().getAsMention() : "<@1070074991653167144>";

    String message = String.format("%s chose: **%s**\nChisaTaki Bot chose: **%s**\n\nWinner: %s",
        member.getEffectiveName(), playerChoice, botChoice, matchOutput);

    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Rock-Paper-Scissors Result");
    embedBuilder.setDescription(message);
    embedBuilder.setImage(RpsLogic.getRandomGifUrl(playerChoice, botChoice));
    embedBuilder.setColor(matchResult > 0 ? Color.green : Color.red);
    return embedBuilder.build();
  }

  private static MessageEmbed getRpsMultiResultsEmbed(RpsChoice playerChoice, RpsChoice opponentChoice, Member member, Member opponent) {
    int matchResult = RpsLogic.compareMatch(playerChoice, opponentChoice);
    boolean isTie = matchResult == 0;
    
    String message;
    if (isTie) {
      message = String.format("%s chose: **%s**\n%s: **%s**\n\nAnd it's a tie! No winner this time", 
                              member.getEffectiveName(), playerChoice, opponent.getEffectiveName(), opponentChoice);
    } else {
      String matchOutput = matchResult > 0 ? member.getUser().getAsMention() : opponent.getAsMention();
      message = String.format("%s chose: **%s**\n%s: **%s**\n\nWinner: %s", member.getEffectiveName(), 
                              playerChoice, opponent.getEffectiveName(), opponentChoice, matchOutput);
    }

    String gifUrl = isTie ? RpsLogic.TIE_GIF.getUrl() : RpsLogic.getRandomGifUrl(playerChoice, opponentChoice);
    Color color = isTie ? Color.orange : new Color(144, 96, 233);

    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Rock-Paper-Scissors Result");
    embedBuilder.setDescription(message);
    embedBuilder.setImage(gifUrl);
    embedBuilder.setColor(color);
    return embedBuilder.build();
  }
  private static MessageCreateData getMatchOpenMessage() {
    MessageCreateData t = new MessageCreateBuilder().setContent("So you've decided to challenge me...")
        .setEmbeds(new EmbedBuilder().setTitle("Rock comes first! Rock-Paper-Scissors, go!")
            .setColor(new Color(144, 96, 233)).setImage(GifEnum.ROCK_COMES_FIRST.getUrl()).build())
        .build();
    return t;
  }

  private long getUnixTimeIn10Minutes() {
      long currentTime = buttonEvent.getTimeCreated().toEpochSecond();
      System.out.println(currentTime);
      return currentTime + (10 * 60);
  }

  private MessageCreateData getMultiMatchResponseMessage(Member challenger, Member opponent){
    return new MessageCreateBuilder()
      .setContent(opponent.getAsMention() + " you have been challenged! You must respond <t:" + getUnixTimeIn10Minutes() + ":R>!")
      .setEmbeds(new EmbedBuilder().setTitle("You have been challenged by "+challenger.getEffectiveName())
                .setColor(new Color(144, 96, 233)).setImage(GifEnum.ROCK_COMES_FIRST.getUrl()).build())
      .build();
  }
}