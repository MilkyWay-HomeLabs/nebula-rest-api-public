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
public class UserAchievementId implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = -1607277085176415307L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "achievement_id", nullable = false)
    private Integer achievementId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAchievementId entity = (UserAchievementId) o;
        return Objects.equals(this.achievementId, entity.achievementId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(achievementId, userId);
    }

}
