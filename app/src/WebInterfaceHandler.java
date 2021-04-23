import com.google.gson.*;
import com.sun.net.httpserver.*;
import services.CustomLogger;

import entites.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

public class WebInterfaceHandler implements HttpHandler {
    static Logger logger;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(WebInterfaceHandler.class.getName());

        String method = httpExchange.getRequestMethod();
        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();
        logger.info("Received HTTP Request with method " + method);

        Message someMessage = new Message(0, "client", "Hello darkness my old friend", new Date());
        Ticket someTicket = new Ticket("Some Title", "client", someMessage);

        Gson responseBody = new GsonBuilder().setPrettyPrinting().create();
        String responseString = responseBody.toJson(someTicket);

        switch (method) {
            case "GET":
                responseHeaders.add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseString.length());

                responseStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                responseStream.flush();
                responseStream.close();
                break;
            default:
                break;
        }
    }
}