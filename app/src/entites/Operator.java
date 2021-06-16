package entites;

import org.bson.Document;

public class Operator {
    private final String username;
    private String state;

    public Operator(String username, String status) {
        this.username = username;
        this.state = status;
    }

    public void changeStatus() {
        if (state.equals("offline")) {
            state = "online";
        } else {
            state = "offline";
        }
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("username", username)
                .append("state", state);
        return document;
    }
}
