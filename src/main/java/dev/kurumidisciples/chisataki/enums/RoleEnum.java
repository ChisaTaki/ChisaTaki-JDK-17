package dev.kurumidisciples.chisataki.enums;

import java.util.List;

public enum RoleEnum {
  BOT_DEV("1044358875039666316"),
  CSTK_GUARD("1038887805234978897"),
  CSTK_STAFF("1016048811581382676"),
  PRESIDENT("1016047777098256435"),
  VICE_PRESIDENT("1016048573621739520"),
  BOOSTER("1020407366300205156"),
  
  BOT_ANNOUNCEMENT("1107470054829862972"),
  MANGA_UPDATES("1139737288520249414"),
  
  DEER_FRIEND("1257875525112823841"),
  LYCO_RECO_REWATCH("1257875660421202053"),
  OSHI_NO_KO("1257875624253587526"),
  
  CHISATO_SHRINE("1023697460763303936"),
  SHAREHOLDER("1064973449568718960"),
  TAKINA_SHRINE("1023698477240291439");

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
  
  public static List<String> getGroupWatchRoles() {
	    return List.of(DEER_FRIEND.id, LYCO_RECO_REWATCH.id, OSHI_NO_KO.id);
  }
}