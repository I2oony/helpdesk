package tests.services;

import entites.User;
import org.junit.*;

import static org.junit.Assert.*;
import static services.DBConnect.*;

public class DBConnect {
    private static User testUser;

    @BeforeClass
    public static void setupTest() {
        setDbProperties("localhost", 27017);

        testUser = new User(
            "testUser",
            "test@i2oony.com",
            User.Role.valueOf("client"),
            "Test",
            "User"
        );
    }

    @AfterClass
    public static void deleteTestUser() {
        assertTrue(deleteUser(testUser));
    }

    @Test
    public void userTest() {
        assertTrue(insertUser(testUser));
        assertTrue(testUser.setPassword("SomeTestPassword"));
        assertTrue(updateUser(testUser));
        assertTrue(testUser.deletePassword());

        User retrievedUser = getUser(testUser.getUsername());
        assertEquals(testUser.getUsername(), retrievedUser.getUsername());
        assertEquals(testUser.getEmail(), retrievedUser.getEmail());
        assertEquals(testUser.getRole(), retrievedUser.getRole());
        assertEquals(testUser.toDocument(), retrievedUser.toDocument());
    }

    @Ignore
    public void ticketTest() {

    }

    @Ignore
    public void sessionTest() {

    }

}
