package org.derleta.nebula.user.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;

import java.sql.Date;

@Repository
public interface UserDataJpaRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Must be annotated with @Transactional, in ex. on @Service methods
     * used to set an actual timestamp where user data (also in other tables) was edited
     *
     * @param userId user id who data was updated
     */
    @Modifying
    @Query(value = """
            UPDATE users SET updated_at = CURRENT_TIMESTAMP
            WHERE id = :userId
            """, nativeQuery = true)
    void updateUserUpdatedAt(@Param("userId") long userId);

    /**
     * Updates the details of a user in the database, including first name, last name, gender, nationality, birthdate,
     * and the updated timestamp.
     *
     * @param userId        the ID of the user whose details are being updated
     * @param firstName     the new first name of the user
     * @param lastName      the new last name of the user
     * @param birthdate     the new birthdate of the user
     * @param nationalityId the ID representing the new nationality of the user
     * @param genderId      the ID representing the new gender of the user
     */
    @Modifying
    @Query(value = """
            UPDATE users u SET u.first_name = :firstName, u.last_name = :lastName,
                        u.gender_id = :genderId, u.nationality_id = :nationalityId,
                        u.birth_date = :birthdate, u.updated_at = CURRENT_TIMESTAMP
            WHERE id = :userId
            """, nativeQuery = true)
    void updateUserDetails(@Param("userId") long userId,
                           @Param("firstName") String firstName,
                           @Param("lastName") String lastName,
                           @Param("birthdate") Date birthdate,
                           @Param("nationalityId") int nationalityId,
                           @Param("genderId") int genderId);

}

