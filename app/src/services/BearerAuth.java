package services;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import entites.Session;

public class BearerAuth extends Authenticator {
    static CustomLogger logger = new CustomLogger(BearerAuth.class.getName());

    @Override
    public Result authenticate(HttpExchange httpExchange) {
        try {
            logger.info("Checking bearer token.");

            String cookies = httpExchange.getRequestHeaders().get("Cookie").get(0);
            String token = cookies.split("=")[1];

            Session session = DBConnect.getSession(token);

            if (session.checkSession()) {
                return new Success(new HttpPrincipal(session.getUsername(), "auth"));
            } else {
                return new Failure(401);
            }
        } catch (Exception e) {
            logger.warning("Can't check the token! " + e.getMessage());
            return new Failure(401);
        }
    }
}
