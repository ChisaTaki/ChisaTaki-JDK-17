package dev.kurumiDisciples.chisataki.commands.slash;

import java.awt.Color;
import java.util.List;

import dev.kurumiDisciples.chisataki.enums.ChannelEnum;
import dev.kurumiDisciples.chisataki.utils.RoleUtils;
import dev.kurumiDisciples.chisataki.utils.UserUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

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
		
		rolesChannel.sendMessage(" ").setEmbeds(getTutorialEmbed()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getShrineEmbed()).addActionRow(getShrineMenu()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getChisatakiEmbed()).addActionRow(getChistakiButton()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getServerEmbed()).addActionRow(getServerMenu()).queue();
		rolesChannel.sendMessage(" ").setEmbeds(getGroupwatchEmbed()).addActionRow(getGroupMenu()).queue();
		
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
				.addField(" ", "<:ChisatoCallingTakina:1056043645112959096> - <@&1107471038981361744> : Get pinged for gartic phone sessions and schedule coordination.",
						false)
				.addField(" ", "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**",
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
				.addOption("Bot Announcement", "botSelect", null, Emoji.fromCustom("KurumiGaming", 1031632598386081882L, false))
				.addOption("Groupwatch", "groupSelect", null, Emoji.fromCustom("WasabiNoriko", 1016648327208648704L, false))
				.addOption("Gartic Player", "garticSelect", null, Emoji.fromCustom("ChisatoCallingTakina", 1056043645112959096L, false))
				.setMaxValues(6).setMinValues(0).build();
		return server;
	}

	private StringSelectMenu getGroupMenu() {
		StringSelectMenu group = StringSelectMenu.create("menu:role:groupwatch").setPlaceholder("Select Groupwatch Role(s)")
				.addOption("BIRDIE WING: Golf Girls' Story Season 2", "birdieSelect", Emoji.fromCustom("AoiLaugh", 981757078173524089L, false))
				.addOption("Kubo Won't Let Me Be Invisible", "kuboSelect", Emoji.fromCustom("KuboThumbsUp", 1078040901177319595L, false))
				.addOption("Madoka Magica", "madokaSelect", Emoji.fromCustom("MadokamiStare_MM", 675807581419339790L, true))
				.addOption("Mobile Suit Gundam: The Witch from Mercury Season 2", "gundamSelect", Emoji.fromCustom("mercurytomato", 1026469176379973632L, false))
				.addOption("Oshi no Ko", "oshiSelect", Emoji.fromCustom("AiSmug", 987071360339677285L, false))
				.addOption("Vinland Saga Season 2", "vinlandSelect", Emoji.fromUnicode("U+2693"))
				.addOption("Yamada-kun to Lv999", "yamadaSelect", Emoji.fromUnicode("U+1F3AE"))
				.setMaxValues(7).setMinValues(0).build();

		return group;
	}

	private MessageEmbed getGroupwatchEmbed() {
		MessageEmbed groupwatch = new EmbedBuilder().setTitle("Groupwatch Roles").setDescription(
				"Select a show to be notified when the group watch starts or when there's a stream schedule change.\n\n"
				+ "**Note: Select ALL roles that apply. The bot will remove all unselected roles.**")
				.setColor(EMBED_COLOR).build();
		return groupwatch;
	}
}