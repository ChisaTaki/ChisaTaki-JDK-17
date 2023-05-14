package dev.kurumiDisciples.chisataki.commands.slash;

import java.awt.Color;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class SendRoleEmbedsCommand extends SlashCommand {

	private static final Color EMBED_COLOR = new Color(216, 109, 127);

	public SendRoleEmbedsCommand() {
		super("send-role-embeds", "send the embeds for the roles channel (bot dev only)", Permission.VIEW_AUDIT_LOGS);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		
		TextChannel rolesChannel = event.getGuild().getTextChannelById(ChannelEnum.ROLES.getId());
		rolesChannel.sendMessage(" ").setEmbeds(getTutorialEmbed()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getShrineEmbed()).addActionRow(getShrineMenu()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getChisatakiEmbed()).addActionRow(getChistakiButton()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getServerEmbed()).addActionRow(getServerMenu()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getGroupwatchEmbed()).addActionRow(getGroupMenu()).queue();
	}
	
	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return RoleUtils.isMemberBotDev(event.getMember());
	}

	@Override
	public String getErrorMessage() {
		return "This command is reserved to bot devs.";
	}

	private MessageEmbed getTutorialEmbed() {
		MessageEmbed tutorial = new EmbedBuilder().setTitle("Role Tutorial").setDescription(
				"● To get a role, just open the dropdown menu and select the roles you wish to have!.\n\n● For example, select "
						+ Emoji.fromUnicode("U+1F4E2").getAsReactionCode()
						+ " Server Announcement to get the <@&1013809351108079636> role.")
				.setColor(EMBED_COLOR).build();

		return tutorial;
	}

	private MessageEmbed getShrineEmbed() {
		MessageEmbed shrine = new EmbedBuilder().setColor(EMBED_COLOR)
				.addField("Faction Roles", "**__Chisato's Soldier!__**", false)
				.addField(" ", "<:ChisatoTrain:1013976121253040160> - <@&1013558607213756518>", false)
				.addField(" ", "This Role provides you access to <#1013939451979911289> and other Chisato related channels.",
						false)
				.addField(" ", "**__Takina's Sakana!__**", false)
				.addField(" ", "<:TakinaTrain:1013976244884344872> - <@&1013567857075953706>", false)
				.addField(" ", "This role provides you access to <#1013939540420997262> and other Takina related channels.",
						false)
				.addField(" ", "**Note: You can only pick one of these roles so choose wisely on whom to follow!**", false)
				.build();
		return shrine;
	}

	private StringSelectMenu getShrineMenu() {
		StringSelectMenu shrine = StringSelectMenu.create("menu:role:shrine")
				.addOption("Chisato's Soldier", "chisatoSelect", null,
						Emoji.fromCustom("ChisaTakiHeart2", 1023727380038176849L, false))
				.addOption("Takina's Sakana", "takinaSelect", null, Emoji.fromCustom("Sakana", 1016650006662496326L, false))
				.setPlaceholder("Select Your Faction").setMinValues(0).build();
		return shrine;
	}

	private MessageEmbed getChisatakiEmbed() {
		MessageEmbed chisataki = new EmbedBuilder().addField("**ChisaTaki**",
				"Click the button below to join the church of ChisaTaki and get access to the exclusive ChisaTaki Chats and channels!",
				false).addField(" ", "<:ChisaTakiKiss:1013059473167888486> - <@&1010080294692458496>", false)
				.setColor(EMBED_COLOR).build();
		return chisataki;
	}

	private Button getChistakiButton() {
		return Button.of(ButtonStyle.SECONDARY, "chisaButton", "ChisaTaki Worshipper",
				Emoji.fromCustom("ChisaTakiKiss", 1014257843974721606L, false));
	}

	private MessageEmbed getServerEmbed() {
		MessageEmbed server = new EmbedBuilder().setColor(EMBED_COLOR)
				.addField("**Server Roles**", "📢 - <@&1013809351108079636> : Get pinged for server updates/announcements.",
						false)
				.addField(" ", "🎁 - <@&1013809301342662726>: Get pinged for event news/announcements.", false)
				.addField(" ",
						"<a:EDFlower:1014474116692181082> - <@&1013809402547011616>: Get pinged for special ChisaTaki announcements/updates.",
						false)
				.addField(" ", "<:WasabiNoriko:1016648327208648704> - <@&1025081700570636318> : Get pinged for groupwatches.",
						false)
				.build();
		return server;
	}

	private StringSelectMenu getServerMenu() {
		StringSelectMenu server = StringSelectMenu.create("menu:role:server").setPlaceholder("Select Server Role(s)")
				.addOption("Server Announcement", "announceSelect", null, Emoji.fromUnicode("U+1F4E2"))
				.addOption("Event Announcement", "eventSelect", null, Emoji.fromUnicode("U+1F381"))
				.addOption("ChisaTaki Announcement", "chisaSelect", null,
						Emoji.fromCustom("EDFlower", 1014474116692181082L, true))
				.addOption("Groupwatch", "groupSelect", null, Emoji.fromCustom("WasabiNoriko", 1016648327208648704L, false))
				.setMaxValues(4).setMinValues(0).build();
		return server;
	}

	private StringSelectMenu getGroupMenu() {
		StringSelectMenu group = StringSelectMenu.create("menu:role:groupwatch").setPlaceholder("Select Groupwatch Role(s)")
				.addOption("BIRDIE WING: Golf Girls' Story Season 2", "birdieSelect", Emoji.fromCustom("AoiLaugh", 981757078173524089L, false))
				.addOption("Bofuri Season 2", "bofuriSelect", Emoji.fromCustom("a_MapleNom", 692821511379222588L, true))
				.addOption("Kubo Won't Let Me Be Invisible", "kuboSelect", Emoji.fromCustom("KuboThumbsUp", 1078040901177319595L, false))
				.addOption("Magical Girl Destroyers", "magicalSelect", Emoji.fromUnicode("U+1FA84"))
				.addOption("Mobile Suit Gundam: The Witch from Mercury Season 2", "gundamSelect", Emoji.fromCustom("mercurytomato", 1026469176379973632L, false))
				.addOption("Vinland Saga Season 2", "vinlandSelect", Emoji.fromUnicode("U+2693"))
				.addOption("Yamada-kun to Lv999", "yamadaSelect", Emoji.fromUnicode("U+1F3AE"))
				.addOption("Yuri Is My Job!", "yuriSelect", Emoji.fromCustom("HimeSmile2", 1064566199037468734L, false)).setMaxValues(8).setMinValues(0).build();

		return group;
	}

	private MessageEmbed getGroupwatchEmbed() {
		MessageEmbed groupwatch = new EmbedBuilder().setTitle("Groupwatch Roles").setDescription(
				"Select the each option that represents a show. Select the show you want to be pinged on when the respective groupwatch starts. Unselect the option to remove it.")
				.setColor(EMBED_COLOR).build();
		return groupwatch;
	}
}