package org.derleta.nebula.user.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_settings_sound")
public class UserSettingsSoundEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long id;

    @ColumnDefault("0")
    @Column(name = "muted")
    private Boolean muted;

    @ColumnDefault("1")
    @Column(name = "battle_cry")
    private Boolean battleCry;

    @ColumnDefault("100")
    @Column(name = "volume_master")
    private Integer volumeMaster;

    @ColumnDefault("100")
    @Column(name = "volume_music")
    private Integer volumeMusic;

    @ColumnDefault("100")
    @Column(name = "volume_effects")
    private Integer volumeEffects;

    @ColumnDefault("100")
    @Column(name = "volume_voices")
    private Integer volumeVoices;

}
