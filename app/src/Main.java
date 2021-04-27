import com.sun.net.httpserver.*;
import services.Authentication;
import services.CustomLogger;
import services.DBConnect;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static Logger logger;

    public static void main(String[] args) {
        logger = new CustomLogger(Main.class.getName());
        logger.info("Application started");

        FileInputStream configFile;
        Properties property = new Properties();
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        Date currentDate = new Date();
        String defaultConfigFile = "config_" + currentDate.getTime() + ".properties";
        property.setProperty("dbHost", "localhost");
        property.setProperty("dbPort", "27017");
        property.setProperty("httpHost", "localhost");
        property.setProperty("httpPort", "3000");
        property.setProperty("globalSalt", String.valueOf(salt));

        try {
            configFile = new FileInputStream("src/resources/config.properties");
            property.load(configFile);
        } catch (IOException e) {
            logger.warning("An error occurred while opening config file occurred. The default configuration was set." +
                    "\nThe values of default config will be saved to the " + defaultConfigFile);
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(defaultConfigFile);
            property.store(fileOutputStream, "Runtime config");
        } catch (Exception e) {
            logger.warning("An error while saving the default configuration to file." +
                    "\nHere is the salt: " + property.getProperty("globalSalt"));
        }

        String dbHost = property.getProperty("dbHost");
        int dbPort = Integer.parseInt(property.getProperty("dbPort"));
        DBConnect.setDbProperties(dbHost, dbPort);

        Authentication.setSalt(property.getProperty("globalSalt"));

        String host = property.getProperty("httpHost");
        int port = Integer.parseInt(property.getProperty("httpPort"));

        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            HttpServer server = HttpServer.create(address, 0);
            server.createContext("/api/", new WebInterfaceHandler());
            server.start();
            logger.info("Server started on " + port);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception occurred while starting the HTTP Server! - IOException", e);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Exception occurred while starting the HTTP Server!", e);
        }
    }
}
