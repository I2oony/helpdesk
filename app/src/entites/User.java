package entites;

public class User {
    private final String username;
    private String firstName;
    private String lastName;
    private String email;
    private final Role role;
    private String password;

    User(String username, String email, Role role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    User(String username, String email, Role role, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean setPassword(String newPassword){
        password = newPassword;
        return true;
    }

    enum Role {
        client,
        operator,
        admin
    }
}


