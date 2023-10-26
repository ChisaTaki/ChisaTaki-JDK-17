package dev.kurumiDisciples.chisataki.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*json classes */
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

/* Logging classes */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/* TO DO */
/* Add Loggers */
@Deprecated
public class RecordRolesInteraction extends ListenerAdapter {

    final static Logger logger = LoggerFactory.getLogger(RecordRolesInteraction.class);

    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Thread memberLeave = new Thread() {
            public void run() {
                /* Save all roles and perms */
                logger.info("attempting to record data for {}", event.getMember().getEffectiveName());
                List<GuildChannel> channels = event.getGuild().getChannels();

                /* grab the member if possible */
                Member left = event.getMember();

                /*
                 * create a map with the guildchannel as the key and the enumset of permissions
                 * as the value
                 */
                Map<GuildChannel, EnumSet<Permission>> channelPermMap = new HashMap<GuildChannel, EnumSet<Permission>>();

                /*
                 * for every channel we want to place the perms inside the map with the
                 * corressponding map
                 */
                for (GuildChannel channel : channels) {
                    channelPermMap.put(channel, left.getPermissionsExplicit(channel));
                }
                /* grab the user's roles */
                List<Role> userRoles = left.getRoles();
                archive(createMemberJsonObject(channelPermMap, userRoles), event.getMember());
                logger.info("created record for {}", event.getMember().getEffectiveName());
            }
        };
        memberLeave.setName("Member-Leave");
        memberLeave.start();
    }

    private static JsonObject getUserSave() {
        while (true) {
            try {
                FileReader reader = new FileReader(new File("data/userLeft.json"));
                JsonReader Jreader = Json.createReader(reader);
                JsonObject t = Jreader.readObject();
                if (t != null) {
                    return t;
                }
            } catch (JsonException jsonexcept) {
                logger.debug("Error reading userLeft.json");
            } catch (IllegalStateException illegal) {
                logger.debug("illegal state caught - retrying");
            } catch (FileNotFoundException e) {
                logger.debug("File not found - retrying");
            }
        }
    }

    private static long toPermissionsLong(EnumSet<Permission> permissions) {
        return Permission.getRaw(permissions);
    }

    private static JsonObject createMemberJsonObject(Map<GuildChannel, EnumSet<Permission>> channelPermMap,
            List<Role> userRoles) {
        JsonObjectBuilder memberObject = Json.createObjectBuilder();

        /*
         * "channel": [ { "1011237892623958076": "permissionLong" } ],
         */
        JsonArrayBuilder channelArray = Json.createArrayBuilder();
        JsonObjectBuilder channelArrayObject = Json.createObjectBuilder();
        for (Map.Entry<GuildChannel, EnumSet<Permission>> set : channelPermMap.entrySet()) {
            channelArrayObject.add(set.getKey().getId(), String.valueOf(toPermissionsLong(set.getValue())));
        }
        channelArray.add(channelArrayObject);
        memberObject.add("channel", channelArray);

        /*
         * "roles": [ "1044358875039666316" ]
         */
        JsonArrayBuilder roleArray = Json.createArrayBuilder();

        for (Role role : userRoles) {
            roleArray.add(role.getId());
        }

        memberObject.add("roles", roleArray);

        return memberObject.build();
    }

    private static void archive(JsonObject jso, Member member /* member being added */) {
        try {
            /* retrieve old file */
            JsonObject oldJson = getUserSave();
            oldJson = insertObject(oldJson, jso, member.getId());
            FileWriter t = new FileWriter("data/" + "userLeft" + ".json");
            t.write(oldJson.toString());
            t.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /************************************
     * JsonObject*Modifiers *
     *************************************************/

    private static JsonObject removeProperty(JsonObject origin, String key) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        for (Map.Entry<String, JsonValue> entry : origin.entrySet()) {
            if (entry.getKey().equals(key)) {
                continue;
            } else {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private static JsonObject insertValue(JsonObject source, String key, String value) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(key, value);
        source.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
        return builder.build();
    }

    private static JsonObject insertObject(JsonObject parent, JsonObject child, String childName) {

        JsonObjectBuilder child_builder = Json.createObjectBuilder();
        JsonObjectBuilder parent_builder = Json.createObjectBuilder();
        parent.entrySet().forEach(e -> parent_builder.add(e.getKey(), e.getValue()));
        child.entrySet().forEach(e -> child_builder.add(e.getKey(), e.getValue()));
        parent_builder.add(childName, child_builder);
        return parent_builder.build();
    }

  private static List<Role> getBlackListedRoles(Guild guild /*Only ChisaTaki*/){
    List<Role> roles = new ArrayList<Role>();
    roles.add(guild.getBoostRole());
    roles.add(guild.getRoleById("1010079865082478622" /*admin*/));
    roles.add(guild.getRoleById("1016046889310883910") /*mod*/);

    return roles;
  }
}
