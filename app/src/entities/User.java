package entities;

import org.bson.Document;
import services.Authentication;

import java.security.GeneralSecurityException;

public class User {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Role role;
    private String password;

    public User(String username, String email, Role role, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean setPassword(String newPassword) {
        try {
            password = Authentication.hashPass(newPassword.toCharArray(), Authentication.createSalt());
            return true;
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    public boolean deletePassword() {
        password = null;
        return true;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("username", username)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("role", role.toString())
                .append("password", password);
        return document;
    }

    public enum Role {
        client,
        operator,
        admin
    }
}


