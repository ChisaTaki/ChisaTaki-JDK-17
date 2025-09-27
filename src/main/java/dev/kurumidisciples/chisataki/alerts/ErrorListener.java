package dev.kurumidisciples.chisataki.alerts;

import java.util.Stack;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.utils.ColorUtils;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

public class ErrorListener extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent e) {
        if (e.getLevel().isGreaterOrEqual(Level.ERROR)) {
            onError(e);
        }
    }

    private void onError(ILoggingEvent e) {
        ThreadChannel channel = getAlertChannel();
        
        Container container = Container.of(
            TextDisplay.of("# ERROR Occured at <t:" + (System.currentTimeMillis() / 1000) + ":F>"),
            TextDisplay.of("```\n" + e.getThreadName() + ": " + e.getFormattedMessage() + "\n```"),
            TextDisplay.of("```\n" + retrieveFullCallerData(e.getCallerData()) + "\n```")
        ).withAccentColor(ColorUtils.PURPLE);

        channel.sendMessageComponents(container).useComponentsV2().queue();
    }

    private String retrieveFullCallerData(StackTraceElement[] callerData) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : callerData) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private ThreadChannel getAlertChannel() {
        return Main.getJDA().getGuildById(1010078628761055234L).getThreadChannelById(1414330572058398790L);
    }
    
}
