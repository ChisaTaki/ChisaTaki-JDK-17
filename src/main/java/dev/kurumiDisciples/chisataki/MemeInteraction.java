package dev.kurumiDisciples.chisataki;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.listeners.IgnoreInteraction;
import dev.kurumiDisciples.chisataki.utils.ColorUtils;
import dev.kurumiDisciples.chisataki.utils.UserUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

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


	private static final ExecutorService THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(5);

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
				if (event.getName().equals("sus")) {
					if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())) {
						handleForbiddenAccess(event);
						return;
					}

					event.deferReply(true).queue();

					OptionMapping userOption = event.getOption("user");
					if (userOption == null || event.getUser().getId().equals(userOption.getAsUser().getId())) {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(" ").setEmbeds(buildSus(event.getMember())).queue();
					} else if (IgnoreInteraction.inList(userOption.getAsMember())) {
						event.getHook().editOriginal("This member wishes not to be pinged by other members").queue();
					} else {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(userOption.getAsMember().getAsMention()).setEmbeds(buildSus(userOption.getAsMember())).queue();
					}
				} else if (event.getName().equals("cute")) {
					if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
						handleForbiddenAccess(event);
						return;
					}

					event.deferReply(true).queue();

					OptionMapping userOption = event.getOption("user");
					if (userOption == null || userOption.getAsMember().getId().equals(event.getMember().getId())) {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(" ").setEmbeds(buildCute(event.getMember())).queue();
					} else if (IgnoreInteraction.inList(userOption.getAsMember())) {
						event.getHook().editOriginal("This member wishes not to be pinged by other members").queue();
					} else {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(userOption.getAsMember().getAsMention()).setEmbeds(buildCute(userOption.getAsMember())).queue();
					}
				} else if (event.getName().equals("cuddle")) {
					if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())){
						handleForbiddenAccess(event);
						return;
					}

					event.deferReply(true).queue();

					OptionMapping userOption = event.getOption("user");
					if (userOption.getAsMember().getId().equals(event.getMember().getId())) {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage("Why hug yourself when ChisaTaki can hug you <:ChisatoSmugArkward:1055641564682129469>")
							.setEmbeds(buildCuddle(event.getGuild().getMemberById(UserUtils.CHISATAKI_BOT_ID), event.getMember())).queue();
					} else if (IgnoreInteraction.inList(userOption.getAsMember())) {
						event.getHook().editOriginal("This member wishes not to be pinged by other members").queue();
					} else {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(userOption.getAsMember().getAsMention()).setEmbeds(buildCuddle(event.getMember(), userOption.getAsMember())).queue();  
					}
				} else if (event.getName().equals("bite")) {
					if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())) {
						handleForbiddenAccess(event);
						return;
					}
					
					event.deferReply(true).queue();

					OptionMapping userOption = event.getOption("user");
					if (userOption.getAsMember().getId().equals(event.getMember().getId())) {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(event.getMember().getAsMention() + " Why would you ask to bite yourself? <:TakinaShocked:1025837092619702352>").queue();
					} else if (IgnoreInteraction.inList(userOption.getAsMember())) {
						event.getHook().editOriginal("This member wishes not to be pinged by other members").queue();
					} else {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(" ").setEmbeds(buildBite(event.getMember(), userOption.getAsMember())).queue();  
					}
				} else if (event.getName().equals("kiss")) {
					if (!event.getChannel().getId().equals(ChannelEnum.BOT_CHANNEL.getId())) {
						handleForbiddenAccess(event);
						return;
					}

					event.deferReply(true).queue();
					
					OptionMapping userOption = event.getOption("user");
					if (userOption.getAsMember().getId().equals(event.getMember().getId())) {
						event.getHook().deleteOriginal().queue();
						event.getGuildChannel().asTextChannel().sendMessage(event.getMember().getAsMention() + " Why would you ask to you kiss yourself? <:TakinaDoubt:1050788470559359046>").queue();
					} else if (IgnoreInteraction.inList(userOption.getAsMember())) {
						event.getHook().editOriginal("This member wishes not to be pinged by other members").queue();
					} else {
						event.getHook().deleteOriginal().queue();
						Member memberToKiss = userOption.getAsMember();
						Member kissing = event.getMember();
						event.getGuildChannel().asTextChannel().sendMessage(memberToKiss.getAsMention()).setEmbeds(buildKiss(kissing, memberToKiss)).queue();
					}
				}
			}
		};

		slashCommand.setName("SlashCommand-Thread");
		slashCommand.setPriority(2);
		slashCommand.start();
	}

	private void handleForbiddenAccess(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		event.getHook().editOriginal("This command can only be used in <#" + ChannelEnum.BOT_CHANNEL.getId() + ">").queue();
	}

	public MessageEmbed buildSus(Member member) {
		return new EmbedBuilder()
				.setColor(ColorUtils.PURPLE)
				.setImage("https://media.tenor.com/xduDNIZJD2wAAAAC/86anime-frederica.gif")
				.setDescription("**So, "+member.getEffectiveName()+", we evaluated how sus you are! \n\n"+member.getEffectiveName()+", you're "+String.valueOf(new Random().nextInt(101))+"% sus! How sussy!**")
				.setFooter("not stolen from another bot at all, i promise")
				.build();
	}

	public MessageEmbed buildCute(Member member) {
		return new EmbedBuilder()
				.setColor(ColorUtils.PURPLE)
				.setImage("https://media.tenor.com/3S9l9HzhGVcAAAAC/shake-kaninayuta.gif")
				.setDescription("**So, "+member.getEffectiveName()+", we evaluated how cute you are! \n\n"+member.getEffectiveName()+", you're "+String.valueOf(new Random().nextInt(101))+"% cute! You're such a cutie pie!**")
				.setFooter("this is actually original lol")
				.build();
	}

	public MessageEmbed buildCuddle(Member memberHugging, Member memberCuddled) {
		return new EmbedBuilder()
				.setColor(ColorUtils.PURPLE)
				.setImage("https://media.tenor.com/kCZjTqCKiggAAAAC/hug.gif")
				.setDescription("**"+memberHugging.getEffectiveName()+" cuddles with "+memberCuddled.getEffectiveName()+"!\n\nDon't be sad anymore, you're an amazing person!**")
				.setFooter("this is stolen")
				.build();
	}

	public MessageEmbed buildBite(Member memberBiting, Member memberBitten) {
		return new EmbedBuilder()
				.setColor(ColorUtils.PURPLE)
				.setImage("https://media.tenor.com/jLoppoafD5EAAAAC/bite.gif")
				.setTitle("Just a little bite, " + memberBitten.getEffectiveName() + ", it shouldn't hurt!")
				.setAuthor(memberBiting.getEffectiveName() + " bites " + memberBitten.getEffectiveName())
				.setFooter("fixed image lol")
				.build();
	}

	private MessageEmbed buildKiss(Member memberKissing, Member memberKissed) {
		return new EmbedBuilder()
				.setColor(ColorUtils.PURPLE)
				.setAuthor(memberKissing.getEffectiveName() + " kisses " + memberKissed.getEffectiveName())
				.setTitle("Just a little kiss, " + memberKissed.getEffectiveName() + ", hope you love it!")
				.setImage("https://cdn.discordapp.com/attachments/1043880710357917727/1070706217728606328/IMG_0452.gif")
				.setFooter("this was a requested feature. a yuri image for a yuri server")
				.build();
	}
}
