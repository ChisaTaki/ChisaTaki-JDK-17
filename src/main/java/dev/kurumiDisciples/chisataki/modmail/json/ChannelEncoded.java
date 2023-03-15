package dev.kurumi.chisataki.modmail.json;

import javax.json.*;
import javax.json.stream.*;

import net.dv8tion.jda.api.entities.concrete.TextChannel;

import java.util.Base64;

public class ChannelEncoded {

  private String name;
  private String id;
  private JsonObject json;

  public ChannelEncoded(TextChannel channel) {
     json = json.createObjectBuilder()
       .add("name", channel.getName())
       .add("id", channel.getId())
       .build();
    }

  public String getEncoded() {
    return Base64.getEncoder().encodeToString(json.toString());
  }
}