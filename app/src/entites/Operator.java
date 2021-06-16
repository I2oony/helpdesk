package entites;

import org.bson.Document;

public class Operator {
    private final String username;
    private String status;

    public Operator(String username, String status) {
        this.username = username;
        this.status = status;
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
                .append("status", status);
        return document;
    }
}
