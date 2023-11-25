package dev.kurumidisciples.chisataki.listeners;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.enums.RoleEnum;
import dev.kurumidisciples.chisataki.utils.CooldownUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/* To Do: Add event loggers */

public class RoleMenuInteraction extends ListenerAdapter {

  StringSelectInteractionEvent EVENT;

  private static final List<String> shrineIDs = List.of(/* chisato */"1013558607213756518", /* takina */ "1013567857075953706");
  private static final List<String> serverIDs = List.of(/* server announcement */ "1013809351108079636",
      /* event */ "1013809301342662726", /* Chisataki */ "1013809402547011616", RoleEnum.BOT_ANNOUNCEMENT.getId(), 
      /* groupwatch */ "1025081700570636318", RoleEnum.MANGA_UPDATES.getId());

  final static Logger logger = LoggerFactory.getLogger(RoleMenuInteraction.class);

  public void onStringSelectInteraction(StringSelectInteractionEvent event) {
    Thread roleThread = new Thread() {
      public void run() {
        EVENT = event;
        if (event.getComponentId().equals("menu:role:groupwatch")) {
          String format = "Your groupwatch roles are now: %s!";
          Guild guild = event.getGuild();
          event.deferReply(true).queue();
          removeGroupRoles(event.getMember());
          String roleName = "";
          for (String s : event.getValues()) {
            switch (s) {
            	case "sxfSelect":
	                roleHandle(event.getMember(), guild.getRoleById(RoleEnum.SPY_X_FAMILY.getId()));
	                roleName += "`Spy x Family S2`, ";
	                break;
            	case "starSelect":
	                roleHandle(event.getMember(), guild.getRoleById(RoleEnum.STARDUST.getId()));
	                roleName += "`Stardust Telepath`, ";
	                break;
            	case "wataoshiSelect":
	                roleHandle(event.getMember(), guild.getRoleById(RoleEnum.WATAOSHI.getId()));
	                roleName += "`WataOshi`, ";
	                break;
            }
          }
          try{
          roleName = roleName.substring(0, roleName.length() - 2);
          format = String.format(format, roleName);
          }
          catch(StringIndexOutOfBoundsException e){
            format = "I've removed all groupwatch roles from you!";
          }
          event.getHook().editOriginal(format).queue();
        } else if (event.getComponentId().equals("menu:role:server")) {
          String format = "Your server roles are now: %s!";
          Guild guild = event.getGuild();
          event.deferReply(true).queue();
          removeServerRoles(event.getMember());
          String roleName = "";
          for (String s : event.getValues()) {

            switch (s) {
              case "announceSelect":
                roleHandle(event.getMember(), guild.getRoleById("1013809351108079636"));
                roleName += "`Server Announcement`, ";
                break;
              case "eventSelect":
                roleHandle(event.getMember(), guild.getRoleById("1013809301342662726"));
                roleName += "`Event Announcement`, ";
                break;
              case "chisaSelect":
                roleHandle(event.getMember(), guild.getRoleById("1013809402547011616"));
                roleName += "`ChisaTaki Announcement`, ";
                break;
              case "botSelect":
                roleHandle(event.getMember(), guild.getRoleById(RoleEnum.BOT_ANNOUNCEMENT.getId()));
                roleName += "`Bot Announcement`, ";
                break;
              case "groupSelect":
                roleHandle(event.getMember(), guild.getRoleById("1025081700570636318"));
                roleName += "`Groupwatch`, ";
                break;
              case "mangaSelect":
                  roleHandle(event.getMember(), guild.getRoleById(RoleEnum.MANGA_UPDATES.getId()));
                  roleName += "`Manga Updates`, ";
                  break;
            }
          }
          try{
            roleName = roleName.substring(0, roleName.length() - 2);
            format = String.format(format, roleName);
          }
          catch(StringIndexOutOfBoundsException e){
            format = "I've removed all server roles from you!";
          }
          event.getHook().editOriginal(format).queue();
        } else if (event.getComponentId().equals("menu:role:shrine")) {
          event.deferReply(true).queue();

          if (canUpdateShrineRoles(event.getUser())) {
            updateShrineRoles(event);
          } else {
            String message = "Please wait an hour before changing your allegiances";
            event.getHook().editOriginal(message).queue();
          }
        }
      }
    };
    roleThread.setName("Role-Thread");
    roleThread.start();
  }

  private static boolean canUpdateShrineRoles(User user) {
    LocalDateTime previousDateTime = CooldownUtils.getPreviousDateTime(user);
    return CooldownUtils.hasCooldownElapsed(previousDateTime);
  }

  private static void updateShrineRoles(StringSelectInteractionEvent event) {
    Guild guild = event.getGuild();
    removeShrineRoles(event.getMember());
    removeLostRole(event.getMember());
    for (String s : event.getValues()) {
      switch (s) {
        case "chisatoSelect":
          roleHandle(event.getMember(), guild.getRoleById("1013558607213756518"));
          break;
        case "takinaSelect":
          roleHandle(event.getMember(), guild.getRoleById("1013567857075953706"));
          break;
      }
    }

    CooldownUtils.updateUsersCooldown(event.getUser());
    event.getHook().editOriginal("Role Added").queue();
  }

  public void onButtonInteraction(ButtonInteractionEvent event) {
    Thread buttonThread = new Thread() {
      public void run() {
        if (event.getComponentId().equals("chisaButton")) {
          Guild guild = event.getGuild();
          event.deferReply(true).queue();
          if (hasRole(event.getMember(), guild.getRoleById("1010080294692458496"))) {
            guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1010080294692458496")).queue();
          } else {
            guild.addRoleToMember(event.getMember(), guild.getRoleById("1010080294692458496")).queue();
          }
          event.getHook().editOriginal("Role Removed/Added").queue();
        }
      }
    };
    buttonThread.setName("Button-Thread");
    buttonThread.start();
  }
  
  private static void roleHandle(Member member, Role role) {
    Guild guild = member.getGuild();
    try {
      guild.addRoleToMember(member, role).complete();
    } catch (Exception e) {
      // nothing
    }
  }

  private static boolean hasRole(Member member, Role role) {
    List<Role> memberRoles = member.getRoles();

    for (Role r : memberRoles) {
      if (r.getId().equals(role.getId()))
        return true;
    }

    return false;
  }

  private static void removeShrineRoles(Member member) {
    Guild guild = member.getGuild();

    for (String id : shrineIDs) {
      Role role = guild.getRoleById(id);
      if (hasRole(member, role)) {
        guild.removeRoleFromMember(member, role).queue();
      }
    }

  }

  private static void removeServerRoles(Member member) {
    Guild guild = member.getGuild();

    for (String id : serverIDs) {
      Role role = guild.getRoleById(id);
      if (hasRole(member, role)) {
        guild.removeRoleFromMember(member, role).queue();
      }
    }
  }

  private static void removeGroupRoles(Member member) {
    Guild guild = member.getGuild();

    for (String id : RoleEnum.getGroupWatchRoles()) {
      Role role = guild.getRoleById(id);
      if (hasRole(member, role)) {
        try {
          guild.removeRoleFromMember(member, role).complete();
        } catch (Exception e) {

        }
      }
    }
  }

  private static void removeLostRole(Member member) {
    Guild guild = member.getGuild();
    Role lostRole = guild.getRoleById("1013807894531166309");

    try {
      guild.removeRoleFromMember(member, lostRole).reason("Role Bot Function").queue();
    } catch (InsufficientPermissionException e) {
      logger.error("Insufficieent Permission Error Caught. Reason: {}", e.getMessage());
    } catch (HierarchyException e) {
      logger.error("Hierarchy Error Caught. Reason: {}", e.getMessage());
    }
  }
}