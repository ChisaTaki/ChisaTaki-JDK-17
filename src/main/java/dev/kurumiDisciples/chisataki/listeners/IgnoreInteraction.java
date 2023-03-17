package dev.kurumiDisciples.chisataki.listeners;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IgnoreInteraction extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(IgnoreInteraction.class);
    private static final ExecutorService ignoreExecutor = Executors.newCachedThreadPool();

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        ignoreExecutor.submit(() -> {
            if (event.getName().equals("ignore-me")) {
                handleIgnoreMeCommand(event);
            }
        });
    }

    private void handleIgnoreMeCommand(SlashCommandInteractionEvent event) {
        if (inList(event.getMember())) {
            event.deferReply(true).queue();
            archiveRemove(event.getMember());
            event.getHook().editOriginal("You are no longer ignored by ChisaTaki.").queue();
        } else {
            event.deferReply(true).queue();
            archiveAdd(event.getMember());
            event.getHook().editOriginal("You are now ignored by ChisaTaki").queue();
        }
    }
  
  private static JsonArray getIgnoreList(){
    while (true) {
            try {
                FileReader reader = new FileReader(new File("data/ignore.json"));
                JsonReader Jreader = Json.createReader(reader);
                JsonObject t = Jreader.readObject();
                if (t != null) {
                    return t.getJsonArray("ignore");
                }
            } catch (JsonException jsonexcept) {
                logger.debug("Error reading chisato.json");
            } catch (IllegalStateException illegal) {
                logger.debug("illegal state caught - retrying");
            } catch (FileNotFoundException e) {
                logger.debug("File not found - retrying");
            }
        }
  }
  public static boolean inList(Member member){
    /* checks if the given member is in the JsonArray provided by getIgnoreList() */
    for (int i = 0; i < getIgnoreList().size(); i++){
      if (getIgnoreList().getString(i).equals(member.getId())) return true;
    }
    return false;
  }

  private static JsonArray insertValue(String value){
    JsonArrayBuilder t = Json.createArrayBuilder();
    JsonArray old = getIgnoreList();

    for (JsonValue s : old){
      t.add(s);
    }
    t.add(value);

    return t.build();
  }

  private static void archiveAdd(Member member /* member being added */) {
        try {
            /* retrieve old file */
          JsonObject oldJson = Json.createObjectBuilder().add("ignore", insertValue(member.getId())).build();
            FileWriter t = new FileWriter("data/" + "ignore" + ".json");
            t.write(oldJson.toString());
            t.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  private static JsonArray removeValue(String value){

    JsonArray list = getIgnoreList();
    JsonArrayBuilder t = Json.createArrayBuilder();

    for (int i = 0; i < list.size(); i++){
      if (!list.getString(i).equals(value)) t.add(list.getString(i));
    }

    return t.build();
  }

  private static void archiveRemove(Member member){
    try {
            /* retrieve old file */
          JsonObject oldJson = Json.createObjectBuilder().add("ignore", removeValue(member.getId())).build();
            FileWriter t = new FileWriter("data/" + "ignore" + ".json");
            t.write(oldJson.toString());
            t.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
  }
}