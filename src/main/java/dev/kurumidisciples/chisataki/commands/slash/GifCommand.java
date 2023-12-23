package dev.kurumidisciples.chisataki.commands.slash;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import dev.kurumidisciples.chisataki.enums.GifEnum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GifCommand extends SlashCommand {
	private List<GifEnum> gifs;

	public GifCommand() {
		super("gif", "send a gif");
		this.gifs = List.of(
				GifEnum.ALOHA, GifEnum.ANGRY, GifEnum.BOXERS, GifEnum.BOXERS_2, GifEnum.CHINANAGO, GifEnum.CHISATO_GIGGLE, 
				GifEnum.CHISATO_SIP, GifEnum.CHISATO_SIP_2, GifEnum.CHISATO_UNAMUSED, GifEnum.COOKIE, GifEnum.DODGE, 
				GifEnum.FEED, GifEnum.FIGHT, GifEnum.HANDSHAKE, GifEnum.HEARTBEAT, GifEnum.HOP, GifEnum.HUH,
				GifEnum.KICK, GifEnum.MACHINE_GUN, GifEnum.NECK_CHOP, GifEnum.NEKO, GifEnum.SAKANA, GifEnum.SHINZOU,
				GifEnum.SHRUG, GifEnum.SKILL_DIFF, GifEnum.SLEEP, GifEnum.SPIN, GifEnum.STOP, GifEnum.SUPAKA, 
				GifEnum.TAKINA_CELEBRATION, GifEnum.TAKINA_KICK, GifEnum.TAKINA_LAUGH, GifEnum.TREADMILL, GifEnum.ZOOM
				);
	}

	private MessageEmbed getRandomGif() {
		int randomIndex = ThreadLocalRandom.current().nextInt(gifs.size());
		String gifUrl = gifs.get(randomIndex).getUrl();
		return new EmbedBuilder().setImage(gifUrl).build();
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		event.getHook().sendMessage("").setEmbeds(getRandomGif()).queue();
	}
}