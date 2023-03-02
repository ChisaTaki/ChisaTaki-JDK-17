package dev.kurumiDisciples.chisataki.utils;

import dev.kurumiDisciples.chisataki.utils.FileUtils;
import dev.kurumiDisciples.chisataki.enums.StatusType;

import javax.json.*;
import javax.json.stream.*;

import java.io.File;

public class Ticket {

  int ticketNumber;
  long ticketId;
  String memberId;
  String staffId;
  String subject;
  String body;
  StatusType status;
  String reason = null;
  JsonObject json;

  // from jsonobject
  public Ticket(JsonObject json) {
    this.json = json;
    this.ticketNumber = json.getJsonNumber("ticket").intValue();
    this.ticketId = json.getJsonNumber("ticket-id").longValue();
    this.memberId = json.getString("member");
    this.staffId = json.getString("staff", null);
    this.subject = json.getString("subject");
    this.body = json.getString("body");
    this.status = StatusType.valueOfLabel(json.getString("status"));
    this.reason = json.getString("reason", null);

  }

  // grabs a already existing ticket
  public Ticket(int ticketNumber) {
    this.json = FileUtils.getFileContent("data/tickets/ticket-" + String.valueOf(ticketNumber) + ".json");
    this.ticketNumber = json.getJsonNumber("ticket").intValue();
    this.ticketId = json.getJsonNumber("ticket-id").longValue();
    this.memberId = json.getString("member");
    this.staffId = json.getString("staff", null);
    this.subject = json.getString("subject");
    this.body = json.getString("body");
    this.status = StatusType.valueOfLabel(json.getString("status"));
    this.reason = json.getString("reason", null);
  }

  public long getStaffId() {
    return Long.parseLong(staffId);
  }

  public long getTicketId() {
    return ticketId;
  }

  public String getBody() {
    return body;
  }

  public String getSubject() {
    return subject;
  }

  public StatusType getStatus() {
    return status;
  }

  public String getReason() {
    if (reason == null) return "N/A";
    return reason;
  }

  public long getMemberId() {
    return Long.parseLong(memberId);
  }

  public JsonObject getJsonObject() {
    return json;
  }

  public int getTicketNumber() {
    return ticketNumber;
  }

  public Ticket setStaff(String staffId) {
    this.staffId = staffId;
    this.json = Json.createObjectBuilder(getJsonObject()).add("staff", staffId).build();
    TicketBuilder.updateTicketFile(this);
    return this;
  }

  public Ticket setStatus(StatusType status) {
    this.status = status;
    this.json = Json.createObjectBuilder(getJsonObject()).add("status", status.toString()).build();
    TicketBuilder.updateTicketFile(this);
    return this;
  }

  public Ticket setReason(String reason) {
    this.reason = reason;
    this.json = Json.createObjectBuilder(getJsonObject()).add("reason", reason).build();
    TicketBuilder.updateTicketFile(this);
    return this;
  }

  public String getJsonPath() {
    return "data/tickets/ticket-" + this.ticketNumber + ".json";
  }

  public boolean hasStaff() {
    return staffId != null;
  }

  public boolean isClosed(){
    return getStatus() == StatusType.CLOSED;
  }

  private static int countFiles() {
    String directoryPath = "data/tickets";
    File directory = new File(directoryPath);
    if (!directory.exists()) {
      System.out.println("Directory does not exist.");
      return 0;
    }
    if (!directory.isDirectory()) {
      System.out.println("Not a directory.");
      return 0;
    }
    File[] files = directory.listFiles();
    return files.length;
  }
}