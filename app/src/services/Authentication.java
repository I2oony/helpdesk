package services;

public class Authentication {
    private static String globalSalt;

    public static void setSalt(String salt) {
        globalSalt = salt;
    }

    public String hashPass(String pass) {
        // TODO Calculating and store the hash of the password.
        return "hash";
    }

    public boolean checkPass(String input, String hash) {
        // TODO Calculating and checking the hash for the auth.
        return true;
    }

}
