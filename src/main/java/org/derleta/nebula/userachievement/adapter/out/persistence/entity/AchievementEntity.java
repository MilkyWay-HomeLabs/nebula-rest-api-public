package org.derleta.nebula.userachievement.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"levels"})
@EqualsAndHashCode(exclude = {"levels"})
@Entity
@Table(name = "achievements")
public class AchievementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "min_value")
    private Integer minValue;

    @Column(name = "max_value")
    private Integer maxValue;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "icon_url")
    private String iconUrl;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "achievement_id", insertable = false, updatable = false)
    List<AchievementLevelEntity> levels;

}
