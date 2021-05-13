package main;

import com.google.gson.*;
import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import services.CustomLogger;
import entites.*;
import services.DBConnect;

public class WebInterfaceHandler implements HttpHandler {
    static CustomLogger logger;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(WebInterfaceHandler.class.getName());

        String method = httpExchange.getRequestMethod();
        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();
        String path = httpExchange.getRequestURI().getPath();
        logger.info("Received HTTP Request with method " + method +
                ". Endpoint: " + path);

        switch (path) {
            case "/api/users":
                User user = DBConnect.getUser(httpExchange.getPrincipal().getUsername());

                Gson responseBody = new GsonBuilder().setPrettyPrinting().create();
                String responseString = responseBody.toJson(user);

                responseHeaders.add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseString.length());

                responseStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                responseStream.flush();
                break;
            case "/api/tickets":

                break;
            default:
                break;
        }
        httpExchange.close();
    }
}