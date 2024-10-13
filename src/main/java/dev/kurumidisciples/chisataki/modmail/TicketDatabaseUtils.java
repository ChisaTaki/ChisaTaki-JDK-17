package dev.kurumidisciples.chisataki.modmail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kurumidisciples.chisataki.internal.database.Database;
import dev.kurumidisciples.chisataki.internal.database.exceptions.InitializationException;

public class TicketDatabaseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketDatabaseUtils.class);

    private static final String INSERT_STAFF_INTO_TICKET = "UPDATE tickets SET staff_id = ? WHERE ticket = ?;";
    private static final String INSERT_STATUS_INTO_TICKET = "UPDATE tickets SET status = ? WHERE ticket = ?;";
    private static final String INSERT_REASON_INTO_TICKET = "UPDATE tickets SET reason = ? WHERE ticket = ?;";
    private static final String INSERT_TICKET = "INSERT INTO tickets (ticket, ticket_id, member_id, subject, body, status) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String SELECT_TICKET = "SELECT * FROM tickets WHERE ticket = ?;";
    private static final String COUNT_TICKETS = "SELECT COUNT(*) FROM tickets;";

    public static int insertStaffIntoTicket(long staffId, int ticketNumber) {
        try (PreparedStatement statement = Database.createStatement(INSERT_STAFF_INTO_TICKET)) {
            statement.setLong(1, staffId);
            statement.setInt(2, ticketNumber);
            return statement.executeUpdate();
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert staff into ticket {} for reason: {}", ticketNumber, e.getMessage());
            return 0;
        }
    }

    public static int insertStatusIntoTicket(String status, int ticketNumber) {
        try (PreparedStatement statement = Database.createStatement(INSERT_STATUS_INTO_TICKET)) {
            statement.setString(1, status);
            statement.setInt(2, ticketNumber);
            return statement.executeUpdate();
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert status into ticket {} for reason: {}", ticketNumber, e.getMessage());
            return 0;
        }
    }

    public static int insertReasonIntoTicket(String reason, int ticketNumber) {
        try (PreparedStatement statement = Database.createStatement(INSERT_REASON_INTO_TICKET)) {
            statement.setString(1, reason);
            statement.setInt(2, ticketNumber);
            return statement.executeUpdate();
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert reason into ticket {} for reason: {}", ticketNumber, e.getMessage());
            return 0;
        }
    }

    public static int insertTicket(Ticket ticket){
        try (PreparedStatement statement = Database.createStatement(INSERT_TICKET)) {
            statement.setInt(1, ticket.getTicketNumber());
            statement.setLong(2, ticket.getTicketId());
            statement.setLong(3, ticket.getMemberId());
            statement.setString(4, ticket.getSubject());
            statement.setString(5, ticket.getBody());
            statement.setString(6, ticket.getStatus().toString());
            return statement.executeUpdate();
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to insert ticket {} for reason: {}", ticket.getTicketNumber(), e.getMessage());
            return 0;
        }
    }

    public static ResultSet selectTicket(int ticketNumber){
        try (PreparedStatement statement = Database.createStatement(SELECT_TICKET)) {
            statement.setInt(1, ticketNumber);
            return statement.executeQuery();
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to select ticket {} for reason: {}", ticketNumber, e.getMessage());
            return null;
        }
    }

    public static Ticket resultSetToTicket(ResultSet set){
            return new Ticket(set);
    }

    public static int countTickets(){
        try (PreparedStatement statement = Database.createStatement(COUNT_TICKETS)) {
            ResultSet set = statement.executeQuery();
            set.next();
            return set.getInt(1);
        }
        catch (SQLException | InitializationException e) {
            LOGGER.error("Failed to count tickets for reason: {}", e.getMessage());
            return 0;
        }
    }
}
