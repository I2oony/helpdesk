import com.sun.net.httpserver.*;
import services.CustomLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static Logger logger;
    private static String host = "localhost";
    private static int port = 3000;

    public static void main(String[] args) {
        logger = new CustomLogger(Main.class.getName());
        logger.info("Application started");

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
