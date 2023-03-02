package dev.kurumiDisciples.chisataki.utils;

import java.util.List;

import dev.kurumiDisciples.chisataki.enums.RoleEnum;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleUtils {

	public static boolean isMemberStaff(Member member) {
	    List<Role> roles = member.getRoles();
	    List<String> staffRoles = RoleEnum.getStaffRoles();
	    
	    for (Role role : roles) {
	    	if (staffRoles.contains(role.getId())) {
	    		return true;
	    	}
	    }
	    
	    return false;
	}

  public static boolean isMemberBotDev(Member member) {
	    List<Role> roles = member.getRoles();
	    String botDevRole = RoleEnum.BOT_DEV.getId();
	    
	    for (Role role : roles) {
	    	if (botDevRole.equals(role.getId())) {
	    		return true;
	    	}
	    }
	    
	    return false;
	}
}
