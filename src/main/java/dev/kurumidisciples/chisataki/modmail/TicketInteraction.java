package dev.kurumidisciples.chisataki.modmail;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.enums.StatusType;
import dev.kurumidisciples.chisataki.modmail.json.ChannelEncoded;
import dev.kurumidisciples.chisataki.modmail.json.MessagesEncoded;
import dev.kurumidisciples.chisataki.modmail.json.ServerEncoded;
import dev.kurumidisciples.chisataki.utils.HTMLUtils;
import dev.kurumidisciples.chisataki.utils.MessageCache;
import dev.kurumidisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;

public class TicketInteraction extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String channelName = event.getChannel().getName();
        if (!channelName.toLowerCase().startsWith("ticket-")) {
            return;
        }

        if (!RoleUtils.isMemberStaff(event.getMember())) {
            event.reply("This action can only be executed by staff members.").setEphemeral(true).queue();
            return;
        }

        String[] componentIdParts = event.getComponentId().split("-");
        String action = componentIdParts[0];
        String ticketNumberStr = componentIdParts[1];
        int ticketNumber = Integer.parseInt(ticketNumberStr);

        switch (action) {
            case "claim":
                handleClaim(event, ticketNumber);
                break;
            case "closereason":
                event.replyModal(getReasonModal(ticketNumberStr)).queue();
                break;
            case "close":
                handleClose(event, ticketNumber);
                break;
            default:
                // Unknown action
                break;
        }
    }

    @SuppressWarnings("null")
    private void handleClaim(ButtonInteractionEvent event, int ticketNumber) {
        Ticket ticket = new Ticket(ticketNumber);

        if (ticket.hasStaff()) {
            event.reply("This ticket has already been claimed by a staff member.").setEphemeral(true).queue();
            return;
        }

        ticket.setStaff(event.getMember().getId()).setStatus(StatusType.ANSWERED);
        event.reply(event.getMember().getAsMention() + " has claimed this ticket!").queue();

        // Disable the claim ticket button
        event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds().get(0))
                .setActionRow(
                        event.getMessage().getButtons().get(0).asDisabled(),
                        event.getMessage().getButtons().get(1),
                        event.getMessage().getButtons().get(2))
                .queue();
    }

    private void handleClose(ButtonInteractionEvent event, int ticketNumber) {
        Ticket ticket = new Ticket(ticketNumber);

        if (ticket.isClosed()) {
            event.reply("This Ticket has already been closed.").setEphemeral(true).queue();
            return;
        }

        ticket.setStatus(StatusType.CLOSED);
        handleClosingTicket(ticket, event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if (event.getModalId().startsWith("reasonModal")) {
            String[] modalIdParts = event.getModalId().split("-");
            int ticketNumber = Integer.parseInt(modalIdParts[1]);
            @SuppressWarnings("null")
            String reason = event.getValue("reason").getAsString();

            Ticket ticket = new Ticket(ticketNumber);

            if (ticket.isClosed()) {
                event.reply("This Ticket has already been closed.").setEphemeral(true).queue();
                return;
            }

            ticket.setStatus(StatusType.CLOSED).setReason(reason);
            handleClosingTicket(ticket, event);
        }
    }

    @SuppressWarnings("null")
    private void handleClosingTicket(Ticket ticket, ButtonInteractionEvent event) {
        if (!ticket.hasStaff()) {
            ticket.setStaff(event.getMember().getId());
        }

        TextChannel channel = event.getChannel().asTextChannel();
        Guild guild = event.getGuild();

        // Remove user's permission from the channel
        channel.getManager().removePermissionOverride(ticket.getMemberId()).queue();

        // Retrieve messages asynchronously
        channel.sendMessage("*Closing Ticket...*").complete();

        List<Message> messages = MessageCache.getMessages(channel);

        if (messages != null && !messages.isEmpty()) {
            String transcript = archive(messages);
            String transcriptName = "ticket-" + ticket.getTicketNumber();
            String htmlTranscript = generateHTMLTranscript(channel, messages);
            List<Attachment> attachments = getAttachments(messages);

            sendTranscript(ticket, guild, transcript, transcriptName, htmlTranscript, attachments);
        }

            // Inform the user and delete the channel after a delay
            event.reply("Ticket closed with no reason.").queue();
            channel.delete().queueAfter(10, TimeUnit.SECONDS);
        
    }

    @SuppressWarnings("null")
    private void handleClosingTicket(Ticket ticket, ModalInteractionEvent event) {
        if (!ticket.hasStaff()) {
            ticket.setStaff(event.getMember().getId());
        }

        TextChannel channel = event.getChannel().asTextChannel();
        Guild guild = event.getGuild();

        // Remove user's permission from the channel
        channel.getManager().removePermissionOverride(ticket.getMemberId()).queue();

        // Retrieve messages asynchronously
        channel.sendMessage("*Closing Ticket...*").complete();

        List<Message> messages = MessageCache.getMessages(channel);

        if (messages != null && !messages.isEmpty()) {
            String transcript = archive(messages);
            String transcriptName = "ticket-" + ticket.getTicketNumber();
            String htmlTranscript = generateHTMLTranscript(channel, messages);
            List<Attachment> attachments = getAttachments(messages);

            sendTranscript(ticket, guild, transcript, transcriptName, htmlTranscript, attachments);
        }
        
        event.reply("Ticket closed with reason: " + ticket.getReason()).queue();
        channel.delete().queueAfter(10, TimeUnit.SECONDS);
    }

    private Modal getReasonModal(String ticketNumber) {
        TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.SHORT)
                .setMinLength(5)
                .build();
        return Modal.create("reasonModal-" + ticketNumber, "Ticket Reason")
                .addComponents(ActionRow.of(reason))
                .build();
    }

    @SuppressWarnings("null")
    private String archive(List<Message> messages) {
        StringBuilder data = new StringBuilder();
        String format = "%s#%s: %s\n";
        String formatReply = "%s#%s replying to %s#%s: %s\n";

        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message.getReferencedMessage() != null) {
                data.append(String.format(formatReply,
                        message.getAuthor().getName(),
                        message.getAuthor().getDiscriminator(),
                        message.getReferencedMessage().getAuthor().getName(),
                        message.getReferencedMessage().getAuthor().getDiscriminator(),
                        message.getContentStripped()));
            } else {
                data.append(String.format(format,
                        message.getAuthor().getName(),
                        message.getAuthor().getDiscriminator(),
                        message.getContentStripped()));
            }
        }

        return data.toString();
    }

    private List<Message> combineMessages(List<Message> messages1, List<Message> messages2) {
        List<Message> combined = new ArrayList<>(messages1);
        for (Message message : messages2) {
            if (!combined.contains(message)) {
                combined.add(message);
            }
        }
        return combined;
    }

    private List<Attachment> getAttachments(List<Message> messages) {
        List<Attachment> attachments = new ArrayList<>();
        for (Message message : messages) {
            attachments.addAll(message.getAttachments());
        }
        return attachments;
    }

    private void sendTranscript(Ticket ticket, Guild guild, String transcript, String transcriptName, String htmlTranscript, List<Attachment> attachments) {
        TextChannel transcriptChannel = guild.getTextChannelById(ChannelEnum.TRANSCRIPT_LOGS.getId());

        if (transcriptChannel == null) {
            // Handle the case where the transcript channel doesn't exist
            return;
        }

        InputStream textStream = new ByteArrayInputStream(transcript.getBytes());
        InputStream htmlStream = new ByteArrayInputStream(htmlTranscript.getBytes());

        @SuppressWarnings("null")
        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(144, 96, 233))
                .setTitle("Ticket #" + ticket.getTicketNumber())
                .addField("Subject", ticket.getSubject(), false)
                .addField("Body", ticket.getBody(), false)
                .addField("Member", guild.getMemberById(ticket.getMemberId()).getAsMention(), false)
                .addField("Staff", guild.getMemberById(ticket.getStaffId()).getAsMention(), false)
                .addField("Reason", ticket.getReason(), false)
                .build();

        compressToZip(htmlStream, textStream, attachments).thenAccept(zipStream -> {
            transcriptChannel.sendMessageEmbeds(embed)
                    .addFiles(FileUpload.fromData(zipStream, "transcripts.zip"))
                    .queue();
        });
    }

    private String generateHTMLTranscript(TextChannel channel, List<Message> messages) {
        String htmlTemplate = HTMLUtils.getTranscriptHTML();
        String encodedServer = new ServerEncoded(channel.getGuild()).getEncoded();
        String encodedChannel = new ChannelEncoded(channel).getEncoded();
        String encodedMessages = new MessagesEncoded(messages).getEncoded();

        return String.format(htmlTemplate, encodedChannel, encodedServer, encodedMessages);
    }

    private CompletableFuture<InputStream> compressToZip(InputStream htmlStream, InputStream textStream, List<Attachment> attachments) {
        return CompletableFuture.supplyAsync(() -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ZipOutputStream zos = new ZipOutputStream(baos)) {

                // Add HTML transcript
                zos.putNextEntry(new ZipEntry("transcript.html"));
                htmlStream.transferTo(zos);
                zos.closeEntry();

                // Add text transcript
                zos.putNextEntry(new ZipEntry("transcript.txt"));
                textStream.transferTo(zos);
                zos.closeEntry();

                // Add attachments
                for (Attachment attachment : attachments) {
                    CompletableFuture<InputStream> attachmentStream = attachment.getProxy().download();
                    try (InputStream is = attachmentStream.get()) {
                        zos.putNextEntry(new ZipEntry(attachment.getFileName()));
                        is.transferTo(zos);
                        zos.closeEntry();
                    }
                }

                zos.finish();
                return new ByteArrayInputStream(baos.toByteArray());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
