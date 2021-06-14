package entites;

import org.bson.Document;

import java.util.ArrayList;

public class Ticket {
    private int id;
    private final String title;
    private final String requester;
    private ArrayList<String> operator;
    private final String state;
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
        ArrayList documentMessages = new ArrayList();
        for (int i=0; i<totalMessages; i++) {
            documentMessages.add(messages[i].toDocument());
        }

        Document document = new Document();
        document.append("ticketId", id)
                .append("title", title)
                .append("requester", requester)
                .append("operator", operator)
                .append("state", state)
                .append("messages", documentMessages);
        return document;
    }

    public void addMessage(Message message) {
        Message[] messages = new Message[totalMessages+1];
        System.arraycopy(this.messages, 0, messages, 0, totalMessages);
        message.setId(totalMessages);
        messages[totalMessages] = message;
        this.messages = messages;
        this.totalMessages++;
    }
}
