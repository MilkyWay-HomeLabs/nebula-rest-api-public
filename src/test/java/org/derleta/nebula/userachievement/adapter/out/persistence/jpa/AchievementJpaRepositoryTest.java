package org.derleta.nebula.userachievement.adapter.out.persistence.jpa;

import jakarta.transaction.Transactional;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.AchievementEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AchievementJpaRepositoryTest {

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
    private AchievementJpaRepository achievementRepository;

    @Test
    void save_validAchievement_shouldPersist() {
        AchievementEntity achievement = new AchievementEntity();
        achievement.setName("Test Achievement");
        achievement.setMinValue(1);
        achievement.setMaxValue(100);
        achievement.setIconUrl("https://example.com/icon.png");

        AchievementEntity savedAchievement = achievementRepository.save(achievement);

        assertNotNull(savedAchievement);
        assertNotNull(savedAchievement.getId(), "ID should be generated");
        assertEquals("Test Achievement", savedAchievement.getName());
    }

    @Test
    void findById_existingId_shouldReturnAchievement() {
        AchievementEntity saved = achievementRepository.save(new AchievementEntity());
        Optional<AchievementEntity> result = achievementRepository.findById(saved.getId());
        assertTrue(result.isPresent());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        Optional<AchievementEntity> result = achievementRepository.findById(9999);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllAchievements() {
        achievementRepository.save(new AchievementEntity());
        achievementRepository.save(new AchievementEntity());
        List<AchievementEntity> results = achievementRepository.findAll();
        assertTrue(results.size() >= 2);
    }

    @Test
    void delete_existingAchievement_shouldRemove() {
        AchievementEntity saved = achievementRepository.save(new AchievementEntity());
        Integer id = saved.getId();
        achievementRepository.deleteById(id);
        assertTrue(achievementRepository.findById(id).isEmpty());
    }
}

