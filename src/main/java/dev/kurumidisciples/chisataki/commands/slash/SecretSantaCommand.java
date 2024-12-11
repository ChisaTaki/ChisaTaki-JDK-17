package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumidisciples.chisataki.secretsanta.SantaComponents;
import dev.kurumidisciples.chisataki.secretsanta.time.SantaClock;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SecretSantaCommand extends SlashCommand{
    
    public SecretSantaCommand() {
        super("secretsanta", "Send Secret Santa message to members", Permission.CREATE_PUBLIC_THREADS);
        this.options = List.of(
            new OptionData(OptionType.CHANNEL, "channel", "channel to send message to", true),
            new OptionData(OptionType.INTEGER, "time", "Unix time to send the secret santa message at", true)
        );
    }



    @SuppressWarnings("null")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        
        SantaClock.setTime(event.getOption("time") == null ? System.currentTimeMillis() : event.getOption("time").getAsLong());
        event.getOption("channel").getAsChannel().asTextChannel()
        .sendMessage("<@&1013809301342662726> We are hosting a Secret Santa Event again this year!").addEmbeds(createSantaEmbed()).setActionRow(SantaComponents.createButton()).queue();
    }

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        return true;
    }

    private MessageEmbed createSantaEmbed(){
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("For this event, the following will occur:");
        builder.setDescription("From today, December 11th to December 18th at <t:1734789600:t>, we'll have this form open to register for the Secret Santa. Assuming we can get at least 5-6 people on board, we'll go through with it.\n\nBetween January 1st and the 5th, we'll give out our gifts to the person that were chosen for us!\n\nIn the form you will be asked about your interests/gift suggestions; this will help whoever was given you decide on what to get you.\n\nUnfortunately, we **won't be able to allow physical objects,** so try to keep it something digital!\n\n**Gifts don't need to cost money, but please try to keep it less than $15 USD if you do plan on buying something!!**");
        builder.setFooter("Contact ChisaTaki Staff for Comments or Concerns.");
        builder.setColor(ColorUtils.PURPLE);
        builder.setImage("https://media.discordapp.net/attachments/1044304571767472199/1316431364832100414/Fk0qYW7acAEfT4c.png?ex=675b05bf&is=6759b43f&hm=5f4ec58026a869bb0f7ec0237bc9c7ab5311df38d67d4545735852c92bc24407&=&format=webp&quality=lossless");

        return builder.build();
    }
}