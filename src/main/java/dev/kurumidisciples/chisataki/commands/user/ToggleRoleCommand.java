package dev.kurumidisciples.chisataki.commands.user;

import dev.kurumidisciples.chisataki.enums.RoleEnum;
import dev.kurumidisciples.chisataki.utils.RoleUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class ToggleRoleCommand extends UserCommand {
	
	private RoleEnum roleEnum;

	public ToggleRoleCommand(String name, RoleEnum role) {
		super(name, Permission.VIEW_AUDIT_LOGS);
		this.roleEnum = role;
	}

	@Override
	public void execute(UserContextInteractionEvent event) {
		event.deferReply(true).queue();
		
		Member targetedMember = event.getTargetMember();
		Role role = event.getGuild().getRoleById(roleEnum.getId());
		String auditMemberName = event.getMember().getEffectiveName();
		
		String message;
		if (RoleUtils.hasRole(targetedMember, roleEnum)) {
			event.getGuild().removeRoleFromMember(targetedMember, role)
							.reason(auditMemberName + " used a context menu to remove a role")
							.queue();
			message = "Role `" + role.getName() + "` removed from member";
		} else {
			event.getGuild().addRoleToMember(targetedMember, role)
							.reason(auditMemberName + " used a context menu to add a role")
							.queue();
			message = "Role `" + role.getName() + "` added to member";
		}

		event.getHook().editOriginal(message).queue();
	}
}
