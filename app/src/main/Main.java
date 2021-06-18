package main;

import com.sun.net.httpserver.*;
import services.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class Main {
    static CustomLogger logger;

    public static void main(String[] args) {
        logger = new CustomLogger(Main.class.getName());
        logger.info("Application started");

        FileInputStream configFile;
        Properties property = new Properties();

        try {
            configFile = new FileInputStream("src/resources/config.properties");
            property.load(configFile);
        } catch (IOException e) {
            String defaultConfigFile = "config.properties";
            property.setProperty("dbHost", "localhost");
            property.setProperty("dbPort", "27017");
            property.setProperty("httpHost", "localhost");
            property.setProperty("httpPort", "3000");
            logger.warning("An error occurred while opening config file. The default configuration was set." +
                    "\nThe values of default config will be saved to the " + defaultConfigFile);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(defaultConfigFile);
                property.store(fileOutputStream, "Runtime config");
                fileOutputStream.close();
            } catch (Exception ex) {
                logger.warning("An error while saving the default configuration to file.");
            }
        }

        String dbHost = property.getProperty("dbHost");
        int dbPort = Integer.parseInt(property.getProperty("dbPort"));
        DBConnect.setDbProperties(dbHost, dbPort);

        String host = property.getProperty("httpHost");
        int port = Integer.parseInt(property.getProperty("httpPort"));

        EmailSender.configureSession(property.getProperty("emailHost"),
                property.getProperty("emailPort"),
                property.getProperty("emailAuth"),
                property.getProperty("emailAddress"),
                property.getProperty("emailPassword"));

        if (DBConnect.getUser("admin") == null) {
            User admin = new User("admin", "none", User.Role.admin, "Admin", "Admin");
            String randomPassword = Authentication.generateRandomPass();
            admin.setPassword(randomPassword);
            DBConnect.insertUser(admin);
            logger.info("Created default admin with login: 'admin', password: '" + randomPassword + "'. " +
                    "Please change it once you logged in the system.");
        }

        TimerTask ticketsQueueChecker = new TicketsQueueChecker();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(ticketsQueueChecker, 30000, 30000);

        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            HttpServer server = HttpServer.create(address, 0);
            HttpContext apiContext = server.createContext("/api/", new WebInterfaceHandler());
            apiContext.setAuthenticator(new BearerAuth());
            HttpContext authContext = server.createContext("/api/users/auth", new AuthHandler());
            authContext.setAuthenticator(new BasicAuth());
            server.setExecutor(null);
            server.start();
            logger.info("Server started on " + host + ":" + port);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception occurred while starting the HTTP Server! - IOException", e);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Exception occurred while starting the HTTP Server!", e);
        }
    }
}
