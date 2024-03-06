package dev.kurumidisciples.chisataki.booster;

import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.Interaction;

public class BoosterEmbedUtils {
    

    public static MessageEmbed getRoleClaimEmbed(Interaction event, Role role){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getUser().getEffectiveAvatarUrl());
        embed.setDescription("You have successfully claimed your role as a booster!\n\n You can now modify your role using the `/boost role` command.");
        embed.setColor(ColorUtils.PURPLE);
        embed.setFooter("Your custom role id is " + role.getId());
        return embed.build();
    }

    public static MessageEmbed getRoleColorEmbed(Interaction event, Role role, int color){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getUser().getEffectiveAvatarUrl());
        embed.setTitle("Edited Role Color!");
        embed.setColor(color);
        embed.setFooter("Role ID: " + role.getId());
        return embed.build();
    }

    public static MessageEmbed getRoleNameEmbed(Interaction event, Role role){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getUser().getEffectiveAvatarUrl());
        embed.setTitle("Edited Role Name!");
        embed.setColor(role.getColor());
        embed.setFooter("Role ID: " + role.getId());
        return embed.build();
    }

    public static MessageEmbed getRoleIconEmbed(Interaction event, Role role){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getUser().getEffectiveAvatarUrl());
        embed.setTitle("Edited Role Icon!");
        embed.setColor(role.getColor());
        embed.setFooter("Role ID: " + role.getId());
        return embed.build();
    }
}
