package services;

import static services.Authentication.checkPass;
import static services.DBConnect.getUsersPassword;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class BasicAuth extends Authenticator {
    static CustomLogger logger = new CustomLogger(BasicAuth.class.getName());

    @Override
    public Result authenticate(HttpExchange httpExchange) {
        try {
            logger.info("Checking credentials.");
            Headers headers =  httpExchange.getRequestHeaders();
            String authHeader = headers.get("Authorization").get(0).split(" ")[1];
            byte[] decodedBytes = Base64.decode(authHeader);

            String[] splitString = new String(decodedBytes).split(":");
            String username = splitString[0];
            String password = splitString[1];

            String passwordFromDb = getUsersPassword(username);

            if(checkPass(password.toCharArray(), passwordFromDb)) {
                return new Success(new HttpPrincipal(username, "auth"));
            } else {
                return new Failure(401);
            }
        } catch (Exception e) {
            logger.warning("Can't check the credentials! " + e);
            return new Failure(401);
        }
    }
}
