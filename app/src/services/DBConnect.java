package services;

import java.net.ServerSocket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import entites.Session;
import org.junit.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import entites.User;

import static com.mongodb.client.model.Filters.*;

public class DBConnect {
    static CustomLogger logger;

    private static MongoCollection<Document> usersCollection;
    private static MongoCollection<Document> ticketsCollection;
    private static MongoCollection<Document> sessionsCollection;

    public static void setDbProperties(String host, int port) {
        logger = new CustomLogger(DBConnect.class.getName());
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase("helpdesk");
        usersCollection = database.getCollection("users");
        ticketsCollection = database.getCollection("tickets");
        sessionsCollection = database.getCollection("sessions");
    }

    public static boolean insertUser(User user) {
        logger.info("Inserting the new user.");
        try {
            Document document = Document.parse(user.toJson());
            usersCollection.insertOne(document);
            logger.info("User successfully inserted.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while adding a user - " + e.getMessage());
            return false;
        }
    }

    public static User getUser(String username) {
        logger.info("Fetching the user with username: " + username);
        Document document = usersCollection.find(eq("username", username)).first();
        return new User(
                document.getString("username"),
                document.getString("email"),
                User.Role.valueOf(document.getString("role")),
                document.getString("firstName"),
                document.getString("lastName")
        );
    }

    public static String getUsersPassword(String username) {
        logger.info("Fetching password of user: " + username);
        Document document = usersCollection.find(eq("username", username)).first();
        return document.getString("password");
    }

    public static boolean updateUser(User user) {
        String username = user.getUsername();
        logger.info("Updating the user with username: " + username);
        try {
            Document document = Document.parse(user.toJson());
            usersCollection.replaceOne(eq("username", username), document);
            logger.info("User successfully updated.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while updating a user - " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteUser(User user) throws MongoException {
        String username = user.getUsername();
        logger.info("Deleting the user with username: " + username);
        try {
            usersCollection.deleteOne(eq("username", username));
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while deleting a user - " + e.getMessage());
            return false;
        }
    }

    public static boolean addSession(Session session) {
        try {
            Document document = Document.parse(session.toJson());
            sessionsCollection.insertOne(document);
            logger.info("Session successfully added.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while adding a session - " + e.getMessage());
            return false;
        }
    }

    public static Date getSessionValidDate(String token) {
        logger.info("Fetching session.");
        Document document = sessionsCollection.find(eq("token", token)).first();
        return document.getDate("validUntil");
    }

    public static boolean deleteSession(String token) {
        logger.info("Deleting the session.");
        try {
            sessionsCollection.deleteOne(eq("token", token));
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while deleting the session - " + e.getMessage());
            return false;
        }
    }

    @Test
    public void userTest() {
        setDbProperties("localhost", 27017);

        User testUser = new User(
                "testUser",
                "test@user.com",
                User.Role.valueOf("client"),
                "Test",
                "User"
        );

        boolean isInserted = insertUser(testUser);
        boolean isPasswordSet = testUser.setPassword("SomeTestPassword2");
        boolean isUpdated = updateUser(testUser);
        User retrievedUser = getUser(testUser.getUsername());
        boolean isDeleted = deleteUser(testUser);

        logger.info("UnitTest for " + DBConnect.class.getName() + " module, 'user' part completed." +
                "\nResults:" +
                        "\n- isInserted: " + isInserted +
                        "\n- isPasswordSet: " + isPasswordSet +
                        "\n- isUpdated: " + isUpdated +
                        "\n- isDeleted: " + isDeleted +
                        "\nRetrieved user:\n" + retrievedUser.toJson()
                );
    }

    @Test
    public void ticketTest() {
        logger.info("UnitTest for " + DBConnect.class.getName() + " module, 'ticket' part completed.");
    }

}
