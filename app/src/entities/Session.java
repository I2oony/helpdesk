package entities;

import services.DBConnect;

import java.util.Date;

public class Session {
    private final String token;
    private final String username;
    private final User.Role role;
    private final Date validUntil;

    public Session(String token, String username, User.Role role, Date validUntil) {
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
