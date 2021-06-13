package entites;

import org.bson.Document;

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

    public int getId() {
        return id;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("id", id)
                .append("title", title)
                .append("requester", requester)
                .append("operator", operator)
                .append("state", state)
                .append("messages", messages);
        return document;
    }

    public void addMessage(Message message) {
        Message[] messages = new Message[totalMessages+1];
        System.arraycopy(this.messages, 0, messages, 0, totalMessages);
        messages[totalMessages] = message;
        this.messages = messages;
    }
}
