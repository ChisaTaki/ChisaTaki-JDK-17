package dev.kurumidisciples.chisataki.commands.slash;

import java.util.List;

import dev.kurumidisciples.chisataki.secretsanta.SantaComponents;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SecretSantaCommand extends SlashCommand{
    
    public SecretSantaCommand() {
        super("secretsanta", "Send Secret Santa message to members", Permission.MODERATE_MEMBERS);
        this.options = List.of(
            new OptionData(OptionType.CHANNEL, "channel", "channel to send message to", true),
            new OptionData(OptionType.INTEGER, "time", "Unix time to send the secret santa message at", true)
        );
    }



    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        

        event.getOption("channel").getAsChannel().asTextChannel()
        .sendMessage("@everyone We are hosting a Secret Santa Event again this year!").addEmbeds(createSantaEmbed()).setActionRow(SantaComponents.createButton()).queue();
    }

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        return true;
    }

    private MessageEmbed createSantaEmbed(){
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("For this event, the following will occur:");
        builder.setDescription("From today, December 10th to the December 21st, we'll have this form open to register for the Secret Santa. Assuming we can get at least 5-6 people on board, we'll go through with it.\n\nOn January 1st, we'll give out our gifts to the person that was choosen for us!\n\nIn the form you will be asked about your interests/gift suggestions; this will help whoever was given you decide on what to get you.\n\nUnfortunately, we **won't be able to allow physical objects,** so try to keep it something digital!\n\n**Gifts don't need to cost money!!**");
        builder.setFooter("Contact ChisaTaki Staff for Comments or Concerns.", "https://cdn.discordapp.com/avatars/1070074991653167144/c4e1c72a4aba1a10e5f3036b6b7c62dc.webp");
        builder.setColor(ColorUtils.PURPLE);
        builder.setImage("https://media.discordapp.net/attachments/1010078629344051202/1178436586023694349/Fk0qYW7acAEfT4c.png?ex=65762398&is=6563ae98&hm=b2b8c2ae9b54cda0b05ee4cfe9a7ed991eedd27c5ead34d9121d541de4d2b124&=&format=webp&width=963&height=681");

        return builder.build();
    }
}
