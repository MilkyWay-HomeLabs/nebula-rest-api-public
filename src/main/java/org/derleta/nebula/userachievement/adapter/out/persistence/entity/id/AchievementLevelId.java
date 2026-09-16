package org.derleta.nebula.userachievement.adapter.out.persistence.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class AchievementLevelId implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = -7858982344622404815L;
    @NotNull
    @Column(name = "achievement_id", nullable = false)
    private Integer achievementId;

    @NotNull
    @Column(name = "level", nullable = false)
    private Integer level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AchievementLevelId entity = (AchievementLevelId) o;
        return Objects.equals(this.level, entity.level) &&
                Objects.equals(this.achievementId, entity.achievementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, achievementId);
    }

}
