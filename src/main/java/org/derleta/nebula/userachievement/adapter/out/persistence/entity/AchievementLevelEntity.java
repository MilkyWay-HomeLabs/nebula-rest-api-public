package org.derleta.nebula.userachievement.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.id.AchievementLevelId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "achievement_levels")
public class AchievementLevelEntity {

    @EmbeddedId
    private AchievementLevelId id;

    @MapsId("achievementId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    private AchievementEntity achievement;

    @NotNull
    @Column(name = "value", nullable = false)
    private Integer value;

}
