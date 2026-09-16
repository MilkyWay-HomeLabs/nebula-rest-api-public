package org.derleta.nebula.user.adapter.out.persistence.entity.id;

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
public class UsersGameId implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 6231311682632816013L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersGameId entity = (UsersGameId) o;
        return Objects.equals(this.gameId, entity.gameId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, userId);
    }

}
