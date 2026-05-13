package org.derleta.nebula.user.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettingsEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "general_id", nullable = false)
    private UserSettingsGeneralEntity general;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "sound_id", nullable = false)
    private UserSettingsSoundEntity sound;

}
