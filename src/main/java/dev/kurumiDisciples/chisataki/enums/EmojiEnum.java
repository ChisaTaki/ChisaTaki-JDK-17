package dev.kurumiDisciples.chisataki.enums;

public enum EmojiEnum {
  CHINANAGO("<:Chinanago:1120915801680134185>"), CHISATO_HEART("<:ChisaTakiHeart2:1035455134253199411>"),
  CHISATO_PADORU("<:ChisatoPadoru:1016960238139736174>"), SAKANA("<:Sakana:1016650006662496326>"),
  TAKINA_PADORU("<:TakinaPadoru:1016960249455968277>");

  private String textFormat;

  private EmojiEnum(String textFormat) {
    this.textFormat = textFormat;
  }

  public String getAsText() {
    return this.textFormat;
  }
}