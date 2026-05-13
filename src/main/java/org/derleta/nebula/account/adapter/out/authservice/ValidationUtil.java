package org.derleta.nebula.account.adapter.out.authservice;

/**
 * Utility class for common validation operations.
 * This class provides static methods for validating data structures such as email addresses,
 * ensuring that inputs conform to specific formats or requirements.
 * <p>
 * The class cannot be instantiated as it only contains static methods for utility purposes.
 */
public class ValidationUtil {

    /**
     * Validates whether a given email address conforms to the standard email format.
     *
     * @param email the email address to validate; may be null or empty.
     * @return true if the email address is valid and matches the required format, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates whether a given string is a valid bcrypt-encrypted password.
     *
     * @param encryptedPassword the encrypted password to validate; may be null or empty.
     * @return true if the password matches the bcrypt format, false otherwise.
     */
    public static boolean isValidEncryptedPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            return false;
        }
        String bcryptRegex = "^\\$2[aby]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$";
        return encryptedPassword.matches(bcryptRegex);
    }

}
