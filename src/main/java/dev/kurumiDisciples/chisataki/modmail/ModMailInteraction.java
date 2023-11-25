package dev.kurumidisciples.chisataki.modmail;

import java.awt.Color;
import java.util.List;

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


  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getName().equals("sdfsa")) {
      event.reply(" ").setActionRow(Button.success("modmail", "ModMail")).queue();
    }
  }

  public void onButtonInteraction(ButtonInteractionEvent event) {
    Thread modMailThread = new Thread() {
      public void run() {
        if (event.getComponentId().equals("modmail")) {
          event.replyModal(getModMailModal()).queue();
        }
      }
    };
    modMailThread.start();
  }

  public void onModalInteraction(ModalInteractionEvent event) {
    Thread modMail = new Thread() {
      public void run() {
        if (event.getModalId().equals("mailModal")) {
          Guild guild = event.getGuild();
          event.deferReply(true).queue();

          /*
           * Create a channel using Guild.createCopyOfChannel() copying Bot Dev. Then
           * overwrite the member into that channel. Have the bot send a embed with the
           * subject and body inside it. Attach action buttons: Claim (Staff use so they
           * can claim the channel), Close(closes the ticket and removes member from the
           * channel), Close with reason (closes the ticket with reason prompt and then
           * removes memebr from the channel). The channel should be deleted by the bot
           * after 10 minutes of being closed.
           */
          TextChannel ticketChannel = guild.createCopyOfChannel(guild.getTextChannelById("1011966579610755102"))
              .setName("Ticket-" + String.valueOf(countFiles()))
              .setTopic("This is a Ticket Channel and therefore it is temporary.")
              .setPosition(0)
            .complete();
          Ticket ticket = TicketBuilder.buildTicket(countFiles(), event.getInteraction(),
              ticketChannel.getIdLong());

          /*
           * Sets an override for the ticket channel so the member can send messages and
           * view the channel
           */
          ticketChannel.getManager().putPermissionOverride(event.getMember(), 137439464512L, 0L).complete();

          event.getHook().editOriginal("").setEmbeds(getRedirectionEmbed(ticketChannel)).queue();

          ticketChannel.sendMessage(createContentEmbed(ticket, event.getMember())).queue();

         sendNotification(event.getGuild(), ticket);
        }
      }
    };
    modMail.start();
  }

  private Modal getModMailModal() {
    TextInput subject = TextInput.create("subject", "Subject", TextInputStyle.SHORT)
        .setPlaceholder("Subject of this ticket").setMinLength(10).setMaxLength(100) // or setRequiredRange(10, 100)
        .build();

    TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH).setPlaceholder("Your concerns go here")
        .setMinLength(10).setMaxLength(1000).build();

    Modal modModal = Modal.create("mailModal", /* title */"Contact Staff")
        .addActionRows(ActionRow.of(subject), ActionRow.of(body)).build();
    return modModal;
  }

  private MessageEmbed getRedirectionEmbed(TextChannel ticketChannel) {
    EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Ticket Successfully Created")
        .setDescription("Please head to " + ticketChannel.getAsMention() + "\nA staff member will be with you shortly")
        .setColor(new Color(144, 96, 233));
    return embedBuilder.build();
  }

  public static int countFiles() {
    return TicketDatabaseUtils.countTickets();
  }

  private MessageCreateData createContentEmbed(Ticket ticket, Member member) {
    MessageCreateData data = new MessageCreateBuilder()
        .addEmbeds(List.of(
            new EmbedBuilder().setTitle(member.getEffectiveName() + " has Created a Ticket!")
                .addField("Subject", ticket.getSubject(), false).addField("Body", ticket.getBody(), false)
                .setColor(new Color(144, 96, 233)).build(),
            new EmbedBuilder().setDescription("**Staff use the buttons below to handle this ticket!**")
                .setColor(new Color(144, 96, 233)).build()))
        .addActionRow(List.of(
            Button.success(/* claim-{ticket-num} */ "claim-" + String.valueOf(ticket.getTicketNumber()),
                "Claim Ticket").withEmoji(Emoji.fromUnicode("U+1F4EC")),
            Button.danger(/* close-{ticket-num} */ "close-" + String.valueOf(ticket.getTicketNumber()), "Close Ticket").withEmoji(Emoji.fromUnicode("U+1F512")),
            Button.danger(/* closereason-{ticket-num} */ "closereason-" + String.valueOf(ticket.getTicketNumber()),
                "Close Ticket With Reason").withEmoji(Emoji.fromUnicode("U+1F50F"))))
        .build();
    return data;
  }

  private void sendNotification(Guild guild, Ticket ticket){
    TextChannel ticketLog = guild.getTextChannelById(ChannelEnum.TICKET_LOGS.getId());
    String format = "**Channel**\n%s\n\n**Subject**\n%s\n\n**Body**\n%s\n\n**Member**\n%s";
    ticketLog.sendMessage("<@&1016048811581382676> ** NEW TICKET**").setEmbeds(new EmbedBuilder()
                                        .setColor(new Color(144, 96, 233))
                                        .setTitle("Ticket #"+String.valueOf(ticket.getTicketNumber()))
                                        .setDescription(String.format(format, guild.getTextChannelById(ticket.getTicketId()).getAsMention(),ticket.getSubject(), ticket.getBody(), guild.getMemberById(ticket.getMemberId()).getAsMention()))
                                         .build()
                                        ).queue();
  }
}