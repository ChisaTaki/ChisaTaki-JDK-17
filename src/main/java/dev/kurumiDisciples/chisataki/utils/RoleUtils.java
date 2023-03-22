package dev.kurumiDisciples.chisataki.utils;

import java.util.List;

import dev.kurumiDisciples.chisataki.enums.RoleEnum;
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
        String botDevRole = RoleEnum.BOT_DEV.getId();
        return member.getRoles().stream()
            .map(Role::getId)
            .anyMatch(botDevRole::equals);
    }
}
