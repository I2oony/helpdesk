package tests.services;

import entites.User;
import org.junit.*;
import services.DBConnect;

import java.security.GeneralSecurityException;

import static org.junit.Assert.*;
import static services.Authentication.*;
import static services.DBConnect.*;

public class Authentication {
    private static User adminUser;
    private static String adminPass;

    @BeforeClass
    public static void setupTest() {
        setDbProperties("localhost", 27017);
        adminUser = services.DBConnect.getUser("admin");
        adminPass = "adminPass";
    }

    @Test
    public void setPassTest() {
        assertTrue("Can't set password.", adminUser.setPassword("adminPass"));
        assertTrue("Can't update user.", updateUser(adminUser));
    }

    @Test
    public void checkPassTest() {
        String hash = DBConnect.getUsersPassword("admin");
        char[] passChars = adminPass.toCharArray();
        try {
            assertTrue("Password is incorrect.", checkPass(passChars, hash));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
