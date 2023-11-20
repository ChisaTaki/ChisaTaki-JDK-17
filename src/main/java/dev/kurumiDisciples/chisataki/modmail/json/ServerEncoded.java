package dev.kurumidisciples.chisataki.modmail.json;

import javax.json.*;
import javax.json.stream.*;

import net.dv8tion.jda.api.entities.Guild;

import java.util.Base64;

public class ServerEncoded{


  private String name;
  private String id;
  private String icon;
  private JsonObject json;
  
  public ServerEncoded(Guild guild){
     json = Json.createObjectBuilder()
      .add("name", guild.getName())
      .add("id", guild.getId())
      .add("icon", guild.getIcon().getUrl().split("/")[5].split("\\.")[0])
      .build();
  }

  public String getEncoded(){
    return Base64.getEncoder().encodeToString(json.toString().getBytes());
  }
}