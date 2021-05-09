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
        logger.info("Received HTTP Request with method " + method +
                ". Endpoint: " + httpExchange.getRequestURI().getPath());

        InputStream requestBody = httpExchange.getRequestBody();

        User testUser = DBConnect.getUser("admin");

        Gson responseBody = new GsonBuilder().setPrettyPrinting().create();
        String responseString = responseBody.toJson(testUser);

        switch (method) {
            case "GET":
                responseHeaders.add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseString.length());

                responseStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                responseStream.flush();
                break;
            default:
                break;
        }
        httpExchange.close();
    }
}