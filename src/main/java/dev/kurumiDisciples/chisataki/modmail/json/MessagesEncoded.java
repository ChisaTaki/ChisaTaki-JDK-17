package dev.kurumidisciples.chisataki.modmail.json;

import java.util.List;
import java.util.ArrayList;

import java.util.Base64;

import javax.json.*;
import javax.json.stream.*;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class MessagesEncoded {
  private List<Message> messages;

  private JsonArray jsonArray;

  public MessagesEncoded(List<Message> usedMessage) {

    messages = reverseList(usedMessage);

    createJsonArray();
}

  public String getEncoded() {
    return Base64.getEncoder().encodeToString(jsonArray.toString().getBytes());
  }
  private void createJsonArray() {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

    for (Message message : messages) {
      jsonArrayBuilder.add(Json.createObjectBuilder()
                           .add("discordData", addDiscordData(message))
                           .add("attachments", addAttachments(message))
                           .add("reactions", addReactions(message))
                           .add("embeds", createEmbedObject(message))
                           .add("content", message.getContentRaw())
                           .add("components", Json.createArrayBuilder())
                           .add("user_id", message.getAuthor().getId())
                           .add("bot", message.getAuthor().isBot())
                           .add("username", message.getAuthor().getName())
                           .add("nick", message.getMember().getEffectiveName())
                           .add("tag", message.getAuthor().getDiscriminator())
                           .add("avatar", message.getAuthor().getAvatarUrl().split("/")[5].split("\\.")[0])
                           .add("id", message.getId())
                           .add("created", message.getTimeCreated().toEpochSecond())
                           //edited is temp
                           .addNull("edited")
                               );
    }
    jsonArray = jsonArrayBuilder.build();
  }

  
private JsonArrayBuilder createEmbedObject(Message message) {
  JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
  message.getEmbeds().forEach(embed -> {
    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    objectBuilder.add("description", embed.getDescription() != null ? embed.getDescription() : "`no description`");
    objectBuilder.add("color", embed.getColor() != null ? "#" + Integer.toHexString(embed.getColor().getRGB()).substring(2) : "#eb4034");

    objectBuilder.add("footer", embed.getFooter() != null ? 
                     Json.createObjectBuilder()
                      .add("text", embed.getFooter().getText() != null? embed.getFooter().getText() : "not set")
                      .add("iconUrl", embed.getFooter().getIconUrl()!= null? embed.getFooter().getIconUrl() : "https://www.example.com") : Json.createObjectBuilder()
                     );
      JsonArrayBuilder fieldBuilder = Json.createArrayBuilder();

    embed.getFields().forEach(field -> {
    JsonObjectBuilder fieldObjectBuilder = Json.createObjectBuilder();
    fieldObjectBuilder.add("name", field.getName()!= null? field.getName() : "not set");
    fieldObjectBuilder.add("value", field.getValue()!= null? field.getValue() : "not set");
    fieldObjectBuilder.add("inline", field.isInline());
    });
      objectBuilder.add("fields", fieldBuilder);

    arrayBuilder.add(objectBuilder);
      });
  return arrayBuilder;
}

  private JsonObjectBuilder addDiscordData(Message message){

    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

    message.getMentions().getMembers().forEach(member -> {
      objectBuilder.add(member.getId(),
                       Json.createObjectBuilder()
                        .add("name", member.getUser().getName())
                        .add("tag", member.getUser().getDiscriminator())
                        .add("nick", member.getEffectiveName())
                        .add("avatar", member.getUser().getAvatarUrl().split("/")[5].split("\\.")[0])
                       );
    });
    return objectBuilder;
  }

  public static <T> List<T> reverseList(List<T> list) {
        List<T> reversedList = new ArrayList<>(list);
        int size = reversedList.size();
        for (int i = 0; i < size / 2; i++) {
            T temp = reversedList.get(i);
            reversedList.set(i, reversedList.get(size - i - 1));
            reversedList.set(size - i - 1, temp);
        }
        return reversedList;
    }

  private JsonArrayBuilder addReactions(Message message){
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    message.getReactions().forEach(reaction -> {
      JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
      objectBuilder.add("id", reaction.getEmoji().getType() == Emoji.Type.CUSTOM ? reaction.getEmoji().asCustom().getId() : "");
      objectBuilder.add("name", reaction.getEmoji().getName());
      objectBuilder.add("animated", reaction.getEmoji().getType() == Emoji.Type.CUSTOM ? reaction.getEmoji().asCustom().isAnimated() : false);
      objectBuilder.add("count", reaction.hasCount() ? reaction.getCount() : 1);
      arrayBuilder.add(objectBuilder);
    });
    return arrayBuilder;
  }
  
  private JsonArrayBuilder addAttachments(Message message){
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    message.getAttachments().forEach(attachment -> {
      JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
      objectBuilder.add("Base64", "");
      objectBuilder.add("url", attachment.getUrl());
      objectBuilder.add("size", attachment.getSize());
      objectBuilder.addNull("height");
      objectBuilder.addNull("width");
    });
    return arrayBuilder;
  }
}