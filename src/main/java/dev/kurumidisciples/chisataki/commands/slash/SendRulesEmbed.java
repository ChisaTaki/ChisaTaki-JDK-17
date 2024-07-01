package dev.kurumidisciples.chisataki.commands.slash;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

/**
 * Keep in mind that even if the `testing` option is set to true. The original embed will reply with the newest version of the dropdown options and button interactions
 */
@SuppressWarnings("null")
public class SendRulesEmbed extends SlashCommand {

	public SendRulesEmbed() {
		super("send-rule-embed", "send the embed for the rules channel (bot dev only)", Permission.VIEW_AUDIT_LOGS);
		this.options = List.of(
				new OptionData(OptionType.BOOLEAN, "testing", "Embeds are sent to #bot-house when true, and to #rules-and-info when false", true)
				);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();

		ChannelEnum channelEnum = event.getOption("testing").getAsBoolean() ? ChannelEnum.BOT_HOUSE : ChannelEnum.RULES;
		TextChannel rulesChannel = event.getGuild().getTextChannelById(channelEnum.getId());

		rulesChannel.sendMessage(" ").setEmbeds(getInfoEmbed()).addActionRow(getSelectMenu()).addActionRow(getButtons()).queue();

		event.getHook().editOriginal("Rule embed sent successfully.").queue();
	}

	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return RoleUtils.isMemberBotDev(event.getMember());
	}

	@Override
	public String getErrorMessage() {
		return "This command is reserved to bot devs.";
	}

	private List<MessageEmbed> getInfoEmbed() {
		List<MessageEmbed> embeds = new ArrayList<MessageEmbed>();

		MessageEmbed aboutServer = new EmbedBuilder()
				.setTitle("Welcome to ChisaTaki!")
				.setThumbnail("https://cdn.discordapp.com/icons/1010078628761055234/a_31c7473cad3f9b74a96b331d9b9dbaa1?size=128")
				.setDescription("Welcome to ChisaTaki!\nWe are a server dedicated to ChisaTaki.\nIt also has a Chisato and Takina Fanclub integrated in it.\n\nLet's gather together to ~~worship~~ lead wholesome discussions and enjoy some time together with Lycoirs Recoil fans. Let's enjoy the harmony and ship Chisato and Takina all along.\n\n**Please note that we are a sister server of the below embedded Lycoris Recoil main server.**")
				.setColor(new Color(144, 96, 233))
				.build();
		embeds.add(aboutServer);

		MessageEmbed aboutSeries = new EmbedBuilder()
				.addField("About us", "This server is dedicated to **Lycoris Recoil (リコリス・リコイル)** original TV Anime Series set to be produced by A-1 Pictures and Director Shingo Adachi (SAO, To-Love Ru) featuring an original story by Asaura and Character Design by Imigimuru.", false)
				.addField("Story", "For these peaceful days――there’s a secret behind it all. A secret organization that prevents crimes: “DA - Direct Attack”. And their group of all-girl agents: “Lycoris”. \n\nThis peaceful everyday life is all thanks to these young girls.\n\nThe elite Chisato Nishikigi is the strongest Lycoris agent of all time. Alongside is Takina Inoue, the talented but mysterious Lycoris.\n\nThey work together at one of its branches–Café LycoReco.\nHere, the orders this café takes range from coffee and sweets to childcare, shopping, teaching Japanese to foreign students, etc.\n\nThe free-spirited and optimistic pacifist, Chisato. And the cool-headed and efficient Takina.\n\nThe chaotic everyday lives of this mismatched duo begin!", false)
				.setColor(new Color(144, 96, 233))
				.build();
		embeds.add(aboutSeries);
		return embeds;
	}

	private SelectMenu getSelectMenu() {
		StringSelectMenu menu = StringSelectMenu.create("menu:info")
				.setPlaceholder("Use this menu to learn more about our server.")
				.addOption(/*label*/"Church Staff", /*value*/"adminSelect", /*description*/"Select to learn about our lovely staff.", Emoji.fromCustom("LycoReco", 993444445741645845L, false))
				.addOption(/*label*/"Bar4bidden", /*value*/ "sisterSelect", "Select to view our translation server.", Emoji.fromCustom("KurumiPat", 1159555167973277847L, false))
				.addOption(/*label*/"Shrine Info", /*value*/ "shrineSelect", /*description*/"Select to learn more about the Shrine Channels", Emoji.fromUnicode("U+26E9"))
				.addOption("XP System", "xpSystem", "Select to learn about our XP System.", Emoji.fromCustom("YellowSpiderLily", 997605599531507873L, false))
				.build();
		return menu;
	}

	private List<Button> getButtons() {
		return List.of(
				Button.secondary("rules", "Server Rules").withEmoji(Emoji.fromCustom("LycorisLogo", 994615569833791498L, false)),
				Button.secondary("strike", "Strike System").withEmoji(Emoji.fromCustom("LycorisFlower1", 994650226453397634L, false)),
				Button.secondary("modmail", "Contact Staff").withEmoji(Emoji.fromUnicode("U+1F4E8"))
				);
	}
}
