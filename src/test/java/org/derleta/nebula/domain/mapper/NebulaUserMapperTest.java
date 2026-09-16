package org.derleta.nebula.domain.mapper;

import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.derleta.nebula.game.adapter.out.persistence.entity.*;
import org.derleta.nebula.theme.adapter.out.persistence.entity.*;
import org.derleta.nebula.user.adapter.out.persistence.entity.*;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.*;
import org.derleta.nebula.game.domain.model.*;
import org.derleta.nebula.theme.domain.model.*;
import org.derleta.nebula.user.domain.model.*;
import org.derleta.nebula.userachievement.domain.model.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class NebulaUserMapperTest {

//    @Test
//    void toUser_validEntity_returnsNebulaUser() {
//        // Arrange
//        UserEntity entity = new UserEntity();
//        entity.setId(1000L);
//        entity.setLogin("testuser");
//        entity.setEmail("test@example.com");
//        entity.setFirstName("John");
//        entity.setLastName("Doe");
//        entity.setAge(30);
//        entity.setBirthDate(Date.valueOf(LocalDate.of(1993, 1, 1)));
//
//        GenderEntity genderEntity = new GenderEntity();
//        genderEntity.setId(1);
//        genderEntity.setName("Male");
//        entity.setGender(genderEntity);
//
//        RegionEntity regionEntity = new RegionEntity();
//        regionEntity.setId(1);
//        regionEntity.setName("Europe");
//
//        NationalityEntity nationalityEntity = new NationalityEntity();
//        nationalityEntity.setId(1);
//        nationalityEntity.setName("Polish");
//        nationalityEntity.setCode("PL");
//        nationalityEntity.setRegion(regionEntity);
//        entity.setNationality(nationalityEntity);
//
//        UserSettingsEntity settingsEntity = new UserSettingsEntity();
//        entity.setSettings(settingsEntity);
//
//        GameEntity gameEntity1 = new GameEntity();
//        gameEntity1.setId(1);
//        gameEntity1.setName("Game 1");
//
//        GameEntity gameEntity2 = new GameEntity();
//        gameEntity2.setId(2);
//        gameEntity2.setName("Game 2");
//
//        List<GameEntity> gameEntities = Arrays.asList(gameEntity1, gameEntity2);
//        entity.setGames(gameEntities);
//
//        List<UserAchievementEntity> achievementEntities = Collections.emptyList();
//        entity.setAchievements(achievementEntities);
//
//        Gender gender = new Gender(1, "Male");
//        Region region = new Region(1, "Europe");
//        Nationality nationality = new Nationality(1, "Polish", "PL", region);
//        UserSettings settings = new UserSettings(1000L, null, null);
//        List<Game> games = Arrays.asList(
//                new Game(1, "Game 1", true, null, null),
//                new Game(2, "Game 2", true, null, null)
//        );
//        List<UserAchievement> achievements = Collections.emptyList();
//
//        try (MockedStatic<GenderMapper> genderMapperMock = Mockito.mockStatic(GenderMapper.class);
//             MockedStatic<NationalityMapper> nationalityMapperMock = Mockito.mockStatic(NationalityMapper.class);
//             MockedStatic<UserSettingsMapper> settingsMapperMock = Mockito.mockStatic(UserSettingsMapper.class);
//             MockedStatic<GameMapper> gameMapperMock = Mockito.mockStatic(GameMapper.class);
//             MockedStatic<UserAchievementMapper> achievementMapperMock = Mockito.mockStatic(UserAchievementMapper.class)) {
//
//            // Mock the static method calls
//            genderMapperMock.when(() -> GenderMapper.toGender(any()))
//                    .thenReturn(gender);
//            nationalityMapperMock.when(() -> NationalityMapper.toNationality(any()))
//                    .thenReturn(nationality);
//            settingsMapperMock.when(() -> UserSettingsMapper.toSetting(any()))
//                    .thenReturn(settings);
//            gameMapperMock.when(() -> GameMapper.toGames(any()))
//                    .thenReturn(games);
//            achievementMapperMock.when(() -> UserAchievementMapper.toUserAchievements(any()))
//                    .thenReturn(achievements);
//
//            // Act
//            NebulaUser result = NebulaUserMapper.toUser(entity);
//
//            // Assert
//            assertNotNull(result);
//            assertEquals(1000L, result.id());
//            assertEquals("testuser", result.login());
//            assertEquals("test@example.com", result.email());
//            assertEquals("John", result.firstName());
//            assertEquals("Doe", result.lastName());
//            assertEquals(30, result.age());
//            assertEquals(Date.valueOf(LocalDate.of(1993, 1, 1)), result.birthDate());
//            assertEquals(gender, result.gender());
//            assertEquals(nationality, result.nationality());
//            assertEquals(settings, result.settings());
//            assertEquals(games, result.games());
//            assertEquals(achievements, result.achievements());
//
//            // Verify that the mapper methods were called with the correct arguments
//            genderMapperMock.verify(() -> GenderMapper.toGender(entity.getGender()));
//            nationalityMapperMock.verify(() -> NationalityMapper.toNationality(entity.getNationality()));
//            settingsMapperMock.verify(() -> UserSettingsMapper.toSetting(entity.getSettings()));
//            gameMapperMock.verify(() -> GameMapper.toGames(entity.getGames()));
//            achievementMapperMock.verify(() -> UserAchievementMapper.toUserAchievements(entity.getAchievements()));
//        }
//    }
//
//    @Test
//    void toUser_nullEntity_throwsNullPointerException() {
//        // Act & Assert
//        assertThrows(
//                NullPointerException.class,
//                () -> NebulaUserMapper.toUser(null)
//        );
//    }
//
//    @Test
//    void toUser_entityWithNullFields_returnsNebulaUserWithNullFields() {
//        // Arrange
//        UserEntity entity = new UserEntity();
//        entity.setId(1000L);
//
//        try (MockedStatic<GenderMapper> genderMapperMock = Mockito.mockStatic(GenderMapper.class);
//             MockedStatic<NationalityMapper> nationalityMapperMock = Mockito.mockStatic(NationalityMapper.class);
//             MockedStatic<UserSettingsMapper> settingsMapperMock = Mockito.mockStatic(UserSettingsMapper.class);
//             MockedStatic<GameMapper> gameMapperMock = Mockito.mockStatic(GameMapper.class);
//             MockedStatic<UserAchievementMapper> achievementMapperMock = Mockito.mockStatic(UserAchievementMapper.class)) {
//
//            // Mock the static method calls
//            genderMapperMock.when(() -> GenderMapper.toGender(any()))
//                    .thenReturn(null);
//            nationalityMapperMock.when(() -> NationalityMapper.toNationality(any()))
//                    .thenReturn(null);
//            settingsMapperMock.when(() -> UserSettingsMapper.toSetting(any()))
//                    .thenReturn(null);
//            gameMapperMock.when(() -> GameMapper.toGames(any()))
//                    .thenReturn(null);
//            achievementMapperMock.when(() -> UserAchievementMapper.toUserAchievements(any()))
//                    .thenReturn(null);
//
//            // Act
//            NebulaUser result = NebulaUserMapper.toUser(entity);
//
//            // Assert
//            assertNotNull(result);
//            assertEquals(1000L, result.id());
//            assertNull(result.login());
//            assertNull(result.email());
//            assertNull(result.firstName());
//            assertNull(result.lastName());
//            assertEquals(0, result.age());
//            assertNull(result.birthDate());
//            assertNull(result.gender());
//            assertNull(result.nationality());
//            assertNull(result.settings());
//            assertNull(result.games());
//            assertNull(result.achievements());
//
//            // Verify that the mapper methods were called with the correct arguments
//            genderMapperMock.verify(() -> GenderMapper.toGender(null));
//            nationalityMapperMock.verify(() -> NationalityMapper.toNationality(null));
//            settingsMapperMock.verify(() -> UserSettingsMapper.toSetting(null));
//            gameMapperMock.verify(() -> GameMapper.toGames(null));
//            achievementMapperMock.verify(() -> UserAchievementMapper.toUserAchievements(null));
//        }
//    }

}
