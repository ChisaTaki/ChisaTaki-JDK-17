package dev.kurumidisciples.chisataki.modmail;

import java.awt.Color;
import java.awt.Desktop.Action;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public class ModMailInteraction extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ModMailInteraction.class);

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (event.getName().equals("sdfsa")) {
            event.reply(" ").addComponents(ActionRow.of(Button.success("modmail", "ModMail"))).queue();
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
    if (!event.getModalId().equals("mailModal")) {
        return;
    }

    Guild guild = event.getGuild();

    if (guild == null) {
        event.reply("Error: Guild not found. How did that happen?")
                .setEphemeral(true)
                .queue();
        return;
    }

    event.deferReply(true).queue();

    TextChannel templateChannel =
            guild.getTextChannelById("1011966579610755102");

    if (templateChannel == null) {
        event.getHook()
                .editOriginal("Error: Template channel not found. Contact Bot Dev.")
                .queue();
        return;
    }

    int ticketNumber = countFiles();

    // Keep track of the channel so we can delete it if something fails
    AtomicReference<TextChannel> createdChannel = new AtomicReference<>();

    CompletableFuture<Void> ticketCreation =
            guild.createCopyOfChannel(templateChannel)
                    .setName("Ticket-" + ticketNumber)
                    .setTopic("This is a Ticket Channel and therefore it is temporary.")
                    .setPosition(0)
                    .submit()

                    .thenCompose(ticketChannel -> {
                        createdChannel.set(ticketChannel);

                        Ticket ticket = TicketBuilder.buildTicket(
                                ticketNumber,
                                event.getInteraction(),
                                ticketChannel.getIdLong()
                        );

                        // Set permissions first
                        return ticketChannel.getManager().putMemberPermissionOverride(event.getUser().getIdLong(),137439464512L,0L).submit()

                                // Send the ticket message
                                .thenCompose(ignored ->
                                    ticketChannel.sendMessage(createContentEmbed(ticket,event.getMember())).submit()
                                )

                                // Update the user's ephemeral response
                                .thenCompose(ignored ->
                                    event.getHook().editOriginalEmbeds(getRedirectionEmbed(ticketChannel)).submit()
                                )

                                .thenAccept(ignored -> {
                                    sendNotification(guild, ticket);
                                });
                    })

                    // Whole operation gets 15 seconds
                    .orTimeout(15, TimeUnit.SECONDS);

    ticketCreation.whenComplete((ignored, throwable) -> {
        if (throwable == null) {
            logger.info("Ticket {} created successfully.", ticketNumber);
            return;
        }

        Throwable cause = throwable;

        // CompletableFuture often wraps the actual exception
        if (cause instanceof CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }

        logger.error(
                "Ticket {} creation failed: {}",
                ticketNumber,
                cause.getMessage(),
                cause
        );

        String errorMessage;

        if (cause instanceof TimeoutException) {
            errorMessage =
                    "Ticket creation timed out. Something went wrong while creating your ticket. "
                    + "Please try again or contact the bot developer.";
        } else if (cause instanceof NullPointerException) {
            errorMessage =
                    "One or more required fields were missing from the modal. "
                    + "Please try again or contact the bot developer.";
        } else {
            errorMessage =
                    "Something went wrong while creating your ticket. "
                    + "Please try again or contact the bot developer.";
        }

        event.getHook()
                .editOriginal(errorMessage)
                .queue(
                        null,
                        replyError -> logger.error(
                                "Could not send ticket error message.",
                                replyError
                        )
                );

        // Clean up the half-created channel
        TextChannel ticketChannel = createdChannel.get();

        if (ticketChannel != null) {
            ticketChannel.delete().queue(
                    null,
                    deleteError -> logger.error(
                            "Could not clean up failed ticket channel {}.",
                            ticketChannel.getId(),
                            deleteError
                    )
            );
        }
    });
}

    private static Modal getModMailModal() {
        StringSelectMenu subject = StringSelectMenu.create("menu:subject")
                .setPlaceholder("Select a concern")
                .addOption("Report a user", "Report a user")
                .addOption("Appeal a punishment", "Appeal a punishment")
                .addOption("Request help", "Request help")
                .addOption("Other", "Other")
                .setRequired(true)
                .build();

        TextInput body = TextInput.create("body", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Body of your message")
                .setMinLength(10)
                .setMaxLength(1000)
                .build();

        return Modal.create("mailModal", "Contact Staff")
                .addComponents(
                    Label.of("Subject", subject),
                    Label.of("Body", body)
                )
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
                .setComponents(
                    ActionRow.of(
                        Button.success("claim-" + ticket.getTicketNumber(), "Claim Ticket")
                                .withEmoji(Emoji.fromUnicode("U+1F4EC")),
                        Button.danger("close-" + ticket.getTicketNumber(), "Close Ticket")
                                .withEmoji(Emoji.fromUnicode("U+1F512")),
                        Button.danger("closereason-" + ticket.getTicketNumber(), "Close Ticket With Reason")
                                .withEmoji(Emoji.fromUnicode("U+1F50F"))
                ))
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
