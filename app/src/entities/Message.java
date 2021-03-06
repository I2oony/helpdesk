package entities;

import org.bson.Document;

import java.util.Date;

public class Message {
    private int id;
    private final String from;
    private final String text;
    private final Date date;

    public Message(int id, String from, String text, Date date) {
        this.id = id;
        this.from = from;
        this.text = text;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("id", id)
                .append("from", from)
                .append("text", text)
                .append("date", date);
        return document;
    }
}
