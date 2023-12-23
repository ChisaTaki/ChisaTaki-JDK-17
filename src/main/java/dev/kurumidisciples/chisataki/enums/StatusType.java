package dev.kurumidisciples.chisataki.enums;

public enum StatusType {
  UNANSWERED("unanswered"),
  ANSWERED("answered"),
  CLOSED("closed");

  private String id; 

  private StatusType(String id){
    this.id = id;
  }

  public String toString(){
    return id;
  }

  public static StatusType valueOfLabel(String val){
    for (StatusType type : StatusType.values()){
      if (type.toString().equalsIgnoreCase(val)) return type;
    }
    throw new IllegalArgumentException("Invalid status type: " + val);
    }
  }
