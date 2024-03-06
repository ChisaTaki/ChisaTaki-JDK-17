package dev.kurumidisciples.chisataki.commands.slash;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.booster.Booster;
import dev.kurumidisciples.chisataki.booster.BoosterDatabaseUtils;
import dev.kurumidisciples.chisataki.booster.BoosterEmbedUtils;
import dev.kurumidisciples.chisataki.enums.ChannelEnum;
import dev.kurumidisciples.chisataki.enums.RoleEnum;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class BoosterCommand extends SlashCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoosterCommand.class);
    
    public BoosterCommand() {
        super("boost", "modify your benefits as a booster");
        subcommands = new ArrayList<SubcommandData>();
        subcommands.add(new SubcommandData("role", "modify your booster role")
            .addOption(OptionType.STRING, "name" , "the name of the role you want to set", false)
            .addOption(OptionType.STRING, "color", "hex code of the color you want to set. include '#'", false)
            .addOption(OptionType.STRING, "icon", "emote to set as your icon", false)
        );
        subcommands.add(new SubcommandData("claim", "claim your booster role")); // adds the booster to the database and creates a generic role for them to modify to their liking
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String name = event.getSubcommandName();
        Long userId = event.getMember().getIdLong();

        if (name.equals("role")) {
            Booster booster = BoosterDatabaseUtils.getBooster(userId);
            if (booster == null) {
                event.reply("You have not claimed your role! Use the `/boost claim`").setEphemeral(true).queue();
            } else if (event.getOptions().isEmpty()) {
                event.reply("You must provide a name, color, or icon to modify your role!").setEphemeral(true).queue();
            } else if (event.getOptions().size() > 1) {
                event.reply("You can only modify one aspect of your role at a time!").setEphemeral(true).queue();
            } else {
                String option = event.getOptions().get(0).getName();
                String value = event.getOptions().get(0).getAsString();
                switch (option) {
                    case "name":
                        modifyRoleName(event, booster, value);
                        break;
                    case "color":
                        modifyRoleColor(event, booster, value);
                        break;
                    case "icon":
                        modifyRoleIcon(event, booster, value);
                        break;
                }
            }
        } else if (name.equals("claim")) {
            if (BoosterDatabaseUtils.isBooster(userId)) {
                event.reply("You have already claimed your role!").setEphemeral(true).queue();
                return;
            }
            String uuid = UUID.randomUUID().toString();
            /* create role and add it to event user */
            event.getGuild().createRole().setName(uuid).queue(role -> {
                event.getGuild().modifyRolePositions().selectPosition(role).moveAbove(event.getGuild().getRoleById(RoleEnum.BOOSTER.getId())).queue();
                event.getGuild().addRoleToMember(event.getMember(), role).queue();
                BoosterDatabaseUtils.insertBooster(userId, role.getIdLong());
                event.replyEmbeds(BoosterEmbedUtils.getRoleClaimEmbed(event.getInteraction(), role)).setEphemeral(false).queue();
                LOGGER.info("Booster role claimed for user " + userId + " with role " + role.getId() + " and name " + uuid);
            });
        }
    }

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        Member user = event.getMember();
        return user.isBoosting();
    }

    @Override
    public String getErrorMessage(){
        return "Please boost cstk to get gay colors! <3 Or use the command <#" + ChannelEnum.BOOSTER_CHANNEL.getId() + "> to get your booster role!";
    }


    private static void modifyRoleIcon(SlashCommandInteractionEvent event, Booster booster, String icon){
        Role role = event.getGuild().getRoleById(Long.valueOf(booster.getRoleId()));

        if (role == null) {
            event.reply("Your role does not exist!").setEphemeral(true).queue();
            return;
        }
        EmojiUnion emoji = Emoji.fromFormatted(icon);
        if (emoji == null) {
            event.reply("Invalid emoji!").setEphemeral(true).queue();
            return;
        }

        if (emoji.getType() == Emoji.Type.CUSTOM){
            try{
            InputStream stream = emoji.asCustom().getImage().download().get();
            role.getManager().setIcon(Icon.from(stream)).queue();
            } catch (InterruptedException | ExecutionException e){
                event.reply("Unable to retrieve your emote from the API. Please try again later or contact a moderator.").setEphemeral(true).queue();
                LOGGER.error("Failed to retrieve emote image from ImageProxy for user " + event.getUser().getId() + " and emote " + emoji.asCustom().getId(), e);
            } catch (IOException e){
                event.reply("Failed to read emote image. Please try again later or contact a moderator.").setEphemeral(true).queue();
                LOGGER.error("Failed to convert InputStream to Icon for user " + event.getUser().getId() + " and emote " + emoji.asCustom().getId(), e);
            }
        } else if (emoji.getType() == Emoji.Type.UNICODE){
            role.getManager().setIcon(emoji.asUnicode().getAsCodepoints()).queue();
        }
        event.replyEmbeds(BoosterEmbedUtils.getRoleIconEmbed(event, role)).setEphemeral(false).queue();
        LOGGER.info("Role icon updated for user " + event.getUser().getId() + " and role " + role.getId() + " with icon " + icon);
    }

    private static void modifyRoleName(SlashCommandInteractionEvent event, Booster booster, String name){
        Role role = event.getGuild().getRoleById(Long.valueOf(booster.getRoleId()));

        if (role == null) {
            event.reply("Your role does not exist!").setEphemeral(true).queue();
            return;
        }
        try{
        role.getManager().setName(name).queue();
        LOGGER.info("Role name updated for user " + event.getUser().getId() + " and role " + role.getId() + " with name " + name);
        event.replyEmbeds(BoosterEmbedUtils.getRoleNameEmbed(event, role)).setEphemeral(false).queue();
        } catch (IllegalArgumentException e){
            event.reply("The name you have entered exceeds 100 characters! If you believe this is an error contact a moderator.").setEphemeral(true).queue();
            LOGGER.error("Failed to update role name for user " + event.getUser().getId() + " and role " + role.getId() + " with name " + name, e);
        }
    }

    private static void modifyRoleColor(SlashCommandInteractionEvent event, Booster booster, String hexcode){
        Role role = event.getGuild().getRoleById(Long.valueOf(booster.getRoleId()));

        if (role == null) {
            event.reply("Your role does not exist!").setEphemeral(true).queue();
            return;
        }
        try{
        //remove the # from the hexcode
        hexcode = hexcode.substring(1);
        role.getManager().setColor(Integer.parseInt(hexcode, 16)).queue();
        LOGGER.info("Role color updated for user " + event.getUser().getId() + " and role " + role.getId() + " with color " + hexcode);
        event.replyEmbeds(BoosterEmbedUtils.getRoleColorEmbed(event, role, Integer.parseInt(hexcode, 16))).setEphemeral(false).queue();
        } catch (NumberFormatException e){
            event.reply("Invalid hex code!").setEphemeral(true).queue();
            LOGGER.error("Failed to update role color for user " + event.getUser().getId() + " and role " + role.getId() + " with color " + hexcode, e);
        }
    }
}
