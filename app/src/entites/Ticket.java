package entites;

import java.util.ArrayList;

public class Ticket {
    private int id;
    private String title;
    private String requester;
    private ArrayList<String> operator;
    private String state;
    private Message[] messages;
    private int totalMessages;

    // Creating an instance of the class for a new ticket.
    public Ticket(String title, String requester, Message message) {
        this.title = title;
        this.requester = requester;

        totalMessages = 1;
        messages = new Message[totalMessages];
        messages[0] = message;

        state = "created";
    }

    // Creating an instance of a class for an existing ticket.
    public Ticket(int id, String title, String requester, ArrayList<String> operator, String state, Message[] messages) {
        this.id = id;
        this.title = title;
        this.requester = requester;
        this.operator = operator;
        this.state = state;
        this.messages = messages;
        this.totalMessages = messages.length;
    }
}
