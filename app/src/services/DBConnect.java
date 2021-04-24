package services;

import java.util.logging.Logger;

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
    private static String host;
    private static int port;

    public static void setDbProperties(String host, int port) {
        DBConnect.host = host;
        DBConnect.port = port;
    }

    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> ticketsCollection;

    public DBConnect() {
        logger = new CustomLogger(DBConnect.class.getName());
        Logger.getLogger("org.mongodb.driver").setParent(logger);
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase("helpdesk");
        usersCollection = database.getCollection("users");
        ticketsCollection = database.getCollection("tickets");
    }

    public boolean insertUser(User user) {
        try {
            Document document = Document.parse(user.toJson());
            usersCollection.insertOne(document);
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while adding a user - " + e.getMessage());
            return false;
        }
    }

    public User getUser(String username) {
        Document document = usersCollection.find(eq("username", username)).first();
        return new User(
                document.getString("username"),
                document.getString("email"),
                User.Role.valueOf(document.getString("role")),
                document.getString("firstName"),
                document.getString("lastName")
        );
    }

    public boolean updateUser(User user) {
        try {
            Document document = Document.parse(user.toJson());
            usersCollection.replaceOne(eq("username", user.getUsername()), document);
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while updating a user - " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(User user) throws MongoException {
        try {
            usersCollection.deleteOne(eq("username", user.getUsername()));
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while deleting a user - " + e.getMessage());
            return false;
        }
    }

    @Test
    public void userTest() {
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
