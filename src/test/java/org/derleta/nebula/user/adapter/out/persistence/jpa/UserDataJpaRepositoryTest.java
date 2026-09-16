package org.derleta.nebula.user.adapter.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.RegionJpaEntity;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDataJpaRepositoryTest {

    @SuppressWarnings("resource")
    @Container
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:11.8")
            .withDatabaseName("nebula_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
        registry.add("spring.datasource.driver-class-name", mariadb::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.jdbc.batch_size", () -> "20");
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserDataJpaRepository userRepository;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("INSERT INTO regions (id, name) VALUES (1, 'Europe')").executeUpdate();
        entityManager.createNativeQuery("INSERT INTO nationalities (id, name, code, region_id) VALUES (1, 'Poland', 'PL', 1)").executeUpdate();
        entityManager.createNativeQuery("INSERT INTO genders (id, name) VALUES (1, 'Male')").executeUpdate();

        GenderJpaEntity gender = entityManager.find(GenderJpaEntity.class, 1);
        NationalityJpaEntity nationality = entityManager.find(NationalityJpaEntity.class, 1);

        UserEntity user15 = new UserEntity();
        user15.setId(15L);
        user15.setLogin("user15");
        user15.setEmail("user15@example.com");
        user15.setUpdatedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        entityManager.persist(user15);

        UserEntity user1000 = new UserEntity();
        user1000.setId(1000L);
        user1000.setLogin("user1000");
        user1000.setEmail("user1000@example.com");
        user1000.setUpdatedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        user1000.setGender(gender);
        user1000.setNationality(nationality);
        entityManager.persist(user1000);

        entityManager.flush();
        entityManager.clear();
    }

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

