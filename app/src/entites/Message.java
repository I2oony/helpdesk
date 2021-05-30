package entites;

import java.util.Date;

public class Message {
    private int id;
    private String from;
    private String text;
    private Date time;

    public Message(int id, String from, String text, Date ts) {
        this.id = id;
        this.from = from;
        this.text = text;
        this.time = time;
    }
}
