package main;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import entites.structures.ChangePassword;
import services.Authentication;
import services.CustomLogger;
import entites.*;
import services.DBConnect;

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
                // TODO Create user
            case "/api/tickets":
                String[] paramsArr = params.split("&");
                logger.info(Arrays.toString(paramsArr));
                break;
            default:
                break;
        }

        responseHeaders.add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(responseCode, responseString.length());
        responseStream.write(responseString.toString().getBytes(StandardCharsets.UTF_8));
        responseStream.flush();
        httpExchange.close();
    }
}