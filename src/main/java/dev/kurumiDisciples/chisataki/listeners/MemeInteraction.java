package dev.kurumiDisciples.chisataki.listeners;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumiDisciples.chisataki.commands.slash.IgnoreCommand;
import dev.kurumiDisciples.chisataki.utils.MessageCache;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/* To do */
//1. remove the opposing emotes from the respective shrines;
public class MemeInteraction extends ListenerAdapter {

	private static final String TAKINA_BOXERS = "<:TakinaBoxers:1015393926573719593>";
	private static final String CHISATO_FOOT = "<:ChisatoFoot:1015522197156155405>";
	private static final String TAKINA_FOOT = "<:TakinaFoot:1039462047257219162>";
	private static final String MAD_DOG_TAKINA = "<:MadDogTakina:1027897140976025701>";
	private static final String CHISATAKI_KISS = "<:ChisaTakiKiss:1013059473167888486>";

	private static final ExecutorService THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(5);

	public void onMessageReceived(MessageReceivedEvent event) {
		Thread messageThread = new Thread() {
			public void run() {
				MessageCache.storeMessage(event.getMessage());
				if (event.getMessage().isWebhookMessage())
					return;
				
				if (IgnoreCommand.isMemberIgnored(event.getMember().getId())) {
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
}
