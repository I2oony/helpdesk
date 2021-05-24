package main;

import com.sun.net.httpserver.*;
import services.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class Main {
    static CustomLogger logger;

    public static void main(String[] args) {
        logger = new CustomLogger(Main.class.getName());
        logger.info("Application started");

        FileInputStream configFile;
        Properties property = new Properties();

        Date currentDate = new Date();
        String defaultConfigFile = "config_" + currentDate.getTime() + ".properties";
        property.setProperty("dbHost", "localhost");
        property.setProperty("dbPort", "27017");
        property.setProperty("httpHost", "localhost");
        property.setProperty("httpPort", "3000");

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
            fileOutputStream.close();
        } catch (Exception e) {
            logger.warning("An error while saving the default configuration to file.");
        }

        String dbHost = property.getProperty("dbHost");
        int dbPort = Integer.parseInt(property.getProperty("dbPort"));
        DBConnect.setDbProperties(dbHost, dbPort);

        String host = property.getProperty("httpHost");
        int port = Integer.parseInt(property.getProperty("httpPort"));

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
