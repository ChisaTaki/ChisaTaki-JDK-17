package dev.kurumiDisciples.chisataki.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class ChristmasCommand extends SlashCommand {
    

    public ChristmasCommand() {
        super("chritmas-countdown", "Countdown to Christmas");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();
        int daysUntilChristmas = calculateDaysUntilChristmas();
        event.getHook().sendMessage("There are " + daysUntilChristmas + " days until Christmas!").queue();
    }

     private static int calculateDaysUntilChristmas() {
        LocalDate currentDate = LocalDate.now();
        LocalDate christmasDate = LocalDate.of(currentDate.getYear(), Month.DECEMBER, 25);

        if (currentDate.isAfter(christmasDate)) {
            // Christmas has already passed for this year, so calculate for the next year
            christmasDate = christmasDate.plusYears(1);
        }

        long daysUntilChristmas = ChronoUnit.DAYS.between(currentDate, christmasDate);
        return (int) daysUntilChristmas;
    }
}
