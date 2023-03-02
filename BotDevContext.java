import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BotDevContext extends ListenerAdapter {

  final static Logger logger = LoggerFactory.getLogger(BotDevContext.class);

  public void onMessageContextInteraction(MessageContextInteractionEvent event){
    Thread botThread = new Thread(){
      public void run(){
        if (event.getName().equals("Delete Bot Message")){
          event.deferReply(true).queue();
          if (!event.getTarget().getAuthor().getId().equals("1070074991653167144")){
            event.getHook().editOriginal("Message is not from ChisaTaki.").queue();
          }
          else if (canUse(event.getMember())){
            event.getHook().editOriginal("Message Deleted").queue();
            event.getTarget().delete().reason("context menu option was used").queue();
            logger.info("Deleted ChisaTaki Bot message");
          }
          else {
            event.getHook().editOriginal("You can't use this context menu option").queue();
          }
        }
      }
    };
    botThread.setName("Delete-Bot-Thread");
    botThread.setPriority(3);
    botThread.start();
  }



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
  private static boolean canUse(Member member){
    /* copied from isExcluded */
    List<Role> roles = member.getRoles();
    List<String> excluded = List.of(/*President*/"1016047777098256435", /*Vice President*/ "1016048573621739520", /*Bot Dev*/ "1044358875039666316", /*ChisaTaki Staff*/ "1016048811581382676");

    for (Role role : roles){
      for (String id : excluded){
        if (role.getId().equals(id)) return true;
      }
    }
    return false;
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