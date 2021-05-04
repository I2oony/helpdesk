package services;

import entites.*;
import org.junit.Test;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import static com.mongodb.util.Util.toHex;

public class Authentication {
    static CustomLogger logger = new CustomLogger(Authentication.class.getName());

    public static byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String hashPass(char[] pass, byte[] salt) throws GeneralSecurityException {
        logger.info("Generating hash.");

        PBEKeySpec spec = new PBEKeySpec(pass, salt, 1024, 32 * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        logger.info("Hash generating finished.");
        return toHex(salt) + ":" + toHex(hash);
    }

    public boolean checkPass(char[] pass, String hash) throws GeneralSecurityException {
        String[] splitHash = hash.split(":");
        String saltHex = splitHash[0];
        String hashedPass = hashPass(pass, fromHex(saltHex));
        return hash.equals(hashedPass);
    }

    @Test
    public void setPassTest() {
        DBConnect.setDbProperties("localhost", 27017);
        User adminUser = DBConnect.getUser("admin");
        String pass = "adminPass";
        char[] passChars = pass.toCharArray();
        try {
            adminUser.setPassword(hashPass(passChars, createSalt()));
            DBConnect.updateUser(adminUser);
        } catch (GeneralSecurityException e) {
            logger.warning("An error occurred while setting the password");
        }
    }

    @Test
    public void checkPassTest() {
        DBConnect.setDbProperties("localhost", 27017);
        String pass = "adminPass";
        String hash = DBConnect.getUsersPassword("admin");
        char[] passChars = pass.toCharArray();
        try {
            boolean isCorrect = checkPass(passChars, hash);
            logger.info("Is password correct: " + isCorrect);
        } catch (Exception e) {
            logger.warning("An error occurred while checking the password");
        }
    }

}
