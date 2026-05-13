package org.derleta.nebula.user.adapter.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class UserDataJpaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserDataJpaRepository userRepository;

    @Test
    @Transactional
    void updateUserUpdatedAt_validUserId_shouldUpdateTimestamp() {
        long userId = 15L;
        Instant beforeUpdate = userRepository.findById(userId).orElseThrow().getUpdatedAt();

        userRepository.updateUserUpdatedAt(userId);
        userRepository.flush();
        entityManager.clear();
        UserEntity updatedUser = userRepository.findById(userId).orElseThrow();

        assertNotNull(updatedUser.getUpdatedAt());
        assertTrue(updatedUser.getUpdatedAt().isAfter(beforeUpdate),
                "Updated timestamp should be after the original timestamp");
    }

    @Test
    @Transactional
    void updateUserDetails_validData_shouldUpdateUserDetails() {
        long userId = 1000L;
        Instant beforeUpdate = userRepository.findById(userId).orElseThrow().getUpdatedAt();
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        String newFirstName = "John_" + currentTimestamp;
        String newLastName = "Doe_" + currentTimestamp;
        Date birthDate = Date.valueOf(LocalDate.of(1985, 5, 5));
        int nationalityId = 1;
        int genderId = 1;

        userRepository.updateUserDetails(userId, newFirstName, newLastName, birthDate, nationalityId, genderId);
        userRepository.flush();
        entityManager.clear();
        UserEntity updatedUser = userRepository.findById(userId).orElseThrow();

        assertNotNull(updatedUser);
        assertEquals(newFirstName, updatedUser.getFirstName());
        assertEquals(newLastName, updatedUser.getLastName());
        assertEquals(birthDate, updatedUser.getBirthDate());
        assertTrue(updatedUser.getUpdatedAt().isAfter(beforeUpdate),
                "Updated timestamp should be after the original timestamp");
    }

    @Test
    @Transactional
    void updateUserDetails_invalidNationalityId_shouldThrowDataIntegrityViolationException() {
        long userId = 1000L;
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        Date birthDate = Date.valueOf(LocalDate.of(1985, 5, 5));
        int invalidNationalityId = 999;

        assertThrows(
                DataIntegrityViolationException.class,
                () -> {
                    userRepository.updateUserDetails(userId, "John_" + currentTimestamp,
                            "Doe_" + currentTimestamp, birthDate, invalidNationalityId, 1);
                    userRepository.flush();
                },
                "Expected DataIntegrityViolationException due to invalid nationality_id"
        );
    }

    @Test
    @Transactional
    void updateUserDetails_invalidUserId_shouldThrowNoSuchElementException() {
        long invalidUserId = 1002L;
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        Date birthDate = Date.valueOf(LocalDate.of(1985, 5, 5));

        assertThrows(
                NoSuchElementException.class,
                () -> {
                    userRepository.updateUserDetails(invalidUserId, "John_" + currentTimestamp,
                            "Doe_" + currentTimestamp, birthDate, 1, 1);
                    userRepository.flush();
                    userRepository.findById(invalidUserId).orElseThrow();
                },
                "Expected NoSuchElementException for invalid user ID"
        );
    }
}

