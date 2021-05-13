package entites;

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
        Date now = new Date();
        if (now.before(validUntil)) {
            return true;
        } else {
            DBConnect.deleteSession(token);
            return false;
        }
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRoleString() {
        return role.toString();
    }

    public Date getValidUntil() {
        return validUntil;
    }

}
