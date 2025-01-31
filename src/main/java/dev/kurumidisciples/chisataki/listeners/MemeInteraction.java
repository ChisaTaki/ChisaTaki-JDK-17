package dev.kurumidisciples.chisataki.listeners;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.commands.slash.IgnoreCommand;
import dev.kurumidisciples.chisataki.enums.NumberEnums;
import dev.kurumidisciples.chisataki.utils.MessageCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/* To do */
//1. remove the opposing emotes from the respective shrines;
@SuppressWarnings({"null", "unused"})
public class MemeInteraction extends ListenerAdapter {

	private static final String TAKINA_BOXERS = "<:TakinaBoxers:1015393926573719593>";
	private static final String CHISATO_FOOT = "<:ChisatoFoot:1015522197156155405>";
	private static final String TAKINA_FOOT = "<:TakinaFoot:1039462047257219162>";
	private static final String MAD_DOG_TAKINA = "<:MadDogTakina:1027897140976025701>";
	private static final String CHISATAKI_KISS = "<:ChisaTakiKiss:1013059473167888486>";

	private static final ExecutorService THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(5);

	static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		Thread messageThread = new Thread() {
			public void run() {
				if (event.getChannelType().equals(ChannelType.PRIVATE)) return;
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
				} /* else if (userMessage.toLowerCase().contains("christmas")){
					List<NumberEnums> numberEnums = getNumberEnumsFromInt(ChristmasCommand.calculateDaysUntilChristmas());
					numberEnums.forEach(numEmoji -> {
						event.getMessage().addReaction(numEmoji.getEmoji()).complete();
					});
					event.getMessage().addReaction(Emoji.fromUnicode("🎄")).complete();
				} */else if (userMessage.toLowerCase().contains("supaka")){
					event.getMessage().addReaction(Emoji.fromFormatted("<a:ChisatoDrive:1015331140317499392>")).queue();
				}

			}
		};
		messageThread.setName("Message-Thread");
		messageThread.setPriority(4);
		messageThread.start();
	}
	@Deprecated
	private List<NumberEnums> getNumberEnumsFromInt(int days){
		List<NumberEnums> numberEnums = new ArrayList<>();
		for (char c : String.valueOf(days).toCharArray()){
			numberEnums.add(NumberEnums.getEnumFromInt(Integer.parseInt(String.valueOf(c))));
		}
		return numberEnums;
	}
	@Deprecated
	private static void createMessageClock(){
		executor.scheduleAtFixedRate(() -> {
			JDA jda = Main.getJDA();
			TextChannel prayerChannel = jda.getTextChannelById("1013939353434738798");
			prayerChannel.sendMessage("May all of your prayers be answered! <:ChisatakiPray2:1041721086036955286> <:ChisatakiPray1:1044650587708473364> ").queue();
			}
			, 1, 24, TimeUnit.HOURS);
	}
}
