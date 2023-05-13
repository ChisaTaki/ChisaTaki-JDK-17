package dev.kurumiDisciples.chisataki;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotDevContext extends ListenerAdapter {

  public void onUserContextInteraction(UserContextInteractionEvent event){
    Thread userMenu = new Thread(){
      public void run(){
        if (event.getName().equals("Remove Chisato's Solider Role")){
          event.deferReply(true).queue();
          
          if (hasRole(event.getTargetMember(), event.getGuild().getRoleById(1013558607213756518L))){
            //remove role from member 
            event.getGuild().removeRoleFromMember(event.getTargetMember(), event.getGuild().getRoleById(1013558607213756518L)).reason(
              event.getMember().getEffectiveName() + " used a context menu to remove a role"
            )
              .queue();
            event.getHook().editOriginal("Role removed from member.").queue();
          }
          else {
            event.getHook().editOriginal("Member does not have this role").queue();
          }
        }

        else if (event.getName().equals("Remove Takina's Sakana Role")){
          event.deferReply(true).queue();
          if (hasRole(event.getTargetMember(), event.getGuild().getRoleById(1013567857075953706L))){
            //remove role from member 
            event.getGuild().removeRoleFromMember(event.getTargetMember(), event.getGuild().getRoleById(1013567857075953706L)).reason(
              event.getMember().getEffectiveName() + " used a context menu to remove a role"
            )
              .queue();
            event.getHook().editOriginal("Role removed from member.").queue();
          }
          else {
            event.getHook().editOriginal("Member does not have this role").queue();
          }
          
        }
        else if (event.getName().equals("Give User Shareholder Role")){
          event.deferReply(true).queue();
          event.getGuild().addRoleToMember(event.getTargetMember(), event.getGuild().getRoleById(1064973449568718960L)).queue();
          event.getHook().editOriginal("Shareholder added").queue();
        }
      }
    };
    userMenu.setName("Role-Correction-Thread");
    userMenu.setPriority(1);
    userMenu.start();
  }
  
   private static boolean hasRole(Member member, Role role) {
    List<Role> memberRoles = member.getRoles();

    for (Role r : memberRoles) {
      if (r.getId().equals(role.getId()))
        return true;
    }

    return false;
  }

}