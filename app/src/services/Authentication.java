package services;

import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class Authentication {

    public String hashPass(char[] pass) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(pass, salt, 1024);

        return "hash";
    }

    public boolean checkPass(String input, String hash) {
        // TODO Calculating and checking the hash for the auth.
        return true;
    }

}
