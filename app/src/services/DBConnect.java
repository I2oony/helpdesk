package services;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import entities.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;

public class DBConnect {
    static CustomLogger logger;

    private static MongoCollection<Document> usersCollection;
    private static MongoCollection<Document> ticketsCollection;
    private static MongoCollection<Document> sessionsCollection;
    private static MongoCollection<Document> operatorsCollection;

    public static void setDbProperties(String host, int port) {
        logger = new CustomLogger(DBConnect.class.getName());
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase("helpdesk");
        usersCollection = database.getCollection("users");
        ticketsCollection = database.getCollection("tickets");
        sessionsCollection = database.getCollection("sessions");
        operatorsCollection = database.getCollection("operators");
    }

    public static boolean insertUser(User user) {
        logger.info("Inserting the new user.");
        try {
            Document document = user.toDocument();
            usersCollection.insertOne(document);
            logger.info("User successfully inserted.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while adding a user - " + e.getMessage());
            return false;
        }
    }

    public static User getUser(String username) {
        try {
            logger.info("Fetching the user with username: " + username);
            Document document = usersCollection.find(eq("username", username)).first();
            return new User(
                    document.getString("username"),
                    document.getString("email"),
                    User.Role.valueOf(document.getString("role")),
                    document.getString("firstName"),
                    document.getString("lastName")
            );
        } catch (Exception e) {
            logger.info("No such user exist: " + username);
            return null;
        }
    }

    public static User[] getUsersList() {
        int length = (int) usersCollection.count();
        logger.info("Fetching the users list. Total users: " + length);
        User[] users = new User[length];
        int i = 0;
        try (MongoCursor<Document> cursor = usersCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                users[i] = new User(
                        document.getString("username"),
                        document.getString("email"),
                        User.Role.valueOf(document.getString("role")),
                        document.getString("firstName"),
                        document.getString("lastName")
                );
                i++;
            }
        }
        return users;
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
            Document document = user.toDocument();
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

    public static int getLastTicketNumber() {
        try {
            int num = ticketsCollection.find().sort(orderBy(descending("ticketId")))
                    .first().getInteger("ticketId");
            return num;
        } catch (Exception e) {
            return 0;
        }
    }

    public static Ticket[] getTicketsList(String username) {
        logger.info("Fetching the tickets list.");
        User requester = getUser(username);
        Ticket[] tickets;
        String[] searchQuery = new String[2];
        switch (requester.getRole()) {
            case admin:
                searchQuery = null;
                break;
            case operator:
                searchQuery[0] = "operator";
                searchQuery[1] = username;
                break;
            case client:
                searchQuery[0] = "requester";
                searchQuery[1] = username;
                break;
            default:
                tickets = null;
                logger.warning("Error while getting the tickets list requester.");
                break;
        }

        int i = 0;
        try {
            MongoCursor<Document> cursor = ticketsCollection
                    .find(searchQuery!=null?eq(searchQuery[0], searchQuery[1]):gt("ticketId", -1))
                    .projection(fields(include("ticketId", "title", "requester", "operator", "state", "messages", "priority"),
                            slice("messages", -1)))
                    .sort(new BasicDBObject("ticketId", -1))
                    .iterator();
            int length = (int) ticketsCollection.count(searchQuery!=null?eq(searchQuery[0], searchQuery[1]):gt("ticketId", -1));
            tickets = new Ticket[length];
            return getTickets(tickets, i, cursor);
        } catch (Exception e) {
            logger.warning("An error occured while fetching the tickets list: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Ticket[] getTicketsInQueue() {
        try {
            int length = (int) ticketsCollection.count(ne("priority", 0));
            Ticket[] tickets = new Ticket[length];
            int i = 0;
            MongoCursor<Document> cursor = ticketsCollection.find(ne("priority", 0)).sort(orderBy(descending("priority"))).iterator();
            return getTickets(tickets, i, cursor);
        } catch (Exception e) {
            logger.warning("An error occured while fetching the tickets list: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Ticket[] getTickets(Ticket[] tickets, int i, MongoCursor<Document> cursor) {
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Message[] messages;
            if (document.get("messages", ArrayList.class).size()!=0) {
                Document messageDoc = (Document) document.get("messages", ArrayList.class).get(0);
                messages = new Message[1];
                messages[0] = new Message(
                        messageDoc.getInteger("id"),
                        messageDoc.getString("from"),
                        messageDoc.getString("text"),
                        messageDoc.getDate("date")
                );
            } else {
                messages = null;
            }
            tickets[i] = new Ticket(
                    document.getInteger("ticketId"),
                    document.getString("title"),
                    document.getString("requester"),
                    document.get("operator", ArrayList.class),
                    document.getString("state"),
                    messages,
                    document.getInteger("priority")
            );
            i++;
        }
        return tickets;
    }

    public static Ticket getTicket(int ticketId) {
        try {
            logger.info("Fetching the ticket with ticketId: " + ticketId);
            Document document = ticketsCollection.find(eq("ticketId", ticketId)).first();
            ArrayList messagesArray = document.get("messages", ArrayList.class);
            int messagesLen = messagesArray.size();
            Message[] messages = new Message[messagesLen];
            for (int i=0; i<messagesLen; i++) {
                Document messageDoc = (Document) messagesArray.get(i);
                messages[i] = new Message(
                        messageDoc.getInteger("id"),
                        messageDoc.getString("from"),
                        messageDoc.getString("text"),
                        messageDoc.getDate("date")
                );
            }
            return new Ticket(
                    document.getInteger("ticketId"),
                    document.getString("title"),
                    document.getString("requester"),
                    document.get("operator", ArrayList.class),
                    document.getString("state"),
                    messages,
                    document.getInteger("priority")
            );
        } catch (Exception e) {
            logger.info("No such ticket exist: " + ticketId);
            return null;
        }
    }

    public static boolean insertTicket(Ticket ticket) {
        try {
            ticket.setId(getLastTicketNumber() + 1);
            ticket.setState("created");
            Document document = ticket.toDocument();
            ticketsCollection.insertOne(document);
            logger.info("Ticket successfully inserted.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while inserting a ticket - " + e.getMessage());
            return false;
        }
    }

    public static boolean updateTicket(Ticket ticket) {
        int ticketId = ticket.getId();
        logger.info("Updating the ticket with ticketId: " + ticketId);
        try {
            Document document = ticket.toDocument();
            ticketsCollection.replaceOne(eq("ticketId", ticketId), document);
            logger.info("Ticket successfully updated.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while updating a ticket - " + e.getMessage());
            return false;
        }
    }

    public static boolean addSession(Session session) {
        try {
            Document document = new Document();
            document.append("token", session.getToken())
                    .append("username", session.getUsername())
                    .append("role", session.getRoleString())
                    .append("validUntil", session.getValidUntil());
            sessionsCollection.insertOne(document);
            logger.info("Session successfully added.");
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while adding a session - " + e.getMessage());
            return false;
        }
    }

    public static Session getSession(String token) throws Exception {
        logger.info("Fetching the session.");
        Document document = sessionsCollection.find(eq("token", token)).first();
        if (document!=null) {
            return new Session(
                    token,
                    document.getString("username"),
                    User.Role.valueOf(document.getString("role")),
                    document.getDate("validUntil")
            );
        } else {
            throw new Exception("Session either expired or doesn't exists.");
        }
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

    public static ArrayList<Operator> getOnlineOperatorsList() {
        logger.info("Getting operators with online status.");
        MongoCursor<Document> cursor = operatorsCollection.find(eq("status", "online")).iterator();
        int length = (int) operatorsCollection.count(eq("status", "online"));
        ArrayList<Operator> operators = new ArrayList<>();
        int i = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            operators.add(new Operator(
                    document.getString("username"),
                    document.getString("status"),
                    document.get("tickets", ArrayList.class)
            ));
            i++;
        }
        return operators;
    }

    public static Operator getOperatorStatus(String username) {
        logger.info("Getting current status for the operator with username: " + username);
        Document document = operatorsCollection.find(eq("username", username)).first();
        Operator operator;
        if (document != null) {
            operator = new Operator(
                    document.getString("username"),
                    document.getString("status"),
                    document.get("tickets", ArrayList.class)
            );
        } else {
            operator = new Operator(username, "offline", new ArrayList<>());
            operatorsCollection.insertOne(operator.toDocument());
        }
        return operator;
    }

    public static boolean updateOperator(Operator operator) {
        try {
            logger.info("Updating the operator with username: " + operator.getUsername());
            Document document = operator.toDocument();
            operatorsCollection.replaceOne(eq("username", operator.getUsername()), document);
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while updating the operator" + e.getMessage());
            return false;
        }
    }
}
