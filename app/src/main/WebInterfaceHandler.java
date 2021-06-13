package main;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import entites.structures.ChangePassword;
import services.Authentication;
import services.CustomLogger;
import entites.*;
import services.DBConnect;
import services.EmailSender;

public class WebInterfaceHandler implements HttpHandler {
    static CustomLogger logger;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(WebInterfaceHandler.class.getName());

        InputStream requestStream = httpExchange.getRequestBody();
        JsonReader requestReader = new JsonReader(new InputStreamReader(requestStream));
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String remoteAddr = httpExchange.getRequestHeaders()
                                        .getFirst("X-Real-IP");
        logger.info("Received HTTP Request with method " + method +
                ". Endpoint: " + path + ". From: " + remoteAddr);

        String params = httpExchange.getRequestURI().getQuery();

        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();
        Gson responseBody = new GsonBuilder().setPrettyPrinting().create();
        int responseCode = 200;
        StringBuilder responseString = new StringBuilder();

        User user;

        switch (path) {
            case "/api/users":
                user = DBConnect.getUser(httpExchange.getPrincipal().getUsername());
                responseString.append(responseBody.toJson(user));
                break;
            case "/api/users/list":
                try {
                    responseString.append(responseBody.toJson(DBConnect.getUsersList()));
                } catch (Exception e) {
                    logger.warning("Something went wrong while fetching the users list. See the full error below: " + e);
                }
                break;
            case "/api/users/logout":
                if (method.equals("DELETE")) {
                    String cookies = httpExchange.getRequestHeaders().get("Cookie").get(0);
                    String token = cookies.split("=")[1];
                    DBConnect.deleteSession(token);
                }
                break;
            case "/api/users/changeInfo":
                if (method.equals("PATCH")) {
                    user = DBConnect.getUser(httpExchange.getPrincipal().getUsername());
                    try {
                        ChangePassword passwordData = new Gson().fromJson(requestReader, ChangePassword.class);
                        if (Authentication.checkPass(passwordData.oldPassword.toCharArray(), DBConnect.getUsersPassword(user.getUsername()))) {
                            user.setPassword(Authentication.hashPass(passwordData.newPassword.toCharArray(), Authentication.createSalt()));
                            DBConnect.updateUser(user);
                            logger.info("Password for the user " + user.getUsername() + " was changed.");
                        } else {
                            responseCode = 401;
                        }
                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                }
                break;
            case "/api/users/create":
                User newUser = new Gson().fromJson(requestReader, User.class);
                if (DBConnect.getUser(newUser.getUsername()) == null) {
                    try {
                        String randomPassword = Authentication.generateRandomPass();
                        newUser.setPassword(Authentication.hashPass(
                                randomPassword.toCharArray(),
                                Authentication.createSalt()));

                        StringBuilder emailText = new StringBuilder();
                        emailText.append("Данные для входа на i2oony.com\n");
                        emailText.append("Логин: " + newUser.getUsername() + "\n");
                        emailText.append("Пароль: " + randomPassword);

                        EmailSender.sendEmail(
                                newUser.getEmail(),
                                "Данные для входа",
                                emailText.toString());

                    } catch (Exception e) {
                        logger.warning("Can't set the random password for " + newUser.getUsername() + ".");
                    }
                    DBConnect.insertUser(newUser);

                    responseCode = 201;
                } else {
                    responseCode = 403;
                }
                break;
            case "/api/tickets":
                try {
                    String username = httpExchange.getPrincipal().getUsername();
                    Ticket[] tickets = DBConnect.getTicketsList(username);
                    responseString.append(responseBody.toJson(tickets));
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
                break;
            case "/api/ticket":
                String ticketIdStr = params.split("=")[1];
                int ticketId = Integer.parseInt(ticketIdStr);
                if (method.equals("GET")) {
                    try {
                        Ticket ticket = DBConnect.getTicket(ticketId);
                        responseString.append(responseBody.toJson(ticket));
                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                } else if (method.equals("POST")) {
                    try {
                        Message message = new Gson().fromJson(requestReader, Message.class);
                        Ticket ticket = DBConnect.getTicket(ticketId);
                        if (ticket != null) {
                            ticket.addMessage(message);
                            DBConnect.updateTicket(ticket);
                            responseString.append(responseBody.toJson(ticket));
                        } else {
                            responseCode = 400;
                            throw new Exception("An error occurred while adding the new message.");
                        }
                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                }
                break;
            default:
                responseCode = 400;
                break;
        }

        responseHeaders.add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(responseCode, responseString.length());
        responseStream.write(responseString.toString().getBytes(StandardCharsets.UTF_8));
        responseStream.flush();
        httpExchange.close();
    }
}