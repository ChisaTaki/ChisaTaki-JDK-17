package dev.kurumiDisciples.chisataki.commands.slash;

public class PercentageBuilder {
	public static SlashCommand[] buildCommands() {
		String susFormat = "**So, %s, we evaluated how sus you are!\n\n%s, you're %d%% sus! How sussy!**";
		String susGifUrl = "https://media.tenor.com/xduDNIZJD2wAAAAC/86anime-frederica.gif";
		String susFooter = "Not stolen from another bot at all, I promise";
		PercentageCommand susCommand = new PercentageCommand("sus", "how sus are you?", "might be the imposter", susFormat, susGifUrl, susFooter);
		

		String cuteFormat = "**So, %s, we evaluated how cute you are! \n\n%s, you're %d%% cute! You're such a cutie pie!**";
		String cuteGifUrl = "https://media.tenor.com/3S9l9HzhGVcAAAAC/shake-kaninayuta.gif";
		String cuteFooter = "this is actually original lol";
		PercentageCommand cuteCommand = new PercentageCommand("cute", "how cute are you?", "how cute are they?", cuteFormat, cuteGifUrl, cuteFooter);
		
		return new SlashCommand[] {susCommand, cuteCommand};
	}

}
