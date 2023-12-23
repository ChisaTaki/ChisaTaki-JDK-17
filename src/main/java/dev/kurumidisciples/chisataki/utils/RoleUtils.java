package dev.kurumidisciples.chisataki.utils;

import java.util.List;

import dev.kurumidisciples.chisataki.enums.RoleEnum;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleUtils {

    public static boolean isMemberStaff(Member member) {
        List<String> staffRoles = RoleEnum.getStaffRoles();
        return member.getRoles().stream()
            .map(Role::getId)
            .anyMatch(staffRoles::contains);
    }

    public static boolean isMemberBotDev(Member member) {
        return hasRole(member, RoleEnum.BOT_DEV);
    }
    
    public static boolean hasRole(Member member, RoleEnum role) {
    	return member.getRoles().stream()
                .map(Role::getId)
                .anyMatch(role.getId()::equals);
    }
}
