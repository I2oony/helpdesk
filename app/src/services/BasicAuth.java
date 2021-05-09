package services;

import static services.Authentication.checkPass;
import static services.DBConnect.getUsersPassword;

import com.sun.net.httpserver.BasicAuthenticator;

public class BasicAuth extends BasicAuthenticator {
    static CustomLogger logger = new CustomLogger(BasicAuth.class.getName());

    public BasicAuth(String s) {
        super(s);
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        logger.info("Check credentials.");
        try {
            String passwordFromDb = getUsersPassword(username);
            logger.info("Checking credentials...");
            return checkPass(password.toCharArray(), passwordFromDb);
        } catch (Exception e) {
            logger.warning("Can't check the credentials!" + e.getMessage());
            return false;
        }
    }
}
