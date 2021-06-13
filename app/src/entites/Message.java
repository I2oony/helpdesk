package entites;

import org.bson.Document;

import java.util.Date;

public class Message {
    private int id;
    private String from;
    private String text;
    private Date time;

    public Message(int id, String from, String text, Date time) {
        this.id = id;
        this.from = from;
        this.text = text;
        this.time = time;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("id", id)
                .append("from", from)
                .append("text", text)
                .append("time", time);
        return document;
    }
}
