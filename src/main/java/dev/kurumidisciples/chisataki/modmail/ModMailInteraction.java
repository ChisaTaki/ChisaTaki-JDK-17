package dev.kurumidisciples.chisataki.modmail;

import java.awt.Color;
import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class ModMailInteraction extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (event.getName().equals("sdfsa")) {
            event.reply(" ").addActionRow(Button.success("modmail", "ModMail")).queue();
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("modmail")) {
            event.replyModal(getModMailModal()).queue();
        }
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if (event.getModalId().equals("mailModal")) {
            Guild guild = event.getGuild();
            if (guild == null) {
                event.reply("Error: Guild not found").setEphemeral(true).queue();
                return;
            }
            event.deferReply(true).queue();

            TextChannel templateChannel = guild.getTextChannelById("1011966579610755102");
            if (templateChannel == null) {
                event.getHook().sendMessage("Error: Template channel not found").setEphemeral(true).queue();
                return;
            }

            int ticketNumber = countFiles(); // Or another method to generate unique ticket numbers

            guild.createCopyOfChannel(templateChannel)
                    .setName("Ticket-" + ticketNumber)
                    .setTopic("This is a Ticket Channel and therefore it is temporary.")
                    .setPosition(0)
                    .queue(ticketChannel -> {
                        // Build the ticket
                        Ticket ticket = TicketBuilder.buildTicket(ticketNumber, event.getInteraction(), ticketChannel.getIdLong());

                        // Set permission override
                        ticketChannel.getManager()
                                .putMemberPermissionOverride(event.getMember().getIdLong(), 137439464512L, 0L)
                                .queue();

                        // Send redirection embed
                        event.getHook().editOriginalEmbeds(getRedirectionEmbed(ticketChannel)).queue();

                        // Send message in ticket channel
                        ticketChannel.sendMessage(createContentEmbed(ticket, event.getMember())).queue();

                        // Send notification
                        sendNotification(guild, ticket);
                    }, throwable -> {
                        event.getHook().sendMessage("Error creating ticket channel").setEphemeral(true).queue();
                    });
        }
    }

    private static Modal getModMailModal() {
        TextInput subject = TextInput.create("subject", "Subject", TextInputStyle.SHORT)
                .setPlaceholder("Subject of this ticket")
                .setMinLength(10)
                .setMaxLength(100)
                .build();

        TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Your concerns go here")
                .setMinLength(10)
                .setMaxLength(1000)
                .build();

        return Modal.create("mailModal", "Contact Staff")
                .addComponents(ActionRow.of(subject), ActionRow.of(body))
                .build();
    }

    private MessageEmbed getRedirectionEmbed(TextChannel ticketChannel) {
        return new EmbedBuilder()
                .setTitle("Ticket Successfully Created")
                .setDescription("Please head to " + ticketChannel.getAsMention() + "\nA staff member will be with you shortly.")
                .setColor(new Color(144, 96, 233))
                .build();
    }

    public static int countFiles() {
        return TicketDatabaseUtils.countTickets();
    }

    private MessageCreateData createContentEmbed(Ticket ticket, Member member) {
        return new MessageCreateBuilder()
                .setContent("<@&1016048811581382676>")
                .setEmbeds(
                        new EmbedBuilder()
                                .setTitle(member.getEffectiveName() + " has Created a Ticket!")
                                .addField("Subject", ticket.getSubject(), false)
                                .addField("Body", ticket.getBody(), false)
                                .setColor(new Color(144, 96, 233))
                                .setThumbnail(member.getEffectiveAvatarUrl())
                                .build(),
                        new EmbedBuilder()
                                .setDescription("**Staff use the buttons below to handle this ticket!**")
                                .setColor(new Color(144, 96, 233))
                                .build()
                )
                .addActionRow(
                        Button.success("claim-" + ticket.getTicketNumber(), "Claim Ticket")
                                .withEmoji(Emoji.fromUnicode("U+1F4EC")),
                        Button.danger("close-" + ticket.getTicketNumber(), "Close Ticket")
                                .withEmoji(Emoji.fromUnicode("U+1F512")),
                        Button.danger("closereason-" + ticket.getTicketNumber(), "Close Ticket With Reason")
                                .withEmoji(Emoji.fromUnicode("U+1F50F"))
                )
                .build();
    }

    private void sendNotification(Guild guild, Ticket ticket) {
        TextChannel ticketLog = guild.getTextChannelById(ChannelEnum.TICKET_LOGS.getId());
        if (ticketLog == null) {
            // Handle the case where the ticket log channel doesn't exist
            return;
        }
        String format = "**Channel**\n%s\n\n**Subject**\n%s\n\n**Body**\n%s\n\n**Member**\n%s";
        TextChannel ticketChannel = guild.getTextChannelById(ticket.getTicketId());
        Member member = guild.getMemberById(ticket.getMemberId());

        if (ticketChannel == null || member == null) {
            // Handle null cases
            return;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(144, 96, 233))
                .setTitle("Ticket #" + ticket.getTicketNumber())
                .setDescription(String.format(
                        format,
                        ticketChannel.getAsMention(),
                        ticket.getSubject(),
                        ticket.getBody(),
                        member.getAsMention()
                ))
                .build();

        ticketLog.sendMessage("** NEW TICKET **").setEmbeds(embed).queue();
    }
}
