package dev.kurumiDisciples.chisataki.modmail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonObject;

import dev.kurumiDisciples.chisataki.enums.StatusType;
import dev.kurumiDisciples.chisataki.internal.database.middlemen.GenericDatabaseTable;

public class Ticket implements GenericDatabaseTable{

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
  @Nonnull
  protected Ticket( int ticketNumber, long ticketId, long memberId, String subject, String body, StatusType status) {
    this.ticketNumber = ticketNumber;
    this.ticketId = ticketId;
    this.memberId = String.valueOf(memberId);
    this.subject = subject;
    this.body = body;
    this.status = status;
  }
  /* This one has reason and staff */
  @Nullable
  protected Ticket(ResultSet set){
    try {
      this.ticketNumber = set.getInt("ticket");
      this.ticketId = set.getLong("ticket_id");
      this.memberId = String.valueOf(set.getLong("member_id"));
      this.staffId = String.valueOf(set.getLong("staff_id"));
      this.subject = set.getString("subject");
      this.body = set.getString("body");
      this.status = StatusType.valueOfLabel(set.getString("status"));
      this.reason = set.getString("reason");
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  public Ticket(){
    
  }


  // grabs a already existing ticket
  public Ticket(int ticketNumber) {
    ResultSet set = TicketDatabaseUtils.selectTicket(ticketNumber);
    if (set == null) {
        throw new NullPointerException("Ticket does not exist");
    }
    try { 
        if (set.next()) { // Move the cursor to the first row
            this.ticketNumber = ticketNumber;
            this.ticketId = set.getLong("ticket_id");
            this.memberId = String.valueOf(set.getLong("member_id"));
            this.staffId = String.valueOf(set.getLong("staff_id"));
            this.subject = set.getString("subject");
            this.body = set.getString("body");
            this.status = StatusType.valueOfLabel(set.getString("status"));
            this.reason = set.getString("reason");
        }
        // else handle the case where the result set is empty
    } catch (SQLException e) {
        e.printStackTrace();
    } catch (NullPointerException e) {
        e.printStackTrace();
    }
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
    TicketDatabaseUtils.insertStaffIntoTicket(Long.parseLong(staffId), ticketNumber);
    return this;
  }

  public Ticket setStatus(StatusType status) {
    this.status = status;
    TicketDatabaseUtils.insertStatusIntoTicket(status.toString(), ticketNumber);
    return this;
  }

  public Ticket setReason(String reason) {
    this.reason = reason;
    TicketDatabaseUtils.insertReasonIntoTicket(reason, ticketNumber);
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
    return TicketDatabaseUtils.countTickets();
  }

  @Override
  public String getTableName() {
    return "tickets";
  }

  @Override
  public String getTableSchema() {
    return "CREATE TABLE IF NOT EXISTS tickets ("
        + "ticket INT NOT NULL,"
        + "ticket_id BIGINT NOT NULL,"
        + "member_id BIGINT NOT NULL,"
        + "staff_id BIGINT,"
        + "subject VARCHAR(255) NOT NULL,"
        + "body VARCHAR(255) NOT NULL,"
        + "status VARCHAR(255) NOT NULL,"
        + "reason VARCHAR(255),"
        + "PRIMARY KEY (ticket)"
        + ");";
  }

  @Override
  public String getPrimaryKey() {
    return "ticket";
  }

  @Override
  public Integer getPrimaryKeyType() {
    return java.sql.Types.INTEGER;
  }

  @Override
  public HashMap<String, Integer> getDefinedColumns(){
    HashMap<String, Integer> columns = new HashMap<>();
    columns.put("ticket", java.sql.Types.INTEGER);
    columns.put("ticket_id", java.sql.Types.BIGINT);
    columns.put("member_id", java.sql.Types.BIGINT);
    columns.put("staff_id", java.sql.Types.BIGINT);
    columns.put("subject", java.sql.Types.VARCHAR);
    columns.put("body", java.sql.Types.VARCHAR);
    columns.put("status", java.sql.Types.VARCHAR);
    columns.put("reason", java.sql.Types.VARCHAR);
    return columns;
  }
}