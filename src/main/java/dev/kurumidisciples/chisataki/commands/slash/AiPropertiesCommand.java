package dev.kurumidisciples.chisataki.commands.slash;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.theokanning.openai.assistants.assistant.Assistant;
import com.theokanning.openai.billing.BillingUsage;
import com.theokanning.openai.billing.DailyCost;
import com.theokanning.openai.billing.LineItem;

import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.character.AiStatusTableUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class AiPropertiesCommand extends SlashCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiPropertiesCommand.class);
    
    public AiPropertiesCommand(){
        super("ai", "Manage/View properties for the Ai", Permission.CREATE_GUILD_EXPRESSIONS); //TODO change to MANAGE_SERVER on release
        super.subcommands = List.of(
            new SubcommandData("enable", "Enable the Ai"),
            new SubcommandData("disable", "Disable the Ai"),
            new SubcommandData("limit", "View or modify the limit of messages per day")
            .addOption(OptionType.INTEGER, "newlimit", "The new limit of messages per day", false),
            new SubcommandData("reset", "Reset all user's usage for the day")
            .addOption(OptionType.USER, "individual", "Only reset a specific user", false),
            new SubcommandData("instructions", "View the instructions for the assistant")
        );
    }

    @Override
    public boolean isAllowed(SlashCommandInteractionEvent event) {
        return true;
    }

    @Override
    @SuppressWarnings("null")
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        switch (event.getSubcommandName()) {
            case "enable" -> handleEnable(event);
            case "disable" -> handleDisable(event);
            case "limit" -> handleLimit(event);
            case "reset" -> handleReset(event);
            case "instructions" -> handleInstructions(event);
            default -> LOGGER.error("Unknown subcommand: {}", event.getSubcommandName());
        }
    }


    @SuppressWarnings("null")
    private static void handleEnable(SlashCommandInteractionEvent event){
        long guildId = event.getGuild().getIdLong();
        if(AiStatusTableUtils.selectAiStatus(guildId)){
            event.getHook().editOriginal("The Ai is already enabled").queue();
            return;
        }
        AiStatusTableUtils.updateAiStatus(guildId, true);
        event.getHook().editOriginal("The Ai has been enabled").queue();
    }

    @SuppressWarnings("null")
    private static void handleDisable(SlashCommandInteractionEvent event){
        long guildId = event.getGuild().getIdLong();
        if(!AiStatusTableUtils.selectAiStatus(guildId)){
            event.getHook().editOriginal("The Ai is already disabled").queue();
            return;
        }
        AiStatusTableUtils.updateAiStatus(guildId, false);
        event.getHook().editOriginal("The Ai has been disabled").queue();
    }

    @SuppressWarnings("null")
    private static void handleLimit(SlashCommandInteractionEvent event){
        if(event.getOption("newlimit") == null){
            int limit = AiStatusTableUtils.selectLimit(event.getGuild().getIdLong());
            event.getHook().editOriginal("The current limit is " + limit).queue();
            return;
        }
        int newLimit = event.getOption("newlimit").getAsInt();
        boolean success = AiStatusTableUtils.updateLimit(event.getGuild().getIdLong(), newLimit);

        if(success){
            event.getHook().editOriginal("The new limit has been set to " + newLimit).queue();
            LOGGER.info("The new limit has been set to {} by user {}", newLimit, event.getUser().getId());
        } else {
            event.getHook().editOriginal("Failed to set the new limit").queue();
        }
    }

    @SuppressWarnings("null")
    private static void handleReset(SlashCommandInteractionEvent event){
        if(event.getOption("individual") == null){
            boolean success = AiStatusTableUtils.resetAllUsers();
            if (success) {
                event.getHook().editOriginal("All user's usage has been reset").queue();
                LOGGER.info("All user's usage has been reset by user {}", event.getUser().getId());
            } else {
                event.getHook().editOriginal("Failed to reset all user's usage").queue();
            }
            return;
        }
        long userId = event.getOption("individual").getAsUser().getIdLong();
        boolean success = AiStatusTableUtils.resetUser(userId);
        if(success){
            event.getHook().editOriginal("User's usage has been reset").queue();
        } else {
            event.getHook().editOriginal("Failed to reset user's usage").queue();
        }
    }

    @SuppressWarnings("null")
    private static void handleInstructions(SlashCommandInteractionEvent event){
        Assistant assistant = Main.getAssistant();
        event.getHook().editOriginal(assistant.getInstructions()).queue();
    }


}
