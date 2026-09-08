package dev.kurumidisciples.chisataki.radiata.notification;

import com.openai.models.moderations.Moderation;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.radiata.service.RadiataModerationTask;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.utils.NamedAttachmentProxy;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.entities.Message;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RadiataNotificationService {
    
    static final Logger logger = LoggerFactory.getLogger(RadiataNotificationService.class);

    public static void notifyModerators(Moderation result, RadiataModerationTask task){
        TextChannel notificationChannel = task.event().getGuild().getTextChannelById(ChannelEnum.BOT_HOUSE.getId());

        Container notification = createNotificationContainer(result.categories(), task.event().getMessage(), task.getAttachment());

        logger.info("Sending notification to moderators for message ID: " + task.event().getMessage().getId());
        
        notificationChannel.sendMessageComponents(notification).useComponentsV2().queue();
    }

    private static Container createNotificationContainer(Moderation.Categories categories, Message actor, NamedAttachmentProxy image){
        return Container.of(
            Section.of(
                Thumbnail.fromUrl("https://media.discordapp.net/attachments/1076249070273843260/1478523767415378043/11309433.png"),
                TextDisplay.of("A message containing potentially harmful content was detected by Radiata."),
                TextDisplay.of("**Categories flagged:** " + formatCategories(categories))
            ),
            Separator.createDivider(Separator.Spacing.SMALL),
            
            TextDisplay.of("Image attached to the flagged message:"),
            MediaGallery.of(MediaGalleryItem.fromUrl(image.getUrl()).withSpoiler(true)),

            Separator.createDivider(Separator.Spacing.SMALL),

            ActionRow.of(
                Button.link(actor.getJumpUrl(), "Jump to Message"),
                Button.of(ButtonStyle.DANGER,"button:radiata:delete:" + actor.getId(), "Delete Message").withEmoji(Emoji.fromUnicode("U+274C"))
            )

            
        ).withAccentColor(ColorUtils.PURPLE);
    }

    private static String formatCategories(Moderation.Categories categories) {
        StringBuilder sb = new StringBuilder();
        if (categories.hate()) sb.append("`Hate` ");
        if (categories.hateThreatening()) sb.append("`Hate Threatening` ");
        if (categories.selfHarm()) sb.append("`Self-Harm` ");
        if (categories.sexual()) sb.append("`Sexual` ");
        if (categories.sexualMinors()) sb.append("`Sexual Minors` ");
        if (categories.violence()) sb.append("`Violence` ");
        if (categories.violenceGraphic()) sb.append("`Violence Graphic` ");
        return sb.toString().trim();
    }
}
