package entites;

public class Ticket {
    private int id;
    private String title;
    private String requester;
    private String[] operator;
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

    // TODO: Creating an instance of a class for an existing ticket.

}
