package entites;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import services.DBConnect;

import java.util.Date;

public class Session {
    private String token;
    private String username;
    private User.Role role;
    private Date validUntil;

    public Session(String token, String username, User.Role role, Date validUntil){
        this.token = token;
        this.username = username;
        this.role = role;
        this.validUntil = validUntil;
    }

    public boolean checkSession() {
        Date validDate = DBConnect.getSessionValidDate(token);
        Date now = new Date();
        if (now.before(validDate)) {
            return true;
        } else {
            DBConnect.deleteSession(token);
            return false;
        }
    }

    public String getToken() {
        return token;
    }

    public String toJson() {
        Gson json = new GsonBuilder().setPrettyPrinting().create();
        return json.toJson(this);
    }
}
