package org.derleta.nebula.game.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    @Column(name = "name", length = 100, unique = true)
    private String name;

    @ColumnDefault("0")
    @Column(name = "enable")
    private Boolean enable;

    @Size(max = 255)
    @Column(name = "icon_url")
    private String iconUrl;

    @Size(max = 255)
    @Column(name = "page_url")
    private String pageUrl;

}
