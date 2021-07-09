package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import entities.Session;
import entities.User;
import services.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import static services.Authentication.generateNewToken;

public class AuthHandler implements HttpHandler {
    static CustomLogger logger;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(AuthHandler.class.getName());

        String method = httpExchange.getRequestMethod();

        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();

        logger.info("Received HTTP Request with method " + method +
                ". Endpoint: " + httpExchange.getRequestURI().getPath());

        if (method.equals("GET")) {
            User user = DBConnect.getUser(httpExchange.getPrincipal().getUsername());
            String token = generateNewToken();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 12);
            Session session = new Session(token, user.getUsername(), user.getRole(), calendar.getTime());

            DBConnect.addSession(session);

            Gson responseBody = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            String responseString = responseBody.toJson(token);

            responseHeaders.add("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, responseString.length());

            responseStream.write(responseString.getBytes(StandardCharsets.UTF_8));
            responseStream.flush();
        } else {
            httpExchange.sendResponseHeaders(401, 0);
        }
        httpExchange.close();
    }
}
