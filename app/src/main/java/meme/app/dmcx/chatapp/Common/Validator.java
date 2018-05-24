package meme.app.dmcx.chatapp.Common;

public class Validator {

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isValidPassword(String password, String confirmPassword, int length) {
        return password.equals(confirmPassword) && password.length() >= length;
    }

    public static boolean isValidPassword(String password, int length) {
        return password.length() >= length;
    }

}
