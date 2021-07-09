package entities;

import org.bson.Document;

import java.util.ArrayList;

public class Operator {
    private final String username;
    private String status;
    private ArrayList<Integer> tickets;

    public Operator(String username, String status, ArrayList<Integer> tickets) {
        this.username = username;
        this.status = status;
        this.tickets = tickets;
    }

    public void changeStatus() {
        if (status.equals("offline")) {
            status = "online";
        } else {
            status = "offline";
        }
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("username", username)
                .append("status", status)
                .append("tickets", tickets);
        return document;
    }

    public boolean popTicket(int ticketId) throws Exception {
        return tickets.remove((Integer) ticketId);
    }

    public boolean pushTicket(int ticketId) throws Exception {
        if (tickets.size() < 5) {
            return tickets.add(ticketId);
        } else {
            return false;
        }
    }

    public int getTicketsCount() {
        return this.tickets.size();
    }

    public String getUsername() {
        return this.username;
    }
}
