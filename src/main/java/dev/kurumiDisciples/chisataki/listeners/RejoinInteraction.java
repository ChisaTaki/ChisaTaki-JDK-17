package dev.kurumiDisciples.chisataki.listeners;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/*json classes */
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

/* Logging classes */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumiDisciples.chisataki.enums.RoleEnum;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
/* Exceptions */
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class RejoinInteraction extends ListenerAdapter {

  final static Logger logger = LoggerFactory.getLogger(RejoinInteraction.class);

  public void onGuildMemberJoin(GuildMemberJoinEvent event){
    Thread addRoles = new Thread(){
      public void run(){
        if (userHasRecord(event.getMember())){
          addRolesBackToMember(event.getMember(), getMemberJsonRole(event.getMember()));
          removeRecord(event.getMember());
        }
      }
    };
    addRoles.setName("Readd-Roles");
    addRoles.start();
  }



  private static JsonArray getMemberJsonRole(Member member){
    JsonObject mainFile = getUserSave();
    JsonObject memberObject = mainFile.getJsonObject(member.getId());

    return memberObject.getJsonArray("roles");
  }


  private static JsonObject getUserSave(){
     while (true) {
            try {
                FileReader reader = new FileReader(new File("data/userLeft.json"));
                JsonReader Jreader = Json.createReader(reader);
                JsonObject t = Jreader.readObject();
                if (t != null) {
                    return t;
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

  private static void addRolesBackToMember(Member member, JsonArray roles){
    for (int i = 0; i < roles.size(); i++){
      try{
        Role role = member.getGuild().getRoleById(roles.getString(i));
        
        if (RoleEnum.TAKINA_SHRINE.getId().equals(role.getId()) || RoleEnum.CHISATO_SHRINE.getId().equals(role.getId())) {
            logger.info("Role " + role.getName() + "was skipped.");      
        } else {
    		member.getGuild().addRoleToMember(member, role).reason("Adding role back to member after they left").queue();
        }
        
      }
      catch (InsufficientPermissionException e){
        logger.error("ChisaTaki lacks the Permissions to perform this action. Reason: {}", e.getMessage());       
      }
      catch (HierarchyException e){
        logger.error("ChisaTaki encountered a Hierarchy Exception. Reason: {}", e.getMessage());       
      }
      catch (IllegalStateException e){
        logger.error("ChisaTaki encountered an Illegal State. Reason: {}", e.getMessage());    
      }
      catch (ErrorResponseException e){
        logger.error("ChisaTaki encountered an Error Response. Response: {}", e.getErrorResponse().toString());
      }
      catch (NullPointerException e){
        logger.error("ChisaTaki caught a NullPointer Error. Response: {}", e.getMessage());
      }
      catch (IllegalArgumentException e){
        logger.error("ChisaTaki encountered a null error. Reason: {}", e.getMessage()); 
      }
    }
  }

  private static boolean userHasRecord(Member member){
    try{
      JsonArray t = getMemberJsonRole(member);
      if (t != null){
        return true;
      }
      else {
        return false;
      }
    }
    catch (NullPointerException e){
      return false;
    }
  }

  private static void removeRecord(Member member){
    archive(removeProperty(getUserSave(), member.getId()));
  }

  private static JsonObject removeProperty(JsonObject origin, String key){
    JsonObjectBuilder builder = Json.createObjectBuilder();

    for (Map.Entry<String,JsonValue> entry : origin.entrySet()){
        if (entry.getKey().equals(key)){
            continue;
        } else {
            builder.add(entry.getKey(), entry.getValue());
        }
    }       
    return builder.build();
}
  private static void archive(JsonObject jso){
      try {
          /* retrieve old file */
            FileWriter t = new FileWriter("data/" + "userLeft" + ".json");
            t.write(jso.toString());
            t.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}