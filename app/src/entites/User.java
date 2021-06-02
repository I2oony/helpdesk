package entites;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User {
    private final String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
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
        password = newPassword;
        return true;
    }

    public String toJson() {
        Gson json = new GsonBuilder().setPrettyPrinting().create();
        return json.toJson(this);
    }

    public enum Role {
        client,
        operator,
        admin
    }
}


