package org.derleta.nebula.user.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_settings_general")
public class UserSettingsGeneralEntity {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private ThemeEntity theme;

}
