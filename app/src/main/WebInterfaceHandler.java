package main;

import com.google.gson.*;
import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import services.CustomLogger;
import entites.*;
import services.DBConnect;

public class WebInterfaceHandler implements HttpHandler {
    static CustomLogger logger;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(WebInterfaceHandler.class.getName());

        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();

        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String remoteAddr = httpExchange.getRequestHeaders()
                                        .getFirst("X-Real-IP");
        logger.info("Received HTTP Request with method " + method +
                ". Endpoint: " + path + ". From: " + remoteAddr);

        String params = httpExchange.getRequestURI().getQuery();

        Gson responseBody = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder responseString = new StringBuilder();

        switch (path) {
            case "/api/users":
                User user = DBConnect.getUser(httpExchange.getPrincipal().getUsername());
                responseString.append(responseBody.toJson(user));
                break;
            case "/api/tickets":
                String[] paramsArr = params.split("&");
                logger.info(Arrays.toString(paramsArr));
                break;
            default:
                break;
        }

        responseHeaders.add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, responseString.length());
        responseStream.write(responseString.toString().getBytes(StandardCharsets.UTF_8));
        responseStream.flush();
        httpExchange.close();
    }
}