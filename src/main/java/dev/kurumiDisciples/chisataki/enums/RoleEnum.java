package dev.kurumiDisciples.chisataki.enums;

import java.util.List;

public enum RoleEnum {
  BOT_DEV("1044358875039666316"),
  CSTK_GUARD("1038887805234978897"),
  CSTK_STAFF("1016048811581382676"),
  PRESIDENT("1016047777098256435"),
  VICE_PRESIDENT("1016048573621739520");

  private String id;

  private RoleEnum(String id) {
		this.id = id;
	}

  public String getId() {
    return this.id;
  }
  
  public static List<String> getStaffRoles() {
    return List.of(BOT_DEV.id, CSTK_GUARD.id, CSTK_STAFF.id, PRESIDENT.id, VICE_PRESIDENT.id);
  }
}