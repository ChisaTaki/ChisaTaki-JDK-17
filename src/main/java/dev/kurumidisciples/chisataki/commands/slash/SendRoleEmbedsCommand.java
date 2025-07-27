package dev.kurumidisciples.chisataki.commands.slash;

import java.awt.Color;
import java.util.List;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.utils.RoleUtils;
import dev.kurumidisciples.chisataki.utils.UserUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings({"null", "unused"})
public class SendRoleEmbedsCommand extends SlashCommand {

	private static final Color EMBED_COLOR = new Color(216, 109, 127);

	public SendRoleEmbedsCommand() {
		super("send-role-embeds", "send the embeds for the roles channel (bot dev only)", Permission.VIEW_AUDIT_LOGS);
		this.options = List.of(
				new OptionData(OptionType.BOOLEAN, "testing", "Embeds are sent to #bot-house when true, and to #roles when false", true)
				);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		
		ChannelEnum channelEnum = event.getOption("testing").getAsBoolean() ? ChannelEnum.BOT_HOUSE : ChannelEnum.ROLES;
		TextChannel rolesChannel = event.getGuild().getTextChannelById(channelEnum.getId());
		
		rolesChannel.sendMessageComponents(getTutorialContainer()).useComponentsV2().queue(); // uses the new components version
		rolesChannel.sendMessageComponents(getShrineContainer()).useComponentsV2().queue();
		rolesChannel.sendMessageComponents(getChisatakiContainer()).useComponentsV2().queue();
		rolesChannel.sendMessageComponents(getServerContainer()).useComponentsV2().queue();
		rolesChannel.sendMessageComponents(getGroupwatchContainer()).useComponentsV2().queue();
		
		event.getHook().editOriginal("Role embeds sent successfully.").queue();
	}
	
	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return RoleUtils.isMemberBotDev(event.getMember());
	}

	@Override
	public String getErrorMessage() {
		return "This command is reserved to bot devs.";
	}

	@Deprecated
	private MessageEmbed getTutorialEmbed() {
		MessageEmbed tutorial = new EmbedBuilder().setTitle("Role Tutorial").setDescription(
				"● To add a role, open the dropdown menu and select the roles you wish to have!\n\n● For example, select "
						+ Emoji.fromUnicode("U+1F4E2").getAsReactionCode()
						+ " Server Announcement to get the <@&1013809351108079636> role.\n\n"
						+ "● To remove a role, open the dropdown menu and unselect the undesired roles.\n\n"
						+ "● Warning: Discord will unselect all roles when opening the dropdown after a client restart (i.e.: when using `CTRL + R`).\n\n"
						+ "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**")
				.setColor(EMBED_COLOR).build();

		return tutorial;
	}

	private Container getTutorialContainer() {
		return Container.of(
			TextDisplay.of("● To add a role, open the dropdown menu and select the roles you wish to have!\n\n● For example, select "
						+ Emoji.fromUnicode("U+1F4E2").getAsReactionCode()
						+ " Server Announcement to get the <@&1013809351108079636> role.\n\n"
						+ "● To remove a role, open the dropdown menu and unselect the undesired roles.\n\n"
						+ "● Warning: Discord will unselect all roles when opening the dropdown after a client restart (i.e.: when using `CTRL + R`).\n\n"
						+ "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**")
		).withAccentColor(EMBED_COLOR);
	}

	@Deprecated
	private MessageEmbed getShrineEmbed() {
		MessageEmbed shrine = new EmbedBuilder().setColor(EMBED_COLOR)
				.addField("Faction Roles", "**__Chisato's Soldier!__**", false)
				.addField(" ", "<:ChisatoTrain:1013976121253040160> - <@&1013558607213756518>", false)
				.addField(" ", "This Role provides you access to `#chisato-shrine` (<#1013939451979911289>) and other Chisato related channels.",
						false)
				.addField(" ", "**__Takina's Sakana!__**", false)
				.addField(" ", "<:TakinaTrain:1013976244884344872> - <@&1013567857075953706>", false)
				.addField(" ", "This role provides you access to `#takina-shrine` (<#1013939540420997262>) and other Takina related channels.",
						false)
				.addField(" ", "**Note: You can only pick one of these roles so choose wisely on whom to follow!**", false)
				.build();
		return shrine;
	}

	private Container getShrineContainer(){
		return Container.of(
			TextDisplay.of("Faction Roles\n**__Chisato's Soldier!__**\n<:ChisatoTrain:1013976121253040160> - <@&1013558607213756518>\nThis Role provides you access to `#chisato-shrine` (<#1013939451979911289>) and other Chisato related channels.\n\n**__Takina's Sakana!__**\n<:TakinaTrain:1013976244884344872> - <@&1013567857075953706>\nThis role provides you access to `#takina-shrine` (<#1013939540420997262>) and other Takina related channels.\n\n**Note: You can only pick one of these roles so choose wisely on whom to follow!**"),
			ActionRow.of(
				StringSelectMenu.create("menu:role:shrine")
					.addOption("Chisato's Soldier", "chisatoSelect", null, Emoji.fromCustom("Chinanago", 1120915801680134185L, false))
					.addOption("Takina's Sakana", "takinaSelect", null, Emoji.fromCustom("Sakana", 1016650006662496326L, false))
					.setPlaceholder("Select Your Faction").setMinValues(0).build()
				).withUniqueId(1) // used to delete the row later if needed
		).withAccentColor(EMBED_COLOR);
	}

	@Deprecated
	private StringSelectMenu getShrineMenu() {
		StringSelectMenu shrine = StringSelectMenu.create("menu:role:shrine")
				.addOption("Chisato's Soldier", "chisatoSelect", null,
						Emoji.fromCustom("Chinanago", 1120915801680134185L, false))
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

	private Container getChisatakiContainer(){
		return Container.of(
			TextDisplay.of("**ChisaTaki**\nClick the button below to join the church of ChisaTaki and get access to the exclusive ChisaTaki Chats and channels!"),
			ActionRow.of(
				Button.of(ButtonStyle.SECONDARY, "chisaButton", "ChisaTaki Worshipper",
					Emoji.fromCustom("ChisaTakiKiss", 1014257843974721606L, false))
			).withUniqueId(2) // used to delete the row later if needed
		);
	}

	@Deprecated
	private MessageEmbed getServerEmbed() {
		MessageEmbed server = new EmbedBuilder().setColor(EMBED_COLOR)
				.addField("**Server Roles**", "📢 - <@&1013809351108079636> : Get pinged for server announcements/updates.",
						false)
				.addField(" ", "🎁 - <@&1013809301342662726>: Get pinged for event news/announcements.", false)
				.addField(" ",
						"<a:EDFlower:1014474116692181082> - <@&1013809402547011616>: Get pinged for special ChisaTaki announcements/updates.",
						false)
				.addField(" ", "<:KurumiGaming:1031632598386081882> - <@&1107470054829862972> : Get pinged for <@" + UserUtils.CHISATAKI_BOT_ID + "> feature updates and maintenance status",
						false)
				.addField(" ", "<:WasabiNoriko:1016648327208648704> - <@&1025081700570636318> : Get pinged for groupwatches schedule coordination (usually when a new season begins).",
						false)
				.addField(" ", "<:TakinaCheckingYourPhone:1114920842564993144> - <@&1139737288520249414> : Get pinged when a new manga chapter of Lycoris Recoil gets released.",
						false)
				.addField(" ", "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**",
						false)
				.build();
		return server;
	}

	private Container getServerContainer(){
		return Container.of(
			TextDisplay.of("**Server Roles**\n📢 - <@&1013809351108079636> : Get pinged for server announcements/updates.\n\n🎁 - <@&1013809301342662726>: Get pinged for event news/announcements.\n\n<a:EDFlower:1014474116692181082> - <@&1013809402547011616>: Get pinged for special ChisaTaki announcements/updates.\n\n<:KurumiGaming:1031632598386081882> - <@&1107470054829862972> : Get pinged for <@" + UserUtils.CHISATAKI_BOT_ID + "> feature updates and maintenance status\n\n<:WasabiNoriko:1016648327208648704> - <@&1025081700570636318> : Get pinged for groupwatches schedule coordination (usually when a new season begins).\n\n<:TakinaCheckingYourPhone:1114920842564993144> - <@&1139737288520249414> : Get pinged when a new manga chapter of Lycoris Recoil gets released.\n\n**Note: Select ALL roles that apply. The bot will remove all unselected roles.**"),
			ActionRow.of(
				StringSelectMenu.create("menu:role:server").setPlaceholder("Select Server Role(s)")
				.addOption("Server Announcement", "announceSelect", null, Emoji.fromUnicode("U+1F4E2"))
				.addOption("Event Announcement", "eventSelect", null, Emoji.fromUnicode("U+1F381"))
				.addOption("ChisaTaki Announcement", "chisaSelect", null,
						Emoji.fromCustom("EDFlower", 1014474116692181082L, true))
				.addOption("Bot Announcement", "botSelect", null, Emoji.fromCustom("KurumiGaming", 1031632598386081882L, false))
				.addOption("Groupwatch", "groupSelect", null, Emoji.fromCustom("WasabiNoriko", 1016648327208648704L, false))
				.addOption("Manga Updates", "mangaSelect", null, Emoji.fromCustom("TakinaCheckingYourPhone", 1114920842564993144L, false))
				.setMaxValues(6).setMinValues(0).build()
			).withUniqueId(3) // used to delete the row later if needed
		).withAccentColor(EMBED_COLOR);
	}

	@Deprecated
	private StringSelectMenu getServerMenu() {
		StringSelectMenu server = StringSelectMenu.create("menu:role:server").setPlaceholder("Select Server Role(s)")
				.addOption("Server Announcement", "announceSelect", null, Emoji.fromUnicode("U+1F4E2"))
				.addOption("Event Announcement", "eventSelect", null, Emoji.fromUnicode("U+1F381"))
				.addOption("ChisaTaki Announcement", "chisaSelect", null,
						Emoji.fromCustom("EDFlower", 1014474116692181082L, true))
				.addOption("Bot Announcement", "botSelect", null, Emoji.fromCustom("KurumiGaming", 1031632598386081882L, false))
				.addOption("Groupwatch", "groupSelect", null, Emoji.fromCustom("WasabiNoriko", 1016648327208648704L, false))
				.addOption("Manga Updates", "mangaSelect", null, Emoji.fromCustom("TakinaCheckingYourPhone", 1114920842564993144L, false))
				.setMaxValues(6).setMinValues(0).build();
		return server;
	}

	@Deprecated
	private StringSelectMenu getGroupMenu() {
		StringSelectMenu group = StringSelectMenu.create("menu:role:groupwatch").setPlaceholder("Select Groupwatch Role(s)")
				.addOption("Ave Mujica", "aveSelect", Emoji.fromCustom("BANdori", 432981165310345216L, false))
				.setMaxValues(1).setMinValues(0).build();

		return group;
	}

	private Container getGroupwatchContainer(){
		return Container.of(
			TextDisplay.of("Select a show to be notified when the group watch starts or when there's a stream schedule change.\n\n"
					+ "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**"),
			ActionRow.of(
				StringSelectMenu.create("menu:role:groupwatch")
					.addOption("Ave Mujica", "aveSelect", null, Emoji.fromCustom("BANdori", 432981165310345216L, false))
					.setPlaceholder("Select Groupwatch Role(s)").setMaxValues(1).setMinValues(0).build()
			).withUniqueId(4) // used to delete the row later if needed
		).withAccentColor(EMBED_COLOR);
	}

	@Deprecated
	private MessageEmbed getGroupwatchEmbed() {
		MessageEmbed groupwatch = new EmbedBuilder().setTitle("Groupwatch Roles").setDescription(
				"Select a show to be notified when the group watch starts or when there's a stream schedule change.\n\n"
				+ "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**")
				.setColor(EMBED_COLOR).build();
		return groupwatch;
	}
}