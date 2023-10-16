package dev.kurumiDisciples.chisataki.commands;

import dev.kurumiDisciples.chisataki.commands.slash.DebugCommand;
import dev.kurumiDisciples.chisataki.commands.slash.EmbedCommand;
import dev.kurumiDisciples.chisataki.commands.slash.GifCommand;
import dev.kurumiDisciples.chisataki.commands.slash.IgnoreCommand;
import dev.kurumiDisciples.chisataki.commands.slash.MentionableCommand;
import dev.kurumiDisciples.chisataki.commands.slash.PercentageCommand;
import dev.kurumiDisciples.chisataki.commands.slash.RpsCommand;
import dev.kurumiDisciples.chisataki.commands.slash.ShrineCountCommand;
import dev.kurumiDisciples.chisataki.commands.slash.SlashCommand;
import dev.kurumiDisciples.chisataki.commands.slash.TTTCommand;
import dev.kurumiDisciples.chisataki.commands.slash.TestImageCommand;
import dev.kurumiDisciples.chisataki.commands.slash.SendRoleEmbedsCommand;
import dev.kurumiDisciples.chisataki.commands.slash.SendRulesEmbed;
import dev.kurumiDisciples.chisataki.commands.user.ToggleRoleCommand;
import dev.kurumiDisciples.chisataki.commands.user.UserCommand;
import dev.kurumiDisciples.chisataki.commands.slash.ChristmasCommand;
import dev.kurumiDisciples.chisataki.enums.RoleEnum;

public class CommandBuilder {
	public static SlashCommand[] buildSlashCommands() {
		// MentionableCommand
		String biteFormat = "**Just a little bite, it shouldn't hurt!\n\n%s bites %s**";
		String biteSelfMsg = "Why would you ask to bite yourself? <:TakinaShocked:1025837092619702352>";
		String biteGifUrl = "https://media.tenor.com/jLoppoafD5EAAAAC/bite.gif";
		String biteFooter = "fixed image lol";
		SlashCommand biteCmd = new MentionableCommand("bite", "bite someone", "person to bite", biteFormat, biteSelfMsg, biteGifUrl, biteFooter);
		
		String cuddleFormat = "**%s cuddles with %s!\n\nDon't be sad anymore, you're an amazing person!**";
		String cuddleSelfMsg = "Come on, don't be shy. Ask to cuddle someone else <:ChisatoSmug:1057116491260108841>";
		String cuddleGifUrl = "https://media1.giphy.com/media/eHc6FoXCrtu3GSntos/giphy.gif";
		String cuddleFooter = "this is stolen";
		SlashCommand cuddleCmd = new MentionableCommand("cuddle", "cuddle someone", "person to cuddle", cuddleFormat, cuddleSelfMsg, cuddleGifUrl, cuddleFooter);
		
		String kissFormat = "**Just a little kiss, hope you love it!\n\n%s kisses %s**";
		String kissSelfMsg = "Why would you ask to you kiss yourself? <:TakinaDoubt:1050788470559359046>";
		String kissGifUrl = "https://cdn.discordapp.com/attachments/1043880710357917727/1070706217728606328/IMG_0452.gif";
		String kissFooter = "this was a requested feature. a yuri image for a yuri server";
		SlashCommand kissCmd = new MentionableCommand("kiss", "kiss someone", "person to kiss", kissFormat, kissSelfMsg, kissGifUrl, kissFooter);
		
		// PercentageCommand
		String cuteFormat = "**So, %s, we evaluated how cute you are! \n\n%s, you're %d%% cute! You're such a cutie pie!**";
		String cuteGifUrl = "https://media.tenor.com/3S9l9HzhGVcAAAAC/shake-kaninayuta.gif";
		String cuteFooter = "this is actually original lol";
		SlashCommand cuteCmd = new PercentageCommand("cute", "how cute are you?", "how cute are they?", cuteFormat, cuteGifUrl, cuteFooter);
		
		String susFormat = "**So, %s, we evaluated how sus you are!\n\n%s, you're %d%% sus! How sussy!**";
		String susGifUrl = "https://media.tenor.com/xduDNIZJD2wAAAAC/86anime-frederica.gif";
		String susFooter = "Not stolen from another bot at all, I promise";
		SlashCommand susCmd = new PercentageCommand("sus", "how sus are you?", "might be the imposter", susFormat, susGifUrl, susFooter);
		
		return new SlashCommand[] {
				biteCmd, cuddleCmd, kissCmd, cuteCmd, susCmd, 
				new DebugCommand(),
				new EmbedCommand(), 
				new GifCommand(), 
				new IgnoreCommand(),
				new RpsCommand(),
				new SendRoleEmbedsCommand(),
				new SendRulesEmbed(),
				new ShrineCountCommand(), 
				new TestImageCommand(),
				new ChristmasCommand(),
				new TTTCommand()
		};
	}
	
	public static UserCommand[] buildUserCommands() {
		UserCommand chisatoShrineCmd = new ToggleRoleCommand("Toggle Chisato's Heart Role", RoleEnum.CHISATO_SHRINE);
		UserCommand takinaShrineCmd = new ToggleRoleCommand("Toggle Sakana Role", RoleEnum.TAKINA_SHRINE);
		UserCommand shareholderCmd = new ToggleRoleCommand("Toggle Shareholder Role", RoleEnum.SHAREHOLDER);
		
		return new UserCommand[] {
				chisatoShrineCmd, takinaShrineCmd, shareholderCmd
		};
	}
}
