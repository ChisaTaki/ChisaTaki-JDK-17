package dev.kurumidisciples.chisataki.modmail;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.enums.StatusType;
import dev.kurumidisciples.chisataki.modmail.json.ChannelEncoded;
import dev.kurumidisciples.chisataki.modmail.json.MessagesEncoded;
import dev.kurumidisciples.chisataki.modmail.json.ServerEncoded;
import dev.kurumidisciples.chisataki.utils.HTMLUtils;
import dev.kurumidisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
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
  public void onButtonInteraction(ButtonInteractionEvent event) {
    Thread ticket = new Thread() {
      public void run() {

        String channelId = event.getChannel().getName();
        if (!channelId.toLowerCase().startsWith("ticket-")) {
          return;
        }

        if (!RoleUtils.isMemberStaff(event.getMember())) {
          event.deferReply(true).queue();
          event.getHook().sendMessage("This action can only be executed by ChisaTaki Staff members").queue();
          return;
        }

        if (event.getComponentId().startsWith("claim-")) {

          Ticket ticket = new Ticket(Integer.valueOf(event.getComponentId().split("-")[1]));
          if (ticket.hasStaff()) {
            event.deferReply(true).queue();
            event.getHook().sendMessage("This ticket is already claimed by a staff member").queue();
            return;
          }
          event.deferReply(false).queue();
          ticket.setStaff(event.getMember().getId()).setStatus(StatusType.ANSWERED);
          event.getHook().editOriginal(event.getMember().getAsMention() + " has claimed this ticket!").queue();

          //disable the claim ticket button
          event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds().get(0)).setActionRow(
              List.of(event.getMessage().getButtons().get(0).asDisabled(), event.getMessage().getButtons().get(1), event.getMessage().getButtons().get(2)))
              .queue(); 
            
        } else if (event.getComponentId().startsWith("closereason-")) {
          String ticketNumber = event.getComponentId().split("-")[1];
          event.replyModal(getReasonModal(ticketNumber)).queue();
        } else if (event.getComponentId().startsWith("close-")) {
          event.deferReply(false).queue();

          String ticketNumber = event.getComponentId().split("-")[1];
          Ticket ticket = new Ticket(Integer.valueOf(ticketNumber));
          if (ticket.isClosed()) {
            event.getHook().editOriginal("This Ticket has already been closed.").queue();
            return;
          }
          ticket.setStatus(StatusType.CLOSED);
          handleClosingTicket(ticket, event);
        }
      }
    };
    ticket.start();
  }

  @Override
  public void onModalInteraction(ModalInteractionEvent event) {
    Thread reasonModal = new Thread() {
      public void run() {
        if (event.getModalId().startsWith("reasonModal")) {
          event.deferReply().queue();
          int ticketNumber = Integer.valueOf(event.getModalId().split("-")[1]);
          String reason = event.getValue("reason").getAsString();

          Ticket ticket = new Ticket(ticketNumber);
          if (ticket.isClosed()) {
            event.getHook().editOriginal("This Ticket has already been closed.").queue();
            return;
          }
          ticket.setStatus(StatusType.CLOSED).setReason(reason);
          handleClosingTicket(ticket, event);
        }

      }
    };

    reasonModal.start();
  }

  private void handleClosingTicket(Ticket ticket, ButtonInteractionEvent event) {
    if (!ticket.hasStaff()) {
      ticket.setStaff(event.getMember().getId());
    }
  event.getHook().editOriginal("Ticket closed").queue();    
    event.getGuildChannel().asTextChannel().getManager().removePermissionOverride(ticket.getMemberId()).complete();
    sendTranscript(ticket, event.getGuild(), archive(event.getGuildChannel().asTextChannel()),
        "ticket-" + ticket.getTicketNumber(), generateHTMLTranscript(event.getGuildChannel().asTextChannel()));
    
    event.getGuildChannel().asTextChannel().delete().queueAfter(10, TimeUnit.SECONDS);

  }

  private void handleClosingTicket(Ticket ticket, ModalInteractionEvent event) {
    if (!ticket.hasStaff()) {
      ticket.setStaff(event.getMember().getId());
    }
    event.getGuildChannel().asTextChannel().getManager().removePermissionOverride(ticket.getMemberId()).complete();
    sendTranscript(ticket, event.getGuild(), archive(event.getGuildChannel().asTextChannel()),
        "ticket-" + ticket.getTicketNumber(), generateHTMLTranscript(event.getGuildChannel().asTextChannel()));
    event.getHook().editOriginal("Ticket closed with reason").queue();
    event.getGuildChannel().asTextChannel().delete().queueAfter(10, TimeUnit.SECONDS);
  }

  private Modal getReasonModal(String ticketNumber) {
    TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.SHORT).setMinLength(5).build();
    Modal reasonModal = Modal.create("reasonModal-" + ticketNumber, "Ticket Reason").addActionRows(ActionRow.of(reason))
        .build();
    return reasonModal;
  }

  private String archive(TextChannel channel) {
    MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();

    String data = "";
    String format = "%s#%s: %s\n";
    String formatReply = "%s#%s replying to %s#%s: %s\n";
    List<Message> messages = history.getRetrievedHistory();
    for (int i = messages.size() - 1; i >= 0; i--) {
      Message message = messages.get(i);
      if (message.getReferencedMessage() != null) {
        data += String.format(formatReply, message.getAuthor().getName(),
            message.getAuthor().getDiscriminator(), message.getReferencedMessage().getAuthor().getName(),
            message.getReferencedMessage().getAuthor().getDiscriminator(), message.getContentStripped());
      } else {
        data += String.format(format, message.getAuthor().getName(), message.getAuthor().getDiscriminator(),
            message.getContentStripped());
      }
    }

    return data;
  }

  private void sendTranscript(Ticket ticket, Guild guild, String transcript, String transcriptName, String htmlTranscript) {
    TextChannel transcriptChannel = guild.getTextChannelById(ChannelEnum.TRANSCRIPT_LOGS.getId());

    InputStream stream = new ByteArrayInputStream(transcript.getBytes());
    InputStream htmlStream = new ByteArrayInputStream(htmlTranscript.getBytes());
    MessageEmbed embed = new EmbedBuilder().setColor(new Color(144, 96, 233))
        .setTitle("Ticket #" + String.valueOf(ticket.getTicketNumber())).addField("Subject", ticket.getSubject(), false)
        .addField("Body", ticket.getBody(), false)
        .addField("Member", guild.getMemberById(ticket.getMemberId()).getAsMention(), false)
        .addField("Staff", guild.getMemberById(ticket.getStaffId()).getAsMention(), false)
        .addField("Reason", ticket.getReason(), false).build();

    transcriptChannel.sendMessage(" ").addEmbeds(embed).addFiles(FileUpload.fromData(compressToZip(htmlStream, stream), "transcripts.zip")).complete();
    //FileUtils.writeFile("data/tickets/"+transcriptName+".txt", transcript);
  }

  private String generateHTMLTranscript(TextChannel channel){
    MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();

    String html = HTMLUtils.getTranscriptHTML();

    List<Message> messages = history.getRetrievedHistory();

    String encodedServer = new ServerEncoded(channel.getGuild()).getEncoded();
    String encodedChannel = new ChannelEncoded(channel).getEncoded();
    String encodedMessages = new MessagesEncoded(messages).getEncoded();

    html = String.format(html, encodedChannel, encodedServer, encodedMessages);

    return html;
  }

  private InputStream compressToZip(InputStream html, InputStream txt) {
    try{
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {
      // Add HTML file to the zip
      zipOut.putNextEntry(new ZipEntry("transcript.html"));
      byte[] htmlBytes = html.readAllBytes();
      zipOut.write(htmlBytes, 0, htmlBytes.length);
      zipOut.closeEntry();

      // Add TXT file to the zip
      zipOut.putNextEntry(new ZipEntry("transcript.txt"));
      byte[] txtBytes = txt.readAllBytes();
      zipOut.write(txtBytes, 0, txtBytes.length);
      zipOut.closeEntry();
    }

    return new ByteArrayInputStream(baos.toByteArray());
  }
  catch(Exception e){
    e.printStackTrace();
    return null;
  }
  }
}