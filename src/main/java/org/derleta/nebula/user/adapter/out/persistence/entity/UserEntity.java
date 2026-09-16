package org.derleta.nebula.user.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;
import org.hibernate.annotations.ColumnDefault;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"settings", "games", "achievements"})
@ToString()
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "email", nullable = false)
    @Email
    private String email;

    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 45)
    @NotNull
    @Column(name = "login", nullable = false, unique = true)
    @Size(min = 3, max = 45)
    private String login;

    @Column(name = "age")
    private Integer age;

    @Column(name = "birth_date")
    private Date birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private GenderJpaEntity gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationality_id")
    private NationalityJpaEntity nationality;

    @ColumnDefault("current_timestamp()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private UserSettingsEntity settings;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_games",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<GameEntity> games;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAchievementEntity> achievements;

    /**
     * Updates the `age` property of the user based on their `birthDate` property.
     * The method calculates the age in years by determining the difference
     * between the current date and the birthdate using ChronoUnit.YEARS.
     *
     * @throws IllegalArgumentException if the `birthDate` is null.
     */
    public void updateAge() {
        LocalDate birthDate = this.birthDate.toLocalDate();
        if (birthDate != null) {
            this.age = (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        } else {
            throw new IllegalArgumentException("Birthdate cannot be null!");
        }
    }

}
