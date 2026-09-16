package org.derleta.nebula.util;

import org.derleta.nebula.account.adapter.out.authservice.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ValidationUtilTest {

    @Test
    public void isValidEmail_withValidEmail_shouldReturnTrue() {
        // Arrange
        String validEmail = "example.email@test.com";

        // Act
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Assert
        assertTrue(result, "The email should be valid.");
    }

    @Test
    public void isValidEmail_withoutAtSymbol_shouldReturnFalse() {
        // Arrange
        String invalidEmail = "exampleemail.com";

        // Act
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result, "The email is invalid because it lacks '@'.");
    }

    @Test
    public void isValidEncryptedPassword_withValidBcryptPassword_shouldReturnTrue() {
        // Arrange
        String validBcryptPassword = "$2a$10$7EqJtq98hPqEX7fNZaFWo.tFp6KjHDT/pr25GJPUyqR8Z2NR.yQH6";

        // Act
        boolean result = ValidationUtil.isValidEncryptedPassword(validBcryptPassword);

        // Assert
        assertTrue(result, "The bcrypt password should be valid.");
    }

    @Test
    public void isValidEncryptedPassword_withNullPassword_shouldReturnFalse() {
        // Arrange
        String nullPassword = null;

        // Act
        boolean result = ValidationUtil.isValidEncryptedPassword(nullPassword);

        // Assert
        assertFalse(result, "The encrypted password should be invalid because it is null.");
    }

    @Test
    public void isValidEncryptedPassword_withEmptyPassword_shouldReturnFalse() {
        // Arrange
        String emptyPassword = "";

        // Act
        boolean result = ValidationUtil.isValidEncryptedPassword(emptyPassword);

        // Assert
        assertFalse(result, "The encrypted password should be invalid because it is empty.");
    }

    @Test
    public void isValidEncryptedPassword_withShortPassword_shouldReturnFalse() {
        // Arrange
        String shortPassword = "$2a$10$abc";

        // Act
        boolean result = ValidationUtil.isValidEncryptedPassword(shortPassword);

        // Assert
        assertFalse(result, "The encrypted password should be invalid because it is too short.");
    }

    @Test
    public void isValidEncryptedPassword_withInvalidCharacters_shouldReturnFalse() {
        // Arrange
        String invalidCharacterPassword = "$2a$10$7EqJtq98hPqEX7fNZaFWo.tFp6KjHDT/pr25GJPUyqR8!!INVALID";

        // Act
        boolean result = ValidationUtil.isValidEncryptedPassword(invalidCharacterPassword);

        // Assert
        assertFalse(result, "The encrypted password should be invalid because it contains illegal characters.");
    }

    @Test
    public void isValidEncryptedPassword_withUnsupportedFormat_shouldReturnFalse() {
        // Arrange
        String unsupportedFormatPassword = "$9y$10$7EqJtq98hPqEX7fNZaFWo.tFp6KjHDT/pr25GJPUyqR8Z2NR.yQH6";

        // Act
        boolean result = ValidationUtil.isValidEncryptedPassword(unsupportedFormatPassword);

        // Assert
        assertFalse(result, "The encrypted password should be invalid because it uses an unsupported hashing format.");
    }

    @Test
    public void isValidEmail_withMultipleAtSymbols_shouldReturnFalse() {
        // Arrange
        String invalidEmail = "example@@test.com";

        // Act
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result, "The email is invalid because it contains multiple '@'.");
    }

    @Test
    public void isValidEmail_withoutDomain_shouldReturnFalse() {
        // Arrange
        String invalidEmail = "exampleemail@";

        // Act
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result, "The email is invalid because it lacks a domain.");
    }

    @Test
    public void isValidEmail_withoutTLD_shouldReturnFalse() {
        // Arrange
        String invalidEmail = "exampleemail@test";

        // Act
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result, "The email is invalid because it lacks a top-level domain.");
    }

    @Test
    public void isValidEmail_withNullEmail_shouldReturnFalse() {
        // Arrange
        String nullEmail = null;

        // Act
        boolean result = ValidationUtil.isValidEmail(nullEmail);

        // Assert
        assertFalse(result, "The email is invalid because it is null.");
    }

    @Test
    public void isValidEmail_withBlankEmail_shouldReturnFalse() {
        // Arrange
        String blankEmail = "   ";

        // Act
        boolean result = ValidationUtil.isValidEmail(blankEmail);

        // Assert
        assertFalse(result, "The email is invalid because it is blank.");
    }

    @Test
    public void isValidEmail_withInvalidCharacters_shouldReturnFalse() {
        // Arrange
        String invalidEmail = "example!email@test.com";

        // Act
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result, "The email is invalid because it contains special characters not allowed in emails.");
    }

    @Test
    public void isValidEmail_withLeadingAndTrailingSpaces_shouldReturnFalse() {
        // Arrange
        String invalidEmail = "  example.email@test.com  ";

        // Act
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result, "The email is invalid because it has leading or trailing spaces.");
    }

    @Test
    public void isValidEmail_withSubdomain_shouldReturnTrue() {
        // Arrange
        String validEmail = "example.email@subdomain.test.com";

        // Act
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Assert
        assertTrue(result, "The email should be valid with a subdomain.");
    }

    @Test
    public void isValidEmail_withNumbers_shouldReturnTrue() {
        // Arrange
        String validEmail = "user123email@test123.com";

        // Act
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Assert
        assertTrue(result, "The email should be valid when it contains numbers.");
    }

}
