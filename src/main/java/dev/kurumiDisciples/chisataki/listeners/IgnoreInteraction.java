package dev.kurumiDisciples.chisataki.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*json classes */
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/* Logging classes */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class IgnoreInteraction extends ListenerAdapter {
  final static Logger logger = LoggerFactory.getLogger(IgnoreInteraction.class);
  
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
    Thread ignoreThread = new Thread(){
      public void run(){
        if (event.getName().equals("ignore-me")){
          if (inList(event.getMember())){
            event.deferReply(true).queue();
            archiveRemove(event.getMember());
            event.getHook().editOriginal("You are no longer ignored by ChisaTaki.").queue();
          }
          else {
            event.deferReply(true).queue();
            archiveAdd(event.getMember());
            event.getHook().editOriginal("You are now ignored by ChisaTaki").queue();
          }
        }
      }    
    };
    ignoreThread.setName("Ignore-Thread");
    ignoreThread.setPriority(3);
    ignoreThread.start();
  }
/* The code begins by declaring the method as private static, which means that it can only be called from within the current class and that it does not require an instance of the class to be created in order to be used. The method returns a JsonArray object, which is part of the Java API for JSON Processing (JSR 353).

Next, the code enters an infinite loop that will keep trying to read the JSON file until it is successful. Inside the loop, the code uses a try-catch block to handle any exceptions that may be thrown while reading the file. If an exception is thrown, the code logs the error message using the logger object, and then continues to the next iteration of the loop.

If the JSON file is successfully read, the code creates a JsonReader object to parse the file contents, and then calls the readObject() method to read the contents of the file as a JsonObject. If the object is not null, the code returns the JsonArray stored in the "ignore" property of the object. If the object is null, the code continues to the next iteration of the loop.

Overall, this code provides a way to read a JSON file and return the contents as a JsonArray, with the ability to retry if there are any errors or exceptions.*/
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
  /*The method takes a Member object as its only parameter, and returns a boolean value indicating whether or not the Member is present in the JsonArray. To determine this, the code calls the getIgnoreList() method to get the JsonArray, and then calls the contains() method on the JsonArray with the Member's ID as the parameter. This will return true if the Member is present in the JsonArray, and false if it is not.

Overall, this code provides a way to check if a Member is present in a list of Member objects stored in a JSON file. The code relies on the getIgnoreList() method to read the JSON file and return the list of Member objects, and then uses the contains() method to check if a given Member is present in the list.*/
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