package dev.kurumiDisciples.chisataki.rps;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.listeners.IgnoreInteraction;
import dev.kurumiDisciples.chisataki.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class RpsInteraction extends ListenerAdapter {
	
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Thread paperCommand = new Thread() {
			public void run() {
				if (!event.getName().equals("rps")) {
					return;
				}

				event.deferReply(true).queue();
				
				if (!ChannelEnum.areCommandsAllowed(event.getChannel().getId())) {
					TextChannel chisatakiChannel = event.getGuild().getTextChannelById(ChannelEnum.CHISATAKI.getId());
					event.getHook().editOriginal("This command can only be used in " + chisatakiChannel.getAsMention()).queue();
					return;
				}

				if (event.getSubcommandName().equals("singleplayer")) {
					MessageCreateData matchStartMessage = MessageUtils.buildMessageCreateData("> So you've decided to challenge me...", RpsSinglePlayerHandler.getChallengerEmbed());
					event.getHook().sendMessage(matchStartMessage).addActionRow(RpsLogic.getRpsButtons()).queue();
				} else if (event.getSubcommandName().equals("multiplayer")) {
					
					OptionMapping opponentOption = event.getOption("challenge");
					if (IgnoreInteraction.inList(opponentOption.getAsMember())) {
						event.getHook().editOriginal("This member wishes not to be challenged by other members").queue();
					} else if (opponentOption.getAsUser().isBot()){
						event.getHook().editOriginal("This member cannot be challenged to a match.").queue();
					} else if (opponentOption.getAsMember().getId().equals(event.getMember().getId())){
						event.getHook().editOriginal("You cannot challenge yourself to a match.").queue();
					} else {
						RpsMultiPlayerHandler.startMatch(event);	
					}
				}
			}
		};
		paperCommand.setName("Paper-Rock-Thread");
		paperCommand.setPriority(5);
		paperCommand.start();
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Thread buttonThread = new Thread() {
			public void run() {
				long matchStartTime = OffsetDateTime.now().toEpochSecond();
				if (!ChannelEnum.areCommandsAllowed(event.getChannel().getId())) {
					return;
				}

				if (event.getComponentId().startsWith("btnRps-")) {
					event.deferEdit().queue();
					RpsSinglePlayerHandler.executeSingleRpsMatch(event);
				} else if (event.getComponentId().startsWith("btnRpsM-")) { 
					event.deferEdit().queue();
					event.getHook().deleteOriginal().complete();
					
					// btnRpsM-<challengerChoice>-<opponentId>
					String challengerChoice = event.getComponentId().split("-")[1].toUpperCase();
					String opponentId = event.getComponentId().split("-")[2];
					Member opponent = event.getGuild().getMemberById(opponentId);
					Member challenger = event.getMember();
					
					MessageCreateData opponentMessage = RpsMultiPlayerHandler.getOpponentMessage(challenger, opponent, matchStartTime);
					List<Button> opponentButtons = RpsLogic.getRpsButtons(challenger, opponent, challengerChoice);
					Message message = event.getGuildChannel().asTextChannel().sendMessage(opponentMessage).addActionRow(opponentButtons).complete();
					message.delete().queueAfter(10, TimeUnit.MINUTES, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
				} else if (event.getComponentId().startsWith("btnRpsMR-")) {
					// btnRpsMR-<opponentId>-<opponentChoice>-<challengerId>-<challengerChoice>
					String opponentId = event.getComponentId().split("-")[1];
					
					if (!opponentId.equals(event.getMember().getId())) {
						event.deferReply(true).queue();
						event.getHook().editOriginal("You cannot interact with this button").queue();
						return;
					}
					
					event.deferEdit().queue();
					RpsMultiPlayerHandler.handleMatchResults(event);
				}
			}
		};
		buttonThread.setName("RpsSelection-Thread");
		buttonThread.start();
	}
}