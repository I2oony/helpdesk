import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.logging.Logger;

public class WebInterfaceHandler implements HttpHandler {
    static Logger logger;
    static String htdocsPath = "C:\\Development\\Helpdesk\\frontend\\";
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(WebInterfaceHandler.class.getName());

        String method = httpExchange.getRequestMethod();
        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();
        logger.info("Received HTTP Request with method " + method);

        // Ticket someTicket = new Ticket();

        // Gson responseBody = new GsonBuilder().setPrettyPrinting().create();
        // String responseString = responseBody.toJson(someTicket);

        Path fileName = FileSystems.getDefault().getPath(htdocsPath, "index.html");
        byte[] responseString = Files.readAllBytes(fileName);

        switch (method) {
            case "GET":
                responseHeaders.add("Content-Type", "text/html; charset=utf-8");
                httpExchange.sendResponseHeaders(200, responseString.length);

                responseStream.write(responseString);
                responseStream.flush();
                responseStream.close();
                break;
            default:
                break;
        }
    }
}